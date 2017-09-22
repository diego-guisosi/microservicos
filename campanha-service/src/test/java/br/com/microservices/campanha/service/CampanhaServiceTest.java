package br.com.microservices.campanha.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import br.com.microservices.campanha.exception.NotificacaoException;
import br.com.microservices.campanha.model.Campanha;
import br.com.microservices.campanha.repository.CampanhaRepository;
import br.com.microservices.campanha.util.DateUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Application.class, loader=AnnotationConfigContextLoader.class)
@DataJpaTest
public class CampanhaServiceTest {
	
	private static Logger log = Logger.getLogger(CampanhaServiceTest.class);
	
	@MockBean
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private CampanhaService service;
	
	@Autowired
	private CampanhaRepository campanhaRepository;
	
	/**
	 * Método testa o cadastro de campanha com vigência entre 2017-09-16 e 2017-09-18.
	 * Como nenhuma das campanhas existentes estarão vigentes entre o período da campanha sendo cadastrada, nenhuma campanha precisa de sua data de final de vigência incrementada.
	 */
	@Test
	public void testCadastrarSemCampanhasVigentes() throws ParseException, NotificacaoException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanha = new Campanha("Minha Campanha A Cadastrar", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"));
		
		campanhaRepository.save(new Campanha("Minha Campanha 1", 1L, sdf.parse("2017-09-14"), sdf.parse("2017-09-15"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 2", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-20"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 3", 1L, sdf.parse("2017-09-13"), sdf.parse("2017-09-14"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 4", 1L, sdf.parse("2017-09-10"), sdf.parse("2017-09-13"))); //Campanha não vigente
		
		Campanha campanhaCadastrada = service.cadastrar(campanha);
		log.info("Campanha cadastrada: " + campanhaCadastrada);
		List<Campanha> campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, campanha.getInicioDataVigencia(), campanha.getFimDataVigencia());
		
		log.info("Datas de final de vigência: " + campanhasVigentes);
		Assert.assertTrue(campanhasVigentes.size() == 1); //Apenas retornará a campanha cadastrada
		
		Date inicioDataVigencia = sdf.parse("2017-09-16");
		Date fimDataVigencia = sdf.parse("2017-09-18");
		Assert.assertTrue(campanhasVigentes.stream()
										   .filter(c -> c.getInicioDataVigencia().equals(inicioDataVigencia) && c.getFimDataVigencia().equals(fimDataVigencia))
										   .findAny()
										   .isPresent());
		
	}
	
