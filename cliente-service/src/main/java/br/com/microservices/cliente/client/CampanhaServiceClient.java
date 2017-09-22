package br.com.microservices.cliente.client;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.microservices.cliente.client.CampanhaServiceClient.CampanhaClientFallback;
import br.com.microservices.cliente.model.AssociacaoClienteCampanha;
import br.com.microservices.cliente.model.Campanha;

@FeignClient(name = "campanha-service", fallback = CampanhaClientFallback.class)
public interface CampanhaServiceClient {
	
	@RequestMapping(method = RequestMethod.GET, value = "/campanha")
    List<Campanha> getCampanhasTimeCoracao(@RequestParam("idTimeCoracao") Long idTimeCoracao);
	
	@RequestMapping(method = RequestMethod.GET, value = "/associacaoCampanha")
    List<Campanha> getCampanhasNaoAssociadas(@RequestParam("clienteId") Long clienteId, @RequestParam("idTimeCoracao") Long idTimeCoracao);
	
	@RequestMapping(method = RequestMethod.POST, value = "/associacaoCampanha")
    AssociacaoClienteCampanha associarClienteCampanha(@RequestBody AssociacaoClienteCampanha associacao);
	
	@Component
	static class CampanhaClientFallback implements CampanhaServiceClient {
		
		private static Logger log = Logger.getLogger(CampanhaClientFallback.class);

		@Override
		public List<Campanha> getCampanhasTimeCoracao(Long idTimeCoracao) {
			log.warn("Fallback para campanha-service: Consulta de campanhas do time do coração indisponível");
			return null;
		}

		@Override
		public List<Campanha> getCampanhasNaoAssociadas(Long clienteId, Long idTimeCoracao) {
			log.warn("Fallback para campanha-service: Consulta de campanhas não associadas indisponível");
			return null;
		}

		@Override
		public AssociacaoClienteCampanha associarClienteCampanha(AssociacaoClienteCampanha associacao) {
			log.warn("Fallback para campanha-service: Associação de campanhas indisponível");
			return null;
		}
		
	}
	
}
