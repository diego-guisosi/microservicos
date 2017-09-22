# Cadastro de Campanhas e Clientes
Este repositório possui a implementação de um cadastro de Campanhas e Clientes desenvolvido com Spring Boot, Spring Data, Base H2 em memória, REST, AMQP RabbitMQ e alguns serviços do Spring Cloud.
# Arquitetura de Microserviços
Em função do enunciado de cada história e dos requisitos não funcionais apresentados, optei por desenvolver os módulos da aplicação com base em arquitetura de microserviços, uma vez que precisaria prover uma infraestrutura que garantisse acesso acentuado aos serviços, notificação para sincronização de informações entre os componentes e tratamento de tolerância a falha.
<br />
A solução é composta por três serviços: discovery-service, campanha-service e cliente-service
## discovery-service
Serviço responsável por prover uma estrutura de registro dos demais serviços, para facilitar a localização dos recursos e diminuir a interdependência entre eles. Para implementar este padrão, utilizei o Spring Cloud Eureka (Netflix). 
