package br.com.microservices.campanha.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.microservices.campanha.exception.CampanhaNaoEncontradaException;
import br.com.microservices.campanha.exception.TodasCampanhasJaAssociadasException;
import br.com.microservices.campanha.model.AssociacaoClienteCampanha;
import br.com.microservices.campanha.model.Campanha;
import br.com.microservices.campanha.repository.AssociacaoClienteCampanhaRepository;

@Service
public class AssociacaoClienteCampanhaService {
	
	private static Logger log = Logger.getLogger(AssociacaoClienteCampanhaService.class);
	
	@Autowired
	private AssociacaoClienteCampanhaRepository repository;
	
	@Autowired
	private CampanhaService campanhaService;
	
	/**
	 * Método consulta as campanhas associadas para o cliente informado.
	 * @param clienteId Long representando o identificador do cliente cujas campanhas devem ser retornadas
	 * @return Lista de campanhas associadas
	 * @throws CampanhaNaoEncontradaException - Se nenhuma campanha estiver associada para o cliente
	 */
	public List<Campanha> consultarCampanhasDoCliente(Long clienteId) throws CampanhaNaoEncontradaException{
		List<Campanha> campanhasDoCliente = repository.findCampanhaByClienteId(clienteId);
		
		if(campanhasDoCliente == null || campanhasDoCliente.isEmpty()) {
			throw new CampanhaNaoEncontradaException(String.format("Campanhas não encontradas para o cliente %d", clienteId));
		}
		
		return campanhasDoCliente;
	}
	
	/**
	 * Método consulta as campanhas ainda não associadas para o cliente informado
	 * @param clienteId Long representando o identificador do cliente
	 * @param idTimeCoracao Long representando o identificador do time do coração
	 * @return Lista de campanhas do time do coração do cliente que ainda não estão associadas para ele
	 * @throws CampanhaNaoEncontradaException Se o cliente não possuir nenhuma campanha associada e não houver campanhas ativas para o time do coração do cliente
	 * @throws TodasCampanhasJaAssociadasException Se todas as campanhas do time do coração do cliente já estiverem associadas
	 */
	public List<Campanha> consultarCampanhasNaoAssociadas(Long clienteId, Long idTimeCoracao) throws CampanhaNaoEncontradaException, TodasCampanhasJaAssociadasException {
		
		List<Campanha> campanhasNaoAssociadas = null;
		
		try {
			List<Campanha> campanhasAssociadas = consultarCampanhasDoCliente(clienteId);
			campanhasNaoAssociadas = campanhaService.consultarCampanhasDoTimeDoCoracaoNaoAssociadas(idTimeCoracao, campanhasAssociadas);
		} catch (CampanhaNaoEncontradaException e) {
			log.warn(String.format("Cliente %d não possui campanhas associadas", clienteId));
			campanhasNaoAssociadas = campanhaService.consultarCampanhasNaoVencidas(idTimeCoracao);
		}
		
		return campanhasNaoAssociadas;
	}
	
	/**
	 * Método responsável por realizar a associação do cliente com a campanha e persistir a informação na base de dados.
	 * @param associacao AssociacaoClienteCampanha a ser persistida
	 * @return AssociacaoClienteCampanha persistida na base de dados.
	 */
	public AssociacaoClienteCampanha cadastrar(AssociacaoClienteCampanha associacao) {
		return repository.save(associacao);
	}
	
	/**
	 * Delete a associação representada pelo identificador informado.
	 * @param id Long representando o identificador da campanha.
	 */
	public void delete(Long id) {
		repository.delete(id);
	}

}
