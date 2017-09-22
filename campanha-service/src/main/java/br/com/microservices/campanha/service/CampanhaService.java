package br.com.microservices.campanha.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.microservices.campanha.exception.CampanhaNaoEncontradaException;
import br.com.microservices.campanha.exception.NotificacaoException;
import br.com.microservices.campanha.exception.TodasCampanhasJaAssociadasException;
import br.com.microservices.campanha.model.Campanha;
import br.com.microservices.campanha.repository.CampanhaRepository;
import br.com.microservices.campanha.util.DateUtil;

@Service
public class CampanhaService {
	
	private static Logger log = Logger.getLogger(CampanhaService.class);
	
	@Autowired
	private CampanhaRepository repository;
	
	@Autowired
	private NotificadorCampanhaService notificadorCampanhaService;
	
	public List<Campanha> consultarCampanhasDoTimeDoCoracaoNaoAssociadas(Long idTimeCoracao, List<Campanha> campanhasAssociadas) throws TodasCampanhasJaAssociadasException {
		List<Long>campanhaIds = campanhasAssociadas.stream()
												   .map(Campanha::getId)
												   .collect(Collectors.toList());
		
		List<Campanha> campanhasNaoAssociadas = repository.findCampanhaByIdTimeCoracaoAndIdNotIn(idTimeCoracao, campanhaIds);
		
		if(campanhasNaoAssociadas == null || campanhasNaoAssociadas.isEmpty()) {
			throw new TodasCampanhasJaAssociadasException(String.format("Campanhas do time do coração %d já associadas", idTimeCoracao));
		}
		
		return campanhasNaoAssociadas;
		
	}
	
	/**
	 * Método retorna campanha pelo identificador informado, caso não esteja vencida.
	 * @param id - Long representando o identificador da campanha
	 * @return Campanha - Campanha não vencida
	 * @throws CampanhaNaoEncontradaException - Se nenhuma campanha for encontrada ou se a campanha encontrada estiver vencida
	 */
	public Campanha consultarCampanhaNaoVencida(Long id) throws CampanhaNaoEncontradaException {
		Campanha campanha = repository.findByIdAndFimDataVigenciaGreaterThanEqual(id, new Date());
		
		if(campanha == null) {
			throw new CampanhaNaoEncontradaException(String.format("Campanha não encontrada para o identificador %d ou está vencida", id));
		}
		
		return campanha;
	}
	
	/**
	 * Método retorna apenas campanhas não vencidas (campanhas com fimDataVigencia superiores à data atual)
	 * @return List<Campanha> - Contendo campanhas que ainda não venceram
	 * @throws CampanhaNaoEncontradaException - Se nenhuma campanha for encontrada ou se todas estiverem vencidas
	 */
	public List<Campanha> consultarCampanhasNaoVencidas(Long idTimeCoracao) throws CampanhaNaoEncontradaException {
		List<Campanha> campanhas = repository.findByIdTimeCoracaoAndFimDataVigenciaGreaterThanEqual(idTimeCoracao, new Date());
		
		if(campanhas == null || campanhas.isEmpty()) {
			throw new CampanhaNaoEncontradaException("Não foi encontrada nenhuma campanha ou todas as campanhas estão vencidas");
		}
		
		return campanhas;
	}
	
	public Campanha cadastrar(Campanha campanha) throws NotificacaoException {
		tratarCampanhasVigentes(campanha);
		Campanha campanhaCadastrada = repository.save(campanha);
		try {
			notificadorCampanhaService.notificarCadastro(campanhaCadastrada);
		} catch (AmqpException | JsonProcessingException e) {
			throw new NotificacaoException("Falha ao tentar notificar cadastro da campanha " + campanhaCadastrada, e);
		}
		return campanhaCadastrada;
	}
	
	public void delete(Long id) {
		repository.delete(id);
	}
	
	private void tratarCampanhasVigentes(Campanha campanha) throws NotificacaoException {
		List<Campanha> campanhasVigentes = consultarCampanhasVigentesOrdenadasPorFinalDeVigencia(campanha);
		log.info("Campanhas vigentes: " + campanhasVigentes);
		if(campanhasVigentes != null && !campanhasVigentes.isEmpty()) {
			incrementarDataFimCampanhasVigentes(campanhasVigentes, campanha);
			campanhasVigentes = repository.save(campanhasVigentes);
			try {
				notificadorCampanhaService.notificarAlteracoes(campanhasVigentes);
			} catch (AmqpException | JsonProcessingException e) {
				throw new NotificacaoException("Falha ao tentar notificar alterações realizadas nas campanhas", e);
			}
		}
		log.info("Campanhas vigentes incrementadas: " + campanhasVigentes);
		
	}
	
	/**
	 * Método recursivo que incrementa a data de fim de vigência das campanhas ativas com base na data de vigência da campanha sendo cadastrada.
	 * @param campanhasVigentes List<Campanha> contendo as campanhas vigentes cujas datas de fim de vigência devem ser incrementadas
	 * @param campanha Campanha contendo a data de fim de vigência que será utilizada com base para incrementar as demais datas
	 */
	private void incrementarDataFimCampanhasVigentes(List<Campanha> campanhasVigentes, Campanha campanha) {
		
		if(campanhasVigentes == null || campanhasVigentes.isEmpty()) {
			return;
		}
		
		Campanha campanhaVigente = campanhasVigentes.get(0);
		
		//Atualiza a data fim apenas se a data da campanha vigente for menor do que a data da campanha a ser cadastrada.
		//Isso evita que as datas das campanhas vigentes armazenadas na base de dados sejam incrementadas,
		//caso a campanha esteja sendo atualizada e a data fim não tenha sido alterada.
		if(campanhaVigente.getFimDataVigencia().getTime() <= campanha.getFimDataVigencia().getTime()) {
			campanhaVigente.setFimDataVigencia(DateUtil.somaDia(campanha.getFimDataVigencia(), 1));
		}
		
		List<Campanha> restanteCampanhasVigentes = campanhasVigentes.subList(1, campanhasVigentes.size());
		incrementarDataFimCampanhasVigentes(restanteCampanhasVigentes, campanhaVigente);
		
	}

	/**
	 * Método retorna as campanhas vigentes atreladas ao time do coração e às datas de vigência informadas.
	 * @param campanha Campanha contendo a data de vigência e o time do coração para a consulta
	 * @return List<Campanha> contendo todas as campanhas, exceto a própria campanha passada por parâmetro, se a mesma já estiver persistida
	 */
	private List<Campanha> consultarCampanhasVigentesOrdenadasPorFinalDeVigencia(Campanha campanha){
		
		return campanha.getId() == null 
					? repository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(
							campanha.getIdTimeCoracao(), campanha.getInicioDataVigencia(), campanha.getFimDataVigencia())
					: repository.findByIdNotAndIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(
							campanha.getId(), campanha.getIdTimeCoracao(), campanha.getInicioDataVigencia(), campanha.getFimDataVigencia());
	}

}
