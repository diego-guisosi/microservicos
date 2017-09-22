# Cadastro de Campanhas e Clientes
Este repositório possui a implementação de um cadastro de Campanhas e Clientes desenvolvido com Spring Boot, Spring Data JPA, Base H2 em memória, Tomcat, REST, AMQP RabbitMQ e alguns serviços do Spring Cloud. Além disso, possui também os fontes de um projeto criado para procesamento de streams.
# Premissas
Antes de descrever a arquitetura e os frameworks que utilizei durante o desenvolvimento, gostaria de esclarecer que assumi algumas premissas:
1. Disponibilidade de um broker para troca de mensagens entre os módulos (RabbitMQ rodando em localhost na porta padrão)
2. Assumi que a maior quantidade de solicitações concorrentes que os serviços precisarão suportar são de consulta. Estou citando isso, pois há um serviço de alteração da data final de vigência das campanhas, que, se for muito utilizado, pode exigir a implementação de lock distribuído entre as instâncias do microserviço de campanhas ou talvez algum solução mais rebuscada do que foi proposta.
3. Adotei a menor quantidade de padrões possíveis, de modo que só utilizasse os padrões de arquitetura necessários para atender aos requisitos do projeto.
# Arquitetura de Microserviços
Em função do enunciado de cada história e dos requisitos não funcionais apresentados, optei por desenvolver os módulos da aplicação com base em arquitetura de microserviços, uma vez que precisaria prover uma infraestrutura que garantisse acesso acentuado aos serviços, notificação para sincronização de informações entre os componentes e tratamento de tolerância a falha. Aleḿ disso, pela descrição das histórias, há a possibilidade de uma futura necessidade de escalar os serviços.
A solução é composta pelos serviços discovery-service, campanha-service e cliente-service, cada um rodando com um Tomcat embarcado, base de dados H2 em memória nos serviços campanha-service e cliente-service e RabbitMQ com tópico configurado para sincronização das informações do banco H2 entre os serviços.
# Serviços e Módulos
* ## discovery-service
Serviço responsável por prover uma estrutura de registro dos demais serviços, para facilitar a localização dos recursos e diminuir a interdependência entre eles. Para implementar este padrão, utilizei o Spring Cloud Eureka (Netflix). É necessário subir este projeto antes dos demais.
* ## campanha-service
Serviço responsável pelo cadastro das campanhas e associação entre campanhas e clientes. Foi desenvolvido como um client do Eureka. Expõe serviços REST para o cadastro e faz uso de tópicos do RabbitMQ para notificar alterações da cadastro para o serviço de cadastro de clientes.
* ## cliente-service
Serviço responsável pelo cadastro dos clientes. Além de se registrar como um client do Eureka, este serviço também utiliza os recursos disponibilizados pelo Spring Cloud Feign para geração de client REST para consumo dos serviços de cadastro e associação de campanhas, balanceamento de carga das requisiçoes e tratamento de fallback para a possibilidade do campanha-service estar indisponível.
* ## H2 em memória
Base de dados dos microserviços campanha-service e cliente-service, rodando com configurações default. Utiliza o pool de datasources do Tomcat para gerenciamento das conexões, que, nos valores default, suportam até 100 conexões de banco ao mesmo tempo. Este número já parace ser suficiente para atender ao requisito de não funcional de requisições por segundo.
* ## Tomcat embarcado
Todos os serviços rodam com o Tomcat em suas configurações padrões. Para os serviços de cadastro de clientes e cadastro de campanhas, que têm como requisito o processamento de até 100 requisições por segundo, as configurações padrões atendem (maxThreads têm o valor default de 200).  
* ## RabbitMQ
Broker OpenSource utilizado para troca de mensagens entre os serviços, com o intuito de sincronizar as bases de dados dos microserviços. Para integrar os serviços com o broker, utilizei Spring AMQP nos módulos campanha-service (sender) e cliente-service (listener). Configurei um tópico "campanha" com routing key para duas filas: uma de cadastro de campanha e outra para alteração de campanha. O módulo cliente-service se inscreve no tópico de campanhas, fazendo bind com as duas routing keys, recebendo assim mensagens de quando as campanhas são alteradas ou novas campanhas são cadastradas.
# Resposta da Questão 04 - Deadlocks
Deadlock é um problema de travamento de threads, que ocorre em cenário concorrente, devido a impossibilidade de obtenção do lock de determinado objeto.
Um cenário típico em que isso ocorre: quando uma thread depende do lock de mais do que um objeto para seguir execução, mas uma segunda thread não libera o lock de um dos objetos que a primeira thread depende, pois essa segunda thread também depende da obtenção do lock de um objeto que a primeira thread não libera.
Exemplificação do cenário descrito anteriormente, considerando a existência das threads T1 e T2 e dos objetos O1 e O2.
1. T1 adquire o lock de O1
2. T2 adquire o lock de O2
3. T1 tenta adquirir o lock de O2, mas não tem sucesso, pois T2 já possui o lock deste objeto
4. T2 tenta adquirir o lock de O1, mas não tem sucesso, pois T1 já possui o lock deste objeto
5. Neste ponto, ambas as threads entram em deadlock, pois uma depende da obtenção do lock de um objeto que a outra thread possui.

O cenário anterior poderia ter sido evitado se os locks dos objetos fossem obtidos sempre na mesma ordem:

1. T1 adquire o lock de O1
2. T2 tenta obter o lock de O1, mas entra em espera, pois T1 já possui o lock de O1
3. T1 adquire o lock de O2
4. T1 realiza o processamento necessário
5. T1 libera o lock de O2
6. T1 libera o lock de O1
7. T2 adquire sai da espera e adquire o lock de O1
8. T2 adquire o lock de O2
9. T2 realiza o processamento necessário
10. T2 libera o lock de O2
11. T2 libera o lock de O1

# Resposta da Questão 05 - Comparativo entre Streams e Parallel Streams
Tanto a Stream quanto o Parallel Stream tem a finalidade de possibilitar o uso declarativo das coleções do Java, em vez de iterativo. 
A diferença básica entre as duas é que a Stream é processada serialmente, enquanto a Parallel Stream é subdividida em múltiplas Streams pelo Fork/Join Framework, que então são processadas paralelamente (se não houver interferência de alguma operação de agregação ou redução).
Deve-se utilizar Parallel Stream com cautela, pois nem sempre é mais rápido processar a coleção em paralelo do que serial. O custo para o JVM de gerenciar as threads pode ser maior do que o de processar a coleção serialmente. Além disso, se a aplicação estiver rodando em ambiente Multi-Thread (como um web container, por exemplo), o Parallel Stream adicionará uma segunda camada de paralelismo.
