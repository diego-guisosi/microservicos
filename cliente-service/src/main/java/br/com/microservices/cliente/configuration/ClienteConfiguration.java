package br.com.microservices.cliente.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClienteConfiguration {
	
	@Value("${cliente.amqp.topic.databasesync}")
	private String databasesyncTopicName;
	
	@Value("${cliente.amqp.topic.databasesync.routingkey.cadastro}")
	private String routingKeyCadastro;
	
	@Value("${cliente.amqp.topic.databasesync.routingkey.atualizacao}")
	private String routingKeyAtualizacao;
	
	@Value("${cliente.amqp.queue.campanha}")
	private String campanhaQueueName;
	
	@Bean
	public TopicExchange databasesyncTopic() {
		return new TopicExchange(databasesyncTopicName);
	}
	
	@Bean
    public Queue cadastroQueue() {
        return new Queue(campanhaQueueName);
    }
	
	@Bean
    public Queue atualizacaoQueue() {
        return new Queue(campanhaQueueName);
    }
	
	@Bean
    public Binding bindingCadastro(TopicExchange topic, Queue cadastroQueue) {
        return BindingBuilder.bind(cadastroQueue)
				             .to(topic)
				             .with(routingKeyCadastro);
    }
	
	@Bean
    public Binding bindingAtualizacao(TopicExchange topic, Queue atualizacaoQueue) {
        return BindingBuilder.bind(atualizacaoQueue)
				             .to(topic)
				             .with(routingKeyAtualizacao);
    }

}
