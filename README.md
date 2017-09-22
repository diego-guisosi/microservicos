# Cadastro de Campanhas e Clientes
Este repositório possui a implementação de um cadastro de Campanhas e Clientes desenvolvido com Spring Boot, Spring Data JPA, Base H2 em memória, Tomcat, REST, AMQP RabbitMQ e alguns serviços do Spring Cloud.
# Premissas
Antes de descrever a arquitetura e os frameworks que utilizei durante o desenvolvimento, gostaria de esclarecer que assumi algumas premissas:
1. Disponibilidade de um broker para troca de mensagens entre os módulos (RabbitMQ rodando em localhost na porta padrão)
2. Assumi que a maior quantidade de solicitações concorrentes que os serviços precisarão suportar são de consulta. Estou citando isso, pois há um serviço de alteração da data final de vigência das campanhas, que, se for muito utilizado, pode exigir a implementação de lock distribuído entre as instâncias do microserviço de campanhas ou talvez algum solução mais rebuscada do que foi proposta.
3. Adotei a menor quantidade de padrões possíveis, de modo que só utilizasse os padrões de arquitetura necessários para atender aos requisitos do projeto.
# Arquitetura de Microserviços
Em função do enunciado de cada história e dos requisitos não funcionais apresentados, optei por desenvolver os módulos da aplicação com base em arquitetura de microserviços, uma vez que precisaria prover uma infraestrutura que garantisse acesso acentuado aos serviços, notificação para sincronização de informações entre os componentes e tratamento de tolerância a falha. Aleḿ disso, pela descrição das histórias, há a possibilidade de uma futura necessidade de escalar os serviços.
A solução é composta pelos serviços discovery-service, campanha-service e cliente-service, cada um rodando com um Tomcat embarcado, base de dados H2 em memória nos serviços campanha-service e cliente-service e RabbitMQ com tópico configurado para sincronização das informações do banco H2 entre os serviços.
## discovery-service
Serviço responsável por prover uma estrutura de registro dos demais serviços, para facilitar a localização dos recursos e diminuir a interdependência entre eles. Para implementar este padrão, utilizei o Spring Cloud Eureka (Netflix). É necessário subir este projeto antes dos demais.
## campanha-service
Serviço responsável pelo cadastro das campanhas e associação entre campanhas e clientes. Foi desenvolvido como um client do Eureka. Expõe serviços REST para o cadastro e faz uso de tópicos do RabbitMQ para notificar alterações da cadastro para o serviço de cadastro de clientes.
## cliente-service
Serviço responsável pelo cadastro dos clientes. Além de se registrar como um client do Eureka, este serviço também utiliza os recursos disponibilizados pelo Spring Cloud Feign para geração de client REST para consumo dos serviços de cadastro e associação de campanhas, balanceamento de carga das requisiçoes e tratamento de fallback para a possibilidade do campanha-service estar indisponível.
## H2 em memória
Base de dados dos microserviços campanha-service e cliente-service, rodando com configurações default. Utiliza o pool de datasources do Tomcat para gerenciamento das conexões, que, nos valores default, suportam até 100 conexões de banco ao mesmo tempo. Este número já parace ser suficiente para atender ao requisito de não funcional de requisições por segundo.
## Tomcat embarcado
Todos os serviços rodam com o Tomcat em suas configurações padrões. Para os serviços de cadastro de clientes e cadastro de campanhas, que têm como requisito o processamento de até 100 requisições por segundo, as configurações padrões atendem (maxThreads têm o valor default de 200).  
