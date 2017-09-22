# Cadastro de Campanhas e Clientes
Este repositório possui a implementação de um cadastro de Campanhas e Clientes desenvolvido com Spring Boot, Spring Data, Base H2 em memória, Tomcat, REST, AMQP RabbitMQ e alguns serviços do Spring Cloud.
# Premissas
Antes de descrever a arquitetura e os frameworks que utilizei durante o desenvolvimento, gostaria de esclarecer que assumi algumas premissas:
1. Disponibilidade de um broker para troca de mensagens entre os módulos (RabbitMQ rodando em localhost na porta padrão)
2. Assumi que a maior quantidade de solicitações concorrentes que os serviços precisarão suportar são de consulta. Estou citando isso, pois há um serviço de alteração da data final de vigência das campanhas, que, se for muito utilizado, pode exigir a implementação de lock distribuído entre as instâncias do microserviço de campanhas.
3. Adotei a menor quantidade de padrões possíveis, de modo que só utilizasse os padrões de arquitetura necessários para atender aos requisitos do projeto.
# Arquitetura de Microserviços
Em função do enunciado de cada história e dos requisitos não funcionais apresentados, optei por desenvolver os módulos da aplicação com base em arquitetura de microserviços, uma vez que precisaria prover uma infraestrutura que garantisse acesso acentuado aos serviços, notificação para sincronização de informações entre os componentes e tratamento de tolerância a falha. Aleḿ disso, pela descrição das histórias, há a possibilidade de uma futura necessidade de escalar os serviços.
A solução é composta por três serviços: discovery-service, campanha-service e cliente-service
## discovery-service
Serviço responsável por prover uma estrutura de registro dos demais serviços, para facilitar a localização dos recursos e diminuir a interdependência entre eles. Para implementar este padrão, utilizei o Spring Cloud Eureka (Netflix). É necessário subir este projeto antes dos demais.
## campanha-service
Serviço responsável pelo cadastro das campanhas e associação entre campanhas e clientes. Foi desenvolvido como um client do Eureka. Expõe serviços REST para o cadastro e faz uso de tópicos do RabbitMQ para notificar alterações da cadastro para o serviço de cadastro de clientes.
## cliente-service
