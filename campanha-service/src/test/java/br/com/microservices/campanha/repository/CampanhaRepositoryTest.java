package br.com.microservices.campanha.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.microservices.campanha.model.Campanha;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CampanhaRepositoryTest {
	
	@MockBean
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private CampanhaRepository repository;
	
	/**
	 * Método testa a consulta de campanhas ativas (vigentes) entre as datas 2017-09-14 e 2017-09-21. 
	 * Como a campanha cadastrada apenas entra em vigência no dia posterior, a consulta não retorna resultado.
	 */
	@Test
	public void testFindByCampanhaComDataInicioInferiorCampanhaCadastrada() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		
		List<Campanha> campanhas = repository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, sdf.parse("2017-09-14"), sdf.parse("2017-09-21"));
		Assert.assertTrue(campanhas == null || campanhas.isEmpty());
		
	}
	
	/**
	 * Método testa a consulta de campanhas ativas entre as datas 2017-09-15 e 2017-09-16
	 * Como a campanha cadastrada entrou em vigência no dia anterior e ficará ativa (vigente) até a data 2017-09-16, a consulta retorna resultado.
	 */
	@Test
	public void testFindByCampanhaComDataInicioSuperiorCampanhaCadastrada() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-14"), sdf.parse("2017-09-16")));
		
		List<Campanha> campanhas = repository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-16"));
		Assert.assertTrue(campanhas != null && !campanhas.isEmpty());
		
	}
	
	/**
	 * Método testa a consulta de campanhas ativas (vigentes) entre as datas 2017-09-19 e 2017-09-21. 
	 * Como a campanha cadastrada encerrou a vigência em 2017-09-20, a consulta não retorna resultado.
	 */
	@Test
	public void testFindByCampanhaComDataFimSuperiorCampanhaCadastrada() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-20")));
		
		List<Campanha> campanhas = repository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-21"));
		Assert.assertTrue(campanhas == null || campanhas.isEmpty());
		
	}
	
	/**
	 * Método testa a consulta de campanhas ativas entre as datas 2017-09-17 e 2017-09-19
	 * Como a campanha cadastrada entrou em vigência no dia anterior e ficará ativa (vigente) até a data 2017-09-20, a consulta retorna resultado.
	 */
	@Test
	public void testFindByCampanhaVigente() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-20")));
		
		List<Campanha> campanhas = repository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, sdf.parse("2017-09-17"), sdf.parse("2017-09-19"));
		Assert.assertTrue(!campanhas.isEmpty());
		
	}
	
	/**
	 * Método testa a consulta de campanhas vigentes entra as datas 2017-09-17 e 2017-09-19, que não sejam a campanha do ID fornecido.
	 * O método testado é utilizado pela rotina de modificação das datas de final de vigência durante o cadastro das campanhas,
	 * sendo responsável por retornar todas as campanhas que precisam ter suas datas de final de vigência alteradas
	 */
	@Test
	public void testFindByCampanhaVigenteComCampanhaJaPersistida() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanhaCadastrada1 = repository.save(new Campanha("Minha Campanha 1", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-20")));
		Campanha campanhaCadastrada2 = repository.save(new Campanha("Minha Campanha 2", 1L, sdf.parse("2017-09-17"), sdf.parse("2017-09-19")));
		
		List<Campanha> campanhas = repository.findByIdNotAndIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(
				campanhaCadastrada1.getId(), 1L, sdf.parse("2017-09-17"), sdf.parse("2017-09-19"));
		
		Assert.assertTrue(campanhas.size() == 1);
		Assert.assertTrue(campanhaCadastrada2.equals(campanhas.get(0))); //A campanha fornecida por parâmetro não é retornada
		
	}
	
	/**
	 * Método testa a consulta das campanhas do time do coração indicado que possua data de final de vigência posterior a data 2017-09-17.
	 * Como a campanha cadastrada finalizará vigência apenas em 2017-09-20, a consulta retorna resultado.
	 * O método testado é utilizado pela rotina de consulta de campanhas do time do coração que não estejam vencidas.
	 */
	@Test
	public void testFindByIdTimeCoracaoCorretoEFimDataVigenciaGreaterThanEqual() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-20")));
		
		List<Campanha> campanhas = repository.findByIdTimeCoracaoAndFimDataVigenciaGreaterThanEqual(1L, sdf.parse("2017-09-17"));
		Assert.assertTrue(!campanhas.isEmpty());
		
	}
	
	/**
	 * Método testa a consulta das campanhas do time do coração indicado que possua data de final de vigência posterior a data 2017-09-17.
	 * Apesar da campanha cadastrada finalizar vigência apenas em 2017-09-20, o time de coração é diferente do time fornecido por parâmetro.
	 * O método testado é utilizado pela rotina de consulta de campanhas do time do coração que não estejam vencidas.
	 */
	@Test
	public void testFindByIdTimeCoracaoIncorretoEFimDataVigenciaGreaterThanEqual() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-20")));
		
		List<Campanha> campanhas = repository.findByIdTimeCoracaoAndFimDataVigenciaGreaterThanEqual(2L, sdf.parse("2017-09-17"));
		Assert.assertTrue(campanhas == null || campanhas.isEmpty());
		
	}
	
	/**
	 * Método testa a consulta de campanha por identificador e que tenha data de final de vigência maior ou igual a data fornecida como parâmetro.
	 * Como a data de final de vigência da campanha cadastrada está de acordo com a data fornecida como parâmetro, a consulta retorna resultado.
	 * O método testado é utlizado pelo método de consulta de campanha ativa, exposto como serviço REST.
	 */
	@Test
	public void testFindByIdCorretoENaoVencida() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanhaSalva = repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-17")));
		
		Campanha campanha = repository.findByIdAndFimDataVigenciaGreaterThanEqual(campanhaSalva.getId(), sdf.parse("2017-09-17"));
		Assert.assertTrue(campanha != null);
		
	}
	
	/**
	 * Método testa a consulta de campanha por identificador e que tenha data de final de vigência maior ou igual a data fornecida como parâmetro.
	 * Apesar da campanha cadastrada possuir data de final de vigência posterior à data informada, o seu identificador é diferente. 
	 * O método testado é utlizado pelo método de consulta de campanha ativa, exposto como serviço REST.
	 */
	@Test
	public void testFindByIdIncorretoENaoVencida() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-20")));
		
		Campanha campanha = repository.findByIdAndFimDataVigenciaGreaterThanEqual(2L, sdf.parse("2017-09-17"));
		Assert.assertTrue(campanha == null);
		
	}
	
	/**
	 * Método testa a consulta de campanha por identificador e que tenha data de final de vigência maior ou igual a data fornecida como parâmetro.
	 * Apesar do identificador da campanha utilizado estar de acordo com a campanha cadastrada, sua data de final de vigência é anterior à data informada e, portanto, não está mais ativa. 
	 * O método testado é utlizado pelo método de consulta de campanha ativa, exposto como serviço REST.
	 * @throws ParseException
	 */
	@Test
	public void testFindByIdCorretoEVencida() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		repository.save(new Campanha("Minha Campanha", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		
		Campanha campanha = repository.findByIdAndFimDataVigenciaGreaterThanEqual(1L, sdf.parse("2017-09-17"));
		Assert.assertTrue(campanha == null);
		
	}
	
}
