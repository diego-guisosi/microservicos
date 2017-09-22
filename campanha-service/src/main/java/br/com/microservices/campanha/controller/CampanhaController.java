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
import br.com.microservices.campanha.model.Campanha;
import br.com.microservices.campanha.service.CampanhaService;


@RestController
@RequestMapping("/campanha")
public class CampanhaController {
	
	private static Logger log = Logger.getLogger(CampanhaController.class);
	
	@Autowired
	private CampanhaService campanhaService;
	
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Campanha> getCampanha(@PathVariable("id") Long id){
		ResponseEntity<Campanha> response = null;
		try {
			log.info("Consultando campanha não vencida com id=" + id);
			Campanha campanha = campanhaService.consultarCampanhaNaoVencida(id);
			response = new ResponseEntity<Campanha>(campanha,HttpStatus.OK);
			log.info("Campanha com id " + id + " consultada com sucesso");
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<Campanha>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<Campanha>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteCampanha(@PathVariable("id") Long id){
		ResponseEntity<Void> response = null;
		try {
			log.info("Deletando campanha com id=" + id);
			campanhaService.delete(id);
			response = new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
			log.info("Campanha com id " + id + " deletada com sucesso");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Campanha>> getCampanhas(@RequestParam("idTimeCoracao") Long idTimeCoracao){
		ResponseEntity<List<Campanha>> response = null;
		try {
			log.info("Consultando campanhas não vencidas do time do coração " + idTimeCoracao);
			List<Campanha> campanhas = campanhaService.consultarCampanhasNaoVencidas(idTimeCoracao);
			response = new ResponseEntity<List<Campanha>>(campanhas,HttpStatus.OK);
			log.info("Campanhas não vencidas do time do coração consultadas com sucesso: " + campanhas);
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<List<Campanha>>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response =  new ResponseEntity<List<Campanha>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Campanha> saveCampanha(@RequestBody Campanha campanha){
		ResponseEntity<Campanha> response = null;
		try {
			log.info("Salvando campanha " + campanha);
			Campanha campanhaSalva = campanhaService.cadastrar(campanha);
			response = new ResponseEntity<Campanha>(campanhaSalva, HttpStatus.CREATED);
			log.info(campanhaSalva + " salva com sucesso");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = new ResponseEntity<Campanha>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Campanha> updateCampanha(@RequestBody Campanha campanha){
		ResponseEntity<Campanha> response = null;
		try {
			log.info("Atualizando a campanha " + campanha);
			Campanha campanhaAtualizada = campanhaService.cadastrar(campanha);
			response = new ResponseEntity<Campanha>(campanhaAtualizada,HttpStatus.OK);
			log.info("Campanha " + campanha + " atualizada com sucesso");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = new ResponseEntity<Campanha>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}
