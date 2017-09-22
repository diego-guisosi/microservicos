package br.com.microservices.cliente.listener;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.microservices.cliente.model.Campanha;
import br.com.microservices.cliente.repository.CampanhaRepository;

@Component
public class CampanhaListener {
	
	private static Logger log = Logger.getLogger(CampanhaListener.class);
	
	@Autowired
	private CampanhaRepository repository;
	
	@RabbitListener(queues = "#{cadastroQueue.name}")
	public void getCadastroCampanhaMessage(String campanhaMensagem) {
		enviaParaRepositorio(campanhaMensagem, "CADASTRO");
	}
	
	@RabbitListener(queues = "#{atualizacaoQueue.name}")
	public void getAtualizacaoCampanhaMessage(String campanhaMensagem) {
		enviaParaRepositorio(campanhaMensagem, "ATUALIZACAO");
	}
	
	private void enviaParaRepositorio(String campanhaMensagem, String operacao) {
		log.info(operacao + " - Mensagem de campanha recebida: " + campanhaMensagem);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		TypeReference<Campanha> mapType = new TypeReference<Campanha>() {};
		Campanha campanha = null;
		try {
			campanha = objectMapper.readValue(campanhaMensagem, mapType);
			repository.save(campanha);
			log.info(operacao + " - Campanha enviada para repositório: " + campanha);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
