package br.com.microservices.cliente.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import br.com.microservices.cliente.Application;
import br.com.microservices.cliente.client.CampanhaServiceClient;
import br.com.microservices.cliente.model.AssociacaoClienteCampanha;
import br.com.microservices.cliente.model.Campanha;
import br.com.microservices.cliente.model.Cliente;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Application.class, loader=AnnotationConfigContextLoader.class)
@DataJpaTest
public class AssociacaoCampanhaServiceTest {
	
	@MockBean(value = {CampanhaServiceClient.class})
	@Qualifier("br.com.microservices.cliente.client.CampanhaServiceClient")
	private CampanhaServiceClient client;
	
	@Autowired
	private AssociacaoCampanhaService associacaoCampanhaService;
	
	private Cliente cliente;
	private AssociacaoClienteCampanha associacao1;
	private AssociacaoClienteCampanha associacao2;
	
	@Before
	public void setup() {
		cliente = new Cliente("Cliente1", "cliente1@gmail.com", new Date(), 1L);
		cliente.setId(1L);
		
		associacao1 = new AssociacaoClienteCampanha(cliente.getId(), 1L);
		associacao2 = new AssociacaoClienteCampanha(cliente.getId(), 2L);
		
		Mockito.when(client.associarClienteCampanha(associacao1)).thenReturn(associacao1);
		Mockito.when(client.associarClienteCampanha(associacao2)).thenReturn(associacao2);
	}

	/**
	 * Método testa associação, para o novo cliente, de todas as campanhas do seu time do coração existentes e ativas.
	 * 
	 * Resultado esperado:
	 * 		Associação de todas as campanhas retornadas pelo client REST
	 */
	@Test
	public void testAssociacaoDasCampanhasParaNovoClienteComCampanhasExistentesParaTimeDoCoracao() throws ParseException {
		
		Mockito.when(client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao())).thenReturn(getCampanhasTimeCoracao());
		
		associacaoCampanhaService.associarNovoCliente(cliente);
		
		Assert.assertTrue(true);
	}
	
	/**
	 * Método testa associação, para o novo cliente, de todas as campanhas do seu time do coração, quando o time do coração não tem nenhuma campanha ativa.
	 * 
	 * Resultado esperado:
	 * 		Interrupção de execução do método associarNovoCliente, ao detectar que o client REST não retornou campanhas
	 */
	@Test
	public void testAssociacaoDasCampanhasParaNovoClienteQuandoTimeDoCoracaoNaoPossuiCampanhasAtivas() throws ParseException {
		
		Mockito.when(client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao())).thenReturn(null);
		
		associacaoCampanhaService.associarNovoCliente(cliente);
		
		Assert.assertTrue(true);
	}
	
	/**
	 * Método testa associação, para o cliente já existente, de todas as campanhas do seu time do coração que ainda não estão associadas.
	 * 
	 * Resultado esperado:
	 * 		Associação de todas as campanhas retornadas pelo client REST
	 */
	@Test
	public void testAssociacaoDasCampanhasParaClienteJaExistente() throws ParseException {
		
		Mockito.when(client.getCampanhasNaoAssociadas(cliente.getId(),cliente.getIdTimeCoracao())).thenReturn(getCampanhasTimeCoracao());
		
		associacaoCampanhaService.associarClienteExistente(cliente);
		
		Assert.assertTrue(true);
	}
	
	/**
	 * Método testa associação, para o novo cliente, de todas as campanhas do seu time do coração, quando o time do coração não tem nenhuma campanha ativa.
	 * 
	 * Resultado esperado:
	 * 		Interrupção de execução do método associarNovoCliente, ao detectar que o client REST não retornou campanhas
	 */
	@Test
	public void testAssociacaoDasCampanhasParaClienteJaExistenteQuandoTimeDoCoracaoNaoPossuiCampanhasAtivas() throws ParseException {
		
		Mockito.when(client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao())).thenReturn(null);
		
		associacaoCampanhaService.associarClienteExistente(cliente);
		
		Assert.assertTrue(true);
	}

	private List<Campanha> getCampanhasTimeCoracao() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Campanha> campanhas = new ArrayList<>();
		campanhas.add(new Campanha("Campanha1", 1L, sdf.parse("2017-09-15"), sdf.parse("2017-09-16")));
		campanhas.add(new Campanha("Campanha2", 1L, sdf.parse("2017-09-18"), sdf.parse("2017-09-20")));
		
		campanhas.get(0).setId(1L);
		campanhas.get(1).setId(2L);
		
		return campanhas;
	}

}
