package br.com.microservices.campanha.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.microservices.campanha.exception.CampanhaNaoEncontradaException;
import br.com.microservices.campanha.exception.TodasCampanhasJaAssociadasException;
import br.com.microservices.campanha.model.AssociacaoClienteCampanha;
import br.com.microservices.campanha.model.Campanha;
import br.com.microservices.campanha.service.AssociacaoClienteCampanhaService;

@RestController
@RequestMapping("/associacaoCampanha")
public class AssociacaoClienteCampanhaController {
	
	private static Logger log = Logger.getLogger(AssociacaoClienteCampanhaController.class);
	
	@Autowired
	private AssociacaoClienteCampanhaService associacaoService;
	
	@RequestMapping(path="/{clienteId}", method = RequestMethod.GET)
	public ResponseEntity<List<Campanha>> consultarCampanhas(@PathVariable("clienteId") Long clienteId){
		ResponseEntity<List<Campanha>> response = null;
		try {
			log.info(String.format("Consultando campanhas associadas para o clienteId=%d", clienteId));
			List<Campanha> campanhasDoCliente = associacaoService.consultarCampanhasDoCliente(clienteId);
			response = new ResponseEntity<List<Campanha>>(campanhasDoCliente,HttpStatus.OK);
			log.info(String.format("Consulta de campanhas associadas realizada com sucesso: " + campanhasDoCliente));
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<List<Campanha>>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<List<Campanha>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Campanha>> consultarCampanhasNaoAssociadas(@RequestParam("clienteId") Long clienteId, @RequestParam("idTimeCoracao") Long idTimeCoracao){
		ResponseEntity<List<Campanha>> response = null;
		try {
			log.info(String.format("Consultando campanhas não associadas para clienteId=%d e idTimeCoracao=%d", clienteId, idTimeCoracao));
			List<Campanha> campanhasNaoAssociadas = associacaoService.consultarCampanhasNaoAssociadas(clienteId,idTimeCoracao);
			response = new ResponseEntity<List<Campanha>>(campanhasNaoAssociadas,HttpStatus.OK);
			log.info(String.format("Consulta de campanhas não associadas realizada com sucesso: " + campanhasNaoAssociadas));
		} catch (CampanhaNaoEncontradaException | TodasCampanhasJaAssociadasException e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<List<Campanha>>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<List<Campanha>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<AssociacaoClienteCampanha> associarClienteCampanha(@RequestBody AssociacaoClienteCampanha associacao){
		ResponseEntity<AssociacaoClienteCampanha> response = null;
		try {
			log.info("Associando cliente e campanha " + associacao);
			AssociacaoClienteCampanha associacaoSalva = associacaoService.cadastrar(associacao);
			response = new ResponseEntity<AssociacaoClienteCampanha>(associacaoSalva, HttpStatus.CREATED);
			log.info(associacaoSalva + "realizada com sucesso");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = new ResponseEntity<AssociacaoClienteCampanha>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}
