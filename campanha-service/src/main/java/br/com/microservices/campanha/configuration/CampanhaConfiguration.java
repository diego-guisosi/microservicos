package br.com.microservices.campanha.configuration;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CampanhaConfiguration {
	
	@Value("${cliente.amqp.topic.databasesync}")
	private String databasesyncTopicName;
	
	@Bean
	public TopicExchange databasesyncTopic() {
		return new TopicExchange(databasesyncTopicName);
	}

}
