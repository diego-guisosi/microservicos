package br.com.microservices.cliente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.microservices.cliente.exception.ClienteNaoEncontradoException;
import br.com.microservices.cliente.model.Cliente;
import br.com.microservices.cliente.repository.ClienteRepository;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private AssociacaoCampanhaService associacaoCampanhaService;
	
	public Cliente consultar(Long clienteId) throws ClienteNaoEncontradoException {
		
		Cliente cliente = repository.findOne(clienteId);
		
		if(cliente == null) {
			throw new ClienteNaoEncontradoException(String.format("Cliente %d não encontrado", clienteId));
		}
		
		return cliente;
	}
	
	public Cliente cadastrar(Cliente cliente) {
		
		Cliente clienteCadastro = repository.findByEmail(cliente.getEmail());
		
		if(clienteCadastro != null) {
			associacaoCampanhaService.associarClienteExistente(clienteCadastro);
		} else {
			clienteCadastro = repository.save(cliente);
			associacaoCampanhaService.associarNovoCliente(clienteCadastro);
		}
		
		return clienteCadastro;
	}

}
