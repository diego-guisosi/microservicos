package br.com.microservices.campanha.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.microservices.campanha.model.Campanha;

@Service
public class NotificadorCampanhaService {
	
	private static Logger log = Logger.getLogger(NotificadorCampanhaService.class);
	
	@Value("${cliente.amqp.topic.databasesync.routingkey.cadastro}")
	private String routingKeyCadastro;
	
	@Value("${cliente.amqp.topic.databasesync.routingkey.atualizacao}")
	private String routingKeyAtualizacao;
	
	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	private TopicExchange databasesyncTopic;
	
	/**
	 * Método responsável por encaminhar para o tópico de alterações uma mensagem de alteração para a campanha cadastrada e
	 * assim, notificar os serviços interessados sobre o cadastro realizado.
	 * @param campanha
	 * @throws AmqpException
	 * @throws JsonProcessingException
	 */
	public void notificarCadastro(Campanha campanha) throws AmqpException, JsonProcessingException {
		notificar(campanha, routingKeyCadastro);
	}
	
	/**
	 * Método responsável por encaminhar para o tópico de alterações uma mensagem de alteração para cada campanha vigente recebida por parâmetro e,
	 * assim, notificar os serviços interessados sobre as alterações realizadas.
	 * @param campanhasAlteradas List<Campanha> contendo as campanhas com a data final de vigência alteradas
	 */
	public void notificarAlteracoes(List<Campanha> campanhasAlteradas) throws AmqpException, JsonProcessingException {
		for (Campanha campanha : campanhasAlteradas) {
			notificar(campanha, routingKeyAtualizacao);
		}
	}

	private String toJSON(Campanha campanha) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(campanha);
		log.debug("from " + campanha + " toJson " + json);
		return json;
	}
	
	private void notificar(Campanha campanha, String routingKey) throws AmqpException, JsonProcessingException {
		template.convertAndSend(databasesyncTopic.getName(), routingKey, toJSON(campanha));
	}

}
