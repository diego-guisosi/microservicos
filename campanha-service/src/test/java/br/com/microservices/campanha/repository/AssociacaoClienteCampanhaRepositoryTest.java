package br.com.microservices.campanha.repository;

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
import org.springframework.test.context.junit4.SpringRunner;

import br.com.microservices.campanha.model.AssociacaoClienteCampanha;
import br.com.microservices.campanha.model.Campanha;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AssociacaoClienteCampanhaRepositoryTest {
	
	private static Logger log = Logger.getLogger(AssociacaoClienteCampanhaRepositoryTest.class);
	
	@MockBean
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private AssociacaoClienteCampanhaRepository associacaoRepository;
	
	@Autowired
	private CampanhaRepository campanhaRepository;

	/**
	 * Método testa a consulta das campanhas associadas para o cliente cujo identificador é igual a 10.
	 * Como a campanha cadastrada no teste foi associada ao cliente de identificador 10, a consulta retorna resultado.
	 */
	@Test
	public void testFindCampanhaByClienteId() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanhaSalva = campanhaRepository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		associacaoRepository.save(new AssociacaoClienteCampanha(10L, campanhaSalva.getId()));
		
		List<Campanha> campanhasCliente = associacaoRepository.findCampanhaByClienteId(10L);
		log.info(campanhasCliente);
		Assert.assertTrue(campanhasCliente.size() == 1);
		Assert.assertTrue(campanhasCliente.get(0).getId().equals(campanhaSalva.getId()));
		
	}
	
	/**
	 * Método testa a consulta das campanhas associadas para o cliente cujo identificador é igual a 9.
	 * Como a campanha cadastrada no teste foi associada ao cliente de identificador 10, a consulta não retorna resultado.
	 */
	@Test
	public void testFindCampanhaByClienteIdIncorreto() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanhaSalva = campanhaRepository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		associacaoRepository.save(new AssociacaoClienteCampanha(10L, campanhaSalva.getId()));
		
		List<Campanha> campanhasCliente = associacaoRepository.findCampanhaByClienteId(9L);
		log.info(campanhasCliente);
		Assert.assertTrue(campanhasCliente == null || campanhasCliente.isEmpty());
		
	}

}
