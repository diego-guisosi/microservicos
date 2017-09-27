package br.com.microservices.cliente.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import br.com.microservices.cliente.client.CampanhaServiceClient;
import br.com.microservices.cliente.model.AssociacaoClienteCampanha;
import br.com.microservices.cliente.model.Campanha;
import br.com.microservices.cliente.model.Cliente;
import br.com.microservices.cliente.repository.ClienteRepository;

@Service
public class AssociacaoCampanhaService {
	
	private static Logger log = Logger.getLogger(AssociacaoCampanhaService.class);
	
	//Anotado com Qualifier para compatibilidade com os testes
	@Autowired
	@Qualifier("br.com.microservices.cliente.client.CampanhaServiceClient")
	private CampanhaServiceClient client;
	
	@Autowired
	private ClienteRepository repository;
	
	/**
	 * Método associa para o novo cliente todas as campanhas do time do coração existentes e ativas.
	 * @param cliente Cliente para o qual as campanhas devem ser associadas
	 */
	public void associarNovoCliente(Cliente cliente) {
		log.info("Associando campanhas para novo cliente " + cliente);
		List<Campanha> campanhasParaAssociar = client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao());
		
		if(campanhasParaAssociar == null || campanhasParaAssociar.isEmpty()) {
			log.warn("Time do coração " + cliente.getIdTimeCoracao() + " não possui campanhas cadastradas");
			return;
		}
		
		campanhasParaAssociar.stream()
							 .forEach(campanha -> {
								  client.associarClienteCampanha(new AssociacaoClienteCampanha(cliente.getId(), campanha.getId()));
								  cliente.getCampanhas().add(campanha);
							  });
					
			repository.save(cliente);
	}
	
	/**
	 * Método associa para o cliente existente apenas as campanhas ainda não associadas
	 * @param cliente Cliente para o qual as campanhas devem ser associadas
	 */
	public void associarClienteExistente(Cliente cliente) {
		log.info("Associando campanhas para cliente existente " + cliente);
		List<Campanha> campanhasNaoAssociadas = client.getCampanhasNaoAssociadas(cliente.getId(), cliente.getIdTimeCoracao());
		
		if(campanhasNaoAssociadas == null || campanhasNaoAssociadas.isEmpty()) {
			log.warn("Cliente " + cliente + " não possui campanhas não associadas");
			return;
		}
		
		campanhasNaoAssociadas.stream()
							  .forEach(campanha -> {
								  client.associarClienteCampanha(new AssociacaoClienteCampanha(cliente.getId(), campanha.getId()));
								  cliente.getCampanhas().add(campanha);
							  });
		
		repository.save(cliente);
	}

}
