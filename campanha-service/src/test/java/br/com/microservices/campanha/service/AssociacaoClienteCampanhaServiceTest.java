package br.com.microservices.campanha.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import br.com.microservices.campanha.Application;
import br.com.microservices.campanha.exception.CampanhaNaoEncontradaException;
import br.com.microservices.campanha.exception.TodasCampanhasJaAssociadasException;
import br.com.microservices.campanha.model.AssociacaoClienteCampanha;
import br.com.microservices.campanha.model.Campanha;
import br.com.microservices.campanha.repository.AssociacaoClienteCampanhaRepository;
import br.com.microservices.campanha.repository.CampanhaRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Application.class, loader=AnnotationConfigContextLoader.class)
@DataJpaTest
public class AssociacaoClienteCampanhaServiceTest {
	
	private static Logger log = Logger.getLogger(AssociacaoClienteCampanhaServiceTest.class);
	
	@MockBean
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private CampanhaRepository campanhaRepository;
	
	@Autowired
	private AssociacaoClienteCampanhaRepository associacaoRepository;
	
	@Autowired
	private AssociacaoClienteCampanhaService service;

	/**
	 * Método testa consulta de campanhas do cliente que não possui nenhuma campanha associada.
	 * 
	 * Resultado esperado:
	 * 		Lançamento de exceção, indicando que nenhuma campanha foi encontrada.
	 */
	@Test
	public void testConsultarCampanhasDoClienteSemCampanhas() throws ParseException {
		
		try {
			service.consultarCampanhasDoCliente(1L);
			Assert.assertTrue(false);
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			Assert.assertTrue(true);
		}
		
	}
	
	/**
	 * Método testa consulta de campanhas do cliente que possui campanhas associadas.
	 * 
	 * Resultado esperado:
	 * 		Lista de campanhas associadas para o cliente
	 * @throws ParseException
	 */
	@Test
	public void testConsultarCampanhasDoClienteComCampanhas() throws ParseException {
		
		Long clienteId = 10L;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanhaSalva = campanhaRepository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		associacaoRepository.save(new AssociacaoClienteCampanha(clienteId, campanhaSalva.getId()));
		
		try {
			List<Campanha> campanhasDoCliente = service.consultarCampanhasDoCliente(clienteId);
			Assert.assertTrue(campanhasDoCliente != null && !campanhasDoCliente.isEmpty() && campanhasDoCliente.get(0).equals(campanhaSalva));
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		
	}
	
	/**
	 * Método testa consulta de campanhas ainda não associadas para o cliente e que sejam do seu time do coração.
	 * 
	 * Resultado esperado:
	 * 		Lista de campanhas ainda não associadas para o cliente
	 * @throws ParseException
	 */
	@Test
	public void testConsultarCampanhasNaoAssociadasQueExistam() throws ParseException {
		
		Long clienteId = 10L;
		Long idTimeCoracao = 1L;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanhaSalva1 = campanhaRepository.save(new Campanha("Minha Campanha", idTimeCoracao, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		Campanha campanhaSalva2 = campanhaRepository.save(new Campanha("Minha Campanha Não Associada", idTimeCoracao, sdf.parse("2017-09-19"), sdf.parse("2017-09-20")));
		
		associacaoRepository.save(new AssociacaoClienteCampanha(clienteId, campanhaSalva1.getId()));
		
		try {
			List<Campanha> campanhasDoCliente = service.consultarCampanhasNaoAssociadas(clienteId, idTimeCoracao);
			Assert.assertTrue(campanhasDoCliente != null && !campanhasDoCliente.isEmpty() && campanhasDoCliente.get(0).equals(campanhaSalva2));
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			Assert.assertTrue(false);
		} catch (TodasCampanhasJaAssociadasException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		
	}
	
	/**
	 * Método testa consulta de campanhas não associadas para o cliente em base que não possui campanhas cadastradas.
	 * 
	 * Resultado esperado:
	 * 		Lançamento de exceção indicando que não há campanhas cadastradas
	 */
	@Test
	public void testConsultarCampanhasNaoAssociadasNaoExistentes() throws ParseException {
		
		Long clienteId = 10L;
		Long idTimeCoracao = 1L;
		
		try {
			service.consultarCampanhasNaoAssociadas(clienteId, idTimeCoracao);
			Assert.assertTrue(false);
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			Assert.assertTrue(true);
		} catch (TodasCampanhasJaAssociadasException e) {
			log.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		
	}
	
	/**
	 * Método testa consulta de campanhas não associadas para o cliente em cenário onde todas as campanhas já estão associadas.
	 * 
	 * Resultado esperado:
	 * 		Lançamento de exceção, indicando que todas as campanhas já estão associadas.
	 * @throws ParseException
	 */
	@Test
	public void testConsultarCampanhasNaoAssociadasQuandoTodasJaEstaoAssociadas() throws ParseException {
		
		Long clienteId = 10L;
		Long idTimeCoracao = 1L;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanhaSalva1 = campanhaRepository.save(new Campanha("Minha Campanha", idTimeCoracao, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		Campanha campanhaSalva2 = campanhaRepository.save(new Campanha("Minha Campanha Não Associada", idTimeCoracao, sdf.parse("2017-09-19"), sdf.parse("2017-09-20")));
		
		associacaoRepository.save(new AssociacaoClienteCampanha(clienteId, campanhaSalva1.getId()));
		associacaoRepository.save(new AssociacaoClienteCampanha(clienteId, campanhaSalva2.getId()));
		
		try {
			service.consultarCampanhasNaoAssociadas(clienteId, idTimeCoracao);
			Assert.assertTrue(false);
		} catch (CampanhaNaoEncontradaException e) {
			log.error(e.getMessage(), e);
			Assert.assertTrue(false);
		} catch (TodasCampanhasJaAssociadasException e) {
			log.error(e.getMessage(), e);
			Assert.assertTrue(true);
		}
		
	}

}
