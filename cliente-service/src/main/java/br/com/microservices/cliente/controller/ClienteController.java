package br.com.microservices.cliente.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.microservices.cliente.exception.ClienteNaoEncontradoException;
import br.com.microservices.cliente.model.Cliente;
import br.com.microservices.cliente.service.ClienteService;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
	
	private static Logger log = Logger.getLogger(ClienteController.class);
	
	@Autowired
	private ClienteService service;
	
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Cliente> getCliente(@PathVariable("id") Long clienteId){
		ResponseEntity<Cliente> response = null;
		try {
			log.info("Consultando cliente " + clienteId);
			Cliente clienteCadastrado = service.consultar(clienteId);
			response = new ResponseEntity<Cliente>(clienteCadastrado, HttpStatus.CREATED);
			log.info("Cliente cadastrado com sucesso: " + clienteCadastrado);
		} catch (ClienteNaoEncontradoException e) {
			log.error(e.getMessage(), e);
			response = new ResponseEntity<Cliente>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = new ResponseEntity<Cliente>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente){
		ResponseEntity<Cliente> response = null;
		try {
			log.info("Cadastrando cliente " + cliente);
			Cliente clienteCadastrado = service.cadastrar(cliente);
			response = new ResponseEntity<Cliente>(clienteCadastrado, HttpStatus.CREATED);
			log.info("Cliente cadastrado com sucesso: " + clienteCadastrado);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = new ResponseEntity<Cliente>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}