	/**
	 * Método testa o cadastro de campanha com vigência entre 2017-09-16 e 2017-09-18.
	 * Como nenhuma das campanhas (que estarão vigentes no período da campanha sendo cadastrada) são do time do coração informado, 
	 * nenhuma data de final de vigência é incrementada.
	 */
	@Test
	public void testCadastrarSemCampanhasVigentesDoTimeDoCoracao() throws ParseException, NotificacaoException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanha = new Campanha("Minha Campanha A Cadastrar", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"));
		
		campanhaRepository.save(new Campanha("Minha Campanha 1", 2L, sdf.parse("2017-09-14"), sdf.parse("2017-09-15"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 2", 2L, sdf.parse("2017-09-15"), sdf.parse("2017-09-20"))); //Campanha vigente para outro time
		campanhaRepository.save(new Campanha("Minha Campanha 3", 2L, sdf.parse("2017-09-13"), sdf.parse("2017-09-14"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 4", 2L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"))); //Campanha vigente para outro time
		
		Campanha campanhaCadastrada = service.cadastrar(campanha);
		log.info("Campanha cadastrada: " + campanhaCadastrada);
		List<Campanha> campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, campanha.getInicioDataVigencia(), campanha.getFimDataVigencia());
		
		log.info("Datas de final de vigência: " + campanhasVigentes);
		Assert.assertTrue(campanhasVigentes.size() == 1); //Apenas retornará a campanha cadastrada
		
		Date inicioDataVigencia = sdf.parse("2017-09-16");
		Date fimDataVigencia = sdf.parse("2017-09-18");
		Assert.assertTrue(campanhasVigentes.stream()
										   .filter(c -> c.getInicioDataVigencia().equals(inicioDataVigencia) && c.getFimDataVigencia().equals(fimDataVigencia))
										   .findAny()
										   .isPresent());
		
	}
	
	/**
	 * Método testa o cadastro de campanha com vigência entre 2017-09-16 e 2017-09-18.
	 * 
	 * Como duas campanhas estarão vigentes no período de vigência da campanha sendo cadastrada e a data final de suas vigências são menores ou iguais
	 * à data de vigência da campanha sendo cadastrada, o método de cadastro incrementa a data final de vigência das campanhas que estavam previamente cadastradas,
	 * de modo que suas datas de final de vigência sejam todas diferentes.
	 * 
	 * Para o caso abaixo, teremos:
	 * 		"Minha Campanha A Cadastrar" com vigência entre 2017-09-16 e 2017-09-18
	 * 		"Minha Campanha 1" com vigência entre 2017-09-16 e 2017-09-19
	 * 		"Minha Campanha 3" com vigência entre 2017-09-15 e 2017-09-20
	 * 
	 * As campanhas serão atualizadas na ordem indicada anteriormente, pois a campanha sendo cadastrada não tem sua data de final de vigência alterada e 
	 * as previamente cadastradas são retornadas pela consulta ordenadas em forma ascendente pela data de final de vigência
	 */
	@Test
	public void testCadastrarComCampanhasVigentesDoTimeDoCoracao() throws ParseException, NotificacaoException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Campanha campanha = new Campanha("Minha Campanha A Cadastrar", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"));
		
		campanhaRepository.save(new Campanha("Minha Campanha 1", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 2", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-20"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 3", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-19"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 4", 1L, sdf.parse("2017-09-14"), sdf.parse("2017-09-16"))); //Campanha não vigente
		
		Campanha campanhaCadastrada = service.cadastrar(campanha);
		log.info("Campanha cadastrada: " + campanhaCadastrada);
		List<Campanha> campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, campanha.getInicioDataVigencia(), campanha.getFimDataVigencia());
		
		Set<Date> conjuntoFimDataVigencia = campanhasVigentes.stream()
				                                             .map(Campanha::getFimDataVigencia)
				                                             .sorted()
				                                             .collect(Collectors.toSet());
		
		log.info("Datas de final de vigência: " + conjuntoFimDataVigencia);
		Assert.assertTrue(conjuntoFimDataVigencia.size() == 3); //Por não aceitar datas repitidas, o Set deverá conter 3 datas (duas datas incrementadas de campanhas existentes e uma data da campanha cadastrada)
		
		Date dataDia18 = sdf.parse("2017-09-18");
		Date dataDia19 = sdf.parse("2017-09-19");
		Date dataDia20 = sdf.parse("2017-09-20");
		
		Assert.assertTrue(contemDataFimVigencia(dataDia18, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia19, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia20, conjuntoFimDataVigencia));
		
	}
	
	/**
	 * Método testa a atualização do campo "nome" de campanha previamente cadastrada, (campanha esta que forçou a atualização da data de final de vigência das demais campanhas ao ser cadastrada).
	 * Como esperado, a atualização do campo não forçou uma nova atualização das datas de vigência das campanhas.
	 * @throws NotificacaoException 
	 */
	@Test
	public void testAtualizarSemAlterarFimDataVigenciaComCampanhasVigentesDoTimeDoCoracao() throws ParseException, NotificacaoException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date dataDia18 = sdf.parse("2017-09-18");
		Date dataDia19 = sdf.parse("2017-09-19");
		Date dataDia20 = sdf.parse("2017-09-20");
		
		Campanha campanha = new Campanha("Minha Campanha A Cadastrar", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"));
		
		campanhaRepository.save(new Campanha("Minha Campanha 1", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 2", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-20"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 3", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-19"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 4", 1L, sdf.parse("2017-09-14"), sdf.parse("2017-09-16"))); //Campanha não vigente
		
		Campanha campanhaCadastrada = service.cadastrar(campanha);
		log.info("Campanha cadastrada: " + campanhaCadastrada);
		List<Campanha> campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, campanha.getInicioDataVigencia(), campanha.getFimDataVigencia());
		
		Set<Date> conjuntoFimDataVigencia = campanhasVigentes.stream()
				                                             .map(Campanha::getFimDataVigencia)
				                                             .sorted()
				                                             .collect(Collectors.toSet());
		
		Assert.assertTrue(contemDataFimVigencia(dataDia18, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia19, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia20, conjuntoFimDataVigencia));
		
		log.info("Datas de final de vigência: " + conjuntoFimDataVigencia);
		Assert.assertTrue(conjuntoFimDataVigencia.size() == 3); //Por não aceitar datas repitidas, o Set deverá conter 3 datas (duas datas incrementadas de campanhas existentes e uma data da campanha cadastrada)
		
		campanhaCadastrada.setNome("Minha Campanha Atualizada");
		Campanha campanhaAtualizada = service.cadastrar(campanhaCadastrada); //Atualização de campanha Cadastrada
		
		//Recupera campanhas vigentes após atualização do nome da campanha cadastrada previamente
		campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(
						1L, campanhaAtualizada.getInicioDataVigencia(), campanhaAtualizada.getFimDataVigencia());
		
		//Como o fim da data de vigência não foi alterado na campanha atualizada, 
		//as datas de fim de vigência das campanhas vigentes armazenadas na base de dados devem se manter com os mesmos valores gerados durante o cadastro
		conjuntoFimDataVigencia = campanhasVigentes.stream()
									               .map(Campanha::getFimDataVigencia)
									               .sorted()
									               .collect(Collectors.toSet());
		
		Assert.assertTrue(contemDataFimVigencia(dataDia18, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia19, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia20, conjuntoFimDataVigencia));
		
	}
	
	/**
	 * Método testa a atualização do campo "fimDataVigencia" de campanha previamente cadastrada, (campanha esta que forçou a atualização da data de final de vigência das demais campanhas ao ser cadastrada).
	 * Como a data alterada é igual a data de final de vigência de uma das campanhas cadastradas, o tratamento que incrementa as datas de final de vigência é realizado novamente
	 * @throws NotificacaoException 
	 */
	@Test
	public void testAtualizarAlterarandoFimDataVigenciaParaValorComCampanhasVigentesDoTimeDoCoracao() throws ParseException, NotificacaoException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date dataDia18 = sdf.parse("2017-09-18");
		Date dataDia19 = sdf.parse("2017-09-19");
		Date dataDia20 = sdf.parse("2017-09-20");
		Date dataDia21 = sdf.parse("2017-09-21");
		
		Campanha campanha = new Campanha("Minha Campanha A Cadastrar", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"));
		
		campanhaRepository.save(new Campanha("Minha Campanha 1", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 2", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-19"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 3", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-20"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 4", 1L, sdf.parse("2017-09-17"), sdf.parse("2017-09-21"))); //Campanha não vigente
		
		Campanha campanhaCadastrada = service.cadastrar(campanha);
		log.info("Campanha cadastrada: " + campanhaCadastrada);
		List<Campanha> campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, campanha.getInicioDataVigencia(), campanha.getFimDataVigencia());
		
		Set<Date> conjuntoFimDataVigencia = campanhasVigentes.stream()
				                                             .map(Campanha::getFimDataVigencia)
				                                             .sorted()
				                                             .collect(Collectors.toSet());
		
		Assert.assertTrue(contemDataFimVigencia(dataDia18, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia19, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia20, conjuntoFimDataVigencia));
		
		log.info("Datas de final de vigência: " + conjuntoFimDataVigencia);
		Assert.assertTrue(conjuntoFimDataVigencia.size() == 3); //Por não aceitar datas repitidas, o Set deverá conter 3 datas (duas datas incrementadas de campanhas existentes e uma data da campanha cadastrada)
		
		campanhaCadastrada.setNome("Minha Campanha Atualizada");
		campanhaCadastrada.setFimDataVigencia(DateUtil.somaDia(campanhaCadastrada.getFimDataVigencia(), 1)); //Somando um dia à data de final de vigência
		
		Campanha campanhaAtualizada = service.cadastrar(campanhaCadastrada); //Atualização de campanha Cadastrada
		
		//Recupera campanhas vigentes após atualização do nome e da data de final de vigência da campanha cadastrada previamente
		campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(
						1L, campanhaAtualizada.getInicioDataVigencia(), campanhaAtualizada.getFimDataVigencia());
		
		//Como o fim da data de vigência foi atualizada para um valor maior do que estava cadastrado anteiormente, 
		//as datas de fim de vigência das campanhas vigentes armazenadas na base de dados devem ser incrementadas, para respeitar a regra das datas de vigência diferentes
		conjuntoFimDataVigencia = campanhasVigentes.stream()
									               .map(Campanha::getFimDataVigencia)
									               .sorted()
									               .collect(Collectors.toSet());
		
		Assert.assertTrue(contemDataFimVigencia(dataDia19, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia20, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia21, conjuntoFimDataVigencia));
		
		log.info("Datas de final de vigência: " + conjuntoFimDataVigencia);
		Assert.assertTrue(conjuntoFimDataVigencia.size() == 3);
		
	}
	
	/**
	 * Método testa a atualização do campo "fimDataVigencia" de 2017-09-18 para 2017-09-19 na campanha previamente cadastrada, (campanha esta que forçou a atualização da data de final de vigência das demais campanhas ao ser cadastrada).
	 * Como a data alterada não é igual a data de final de vigência de nenhuma das campanhas cadastradas, o tratamento que incrementa as datas de final de vigência não é executado.
	 * Com isso, apenas a data de final de vigência da campanha atualizada é modificada e, as demais, permanecem inalteradas.
	 * @throws NotificacaoException 
	 */
	@Test
	public void testAtualizarAlterarandoFimDataVigenciaParaValorMenorComCampanhasVigentesDoTimeDoCoracao() throws ParseException, NotificacaoException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date dataDia17 = sdf.parse("2017-09-17");
		Date dataDia18 = sdf.parse("2017-09-18");
		Date dataDia19 = sdf.parse("2017-09-19");
		Date dataDia20 = sdf.parse("2017-09-20");
		
		Campanha campanha = new Campanha("Minha Campanha A Cadastrar", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"));
		
		campanhaRepository.save(new Campanha("Minha Campanha 1", 1L, sdf.parse("2017-09-16"), sdf.parse("2017-09-18"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 2", 1L, sdf.parse("2017-09-19"), sdf.parse("2017-09-19"))); //Campanha não vigente
		campanhaRepository.save(new Campanha("Minha Campanha 3", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-20"))); //Campanha vigente
		campanhaRepository.save(new Campanha("Minha Campanha 4", 1L, sdf.parse("2017-09-17"), sdf.parse("2017-09-21"))); //Campanha não vigente
		
		Campanha campanhaCadastrada = service.cadastrar(campanha);
		log.info("Campanha cadastrada: " + campanhaCadastrada);
		List<Campanha> campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(1L, campanha.getInicioDataVigencia(), campanha.getFimDataVigencia());
		
		Set<Date> conjuntoFimDataVigencia = campanhasVigentes.stream()
				                                             .map(Campanha::getFimDataVigencia)
				                                             .sorted()
				                                             .collect(Collectors.toSet());
		
		Assert.assertTrue(contemDataFimVigencia(dataDia18, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia19, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia20, conjuntoFimDataVigencia));
		
		log.info("Datas de final de vigência: " + conjuntoFimDataVigencia);
		Assert.assertTrue(conjuntoFimDataVigencia.size() == 3); //Por não aceitar datas repitidas, o Set deverá conter 3 datas (duas datas incrementadas de campanhas existentes e uma data da campanha cadastrada)
		
		campanhaCadastrada.setNome("Minha Campanha Atualizada");
		campanhaCadastrada.setFimDataVigencia(DateUtil.somaDia(campanhaCadastrada.getFimDataVigencia(), -1)); //Subtraindo um dia à data de final de vigência
		
		Campanha campanhaAtualizada = service.cadastrar(campanhaCadastrada); //Atualização de campanha Cadastrada
		
		//Recupera campanhas vigentes após atualização do nome e da data de final de vigência da campanha cadastrada previamente
		campanhasVigentes = 
				campanhaRepository.findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(
						1L, campanhaAtualizada.getInicioDataVigencia(), campanhaAtualizada.getFimDataVigencia());
		
		//Como o fim da data de vigência foi alterado na campanha atualizada para um valor menor do que estava cadastrado anteriormente, 
		//as datas de fim de vigência das campanhas vigentes armazenadas na base de dados devem se manter com os mesmos valores gerados durante o cadastro
		conjuntoFimDataVigencia = campanhasVigentes.stream()
									               .map(Campanha::getFimDataVigencia)
									               .sorted()
									               .collect(Collectors.toSet());
		
		Assert.assertTrue(contemDataFimVigencia(dataDia17, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia19, conjuntoFimDataVigencia));
		Assert.assertTrue(contemDataFimVigencia(dataDia20, conjuntoFimDataVigencia));
		
		log.info("Datas de final de vigência: " + conjuntoFimDataVigencia);
		Assert.assertTrue(conjuntoFimDataVigencia.size() == 3);
		
	}
	
	private boolean contemDataFimVigencia(Date dataFimVigencia, Set<Date> conjuntoFimDataVigencia) {
		return conjuntoFimDataVigencia.stream()
									  .filter(fimVigencia -> fimVigencia.equals(dataFimVigencia))
									  .findAny()
									  .isPresent();
	}

}
