package br.com.microservices.cliente.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
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
import br.com.microservices.cliente.repository.ClienteRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Application.class, loader=AnnotationConfigContextLoader.class)
@DataJpaTest
public class ClienteServiceTest {
	
	
	@MockBean(value = {CampanhaServiceClient.class})
	@Qualifier("br.com.microservices.cliente.client.CampanhaServiceClient")
	private CampanhaServiceClient client;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ClienteService service;
	
	/**
	 * Método testa o cadastro de novo cliente, quando seu time do coração possui campanhas ativas.
	 * 
	 * Resultado esperado:
	 * 		Que o cliente seja cadastrado normalmente e que as associações sejam realizadas, utilizando-se o client REST do serviço de cadastro/associação de campanhas
	 */
	@Test
	public void testCadastrarClienteInexistenteQuandoTimeDoCoracaoPossuiCampanhasAtivas() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Cliente cliente = new Cliente("James Bond", "jamesbond007@gmail.com", sdf.parse("1988-05-05"), 1L);
		
		AssociacaoClienteCampanha associacao1 = new AssociacaoClienteCampanha(cliente.getId(), 1L);
		AssociacaoClienteCampanha associacao2 = new AssociacaoClienteCampanha(cliente.getId(), 2L);
		
		Mockito.when(client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao())).thenReturn(getCampanhasTimeCoracao());
		Mockito.when(client.associarClienteCampanha(associacao1)).thenReturn(associacao1);
		Mockito.when(client.associarClienteCampanha(associacao2)).thenReturn(associacao2);
		
		Cliente clienteCadastrado = service.cadastrar(cliente);
		Assert.assertNotNull(clienteCadastrado);
		Assert.assertNotNull(clienteCadastrado.getId()); //Se o ID retornar, é sinal de que o objeto foi persistido.
	}
	
	/**
	 * Método testa o cadastro de novo cliente, quando seu time do coração não possui campanhas ativas.
	 * 
	 * Resultado esperado:
	 * 		Que o cliente seja cadastrado normalmente sem as associações
	 */
	@Test
	public void testCadastrarClienteInexistenteQuandoTimeDoCoracaoNaoPossuiCampanhasAtivas() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Cliente cliente = new Cliente("Jannis Joplin", "jannis@gmail.com", sdf.parse("1963-05-05"), 2L); //Time do coração diferente do time retornado pelo método getCampanhasTimeCoracao()
		
		AssociacaoClienteCampanha associacao1 = new AssociacaoClienteCampanha(cliente.getId(), 1L);
		AssociacaoClienteCampanha associacao2 = new AssociacaoClienteCampanha(cliente.getId(), 2L);
		
		Mockito.when(client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao())).thenReturn(getCampanhasTimeCoracao());
		Mockito.when(client.associarClienteCampanha(associacao1)).thenReturn(associacao1);
		Mockito.when(client.associarClienteCampanha(associacao2)).thenReturn(associacao2);
		
		Cliente clienteCadastrado = service.cadastrar(cliente);
		Assert.assertNotNull(clienteCadastrado);
		Assert.assertNotNull(clienteCadastrado.getId()); //Se o ID retornar, é sinal de que o objeto foi persistido.
	}
	
	/**
	 * Método testa atualização de cadastro de cliente existente, quando seu time do coração possui campanhas ativas.
	 * 
	 * Resultado esperado:
	 * 		Que sejam realizadas associações com as campanhas que o cliente ainda não possui
	 */
	@Test
	public void testCadastrarClienteExistenteQuandoTimeDoCoracaoPossuiCampanhasAtivas() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Cliente cliente = new Cliente("James Bond", "jamesbond007@gmail.com", sdf.parse("1988-05-05"), 1L);
		
		clienteRepository.save(cliente);
		
		AssociacaoClienteCampanha associacao1 = new AssociacaoClienteCampanha(cliente.getId(), 1L);
		AssociacaoClienteCampanha associacao2 = new AssociacaoClienteCampanha(cliente.getId(), 2L);
		
		Mockito.when(client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao())).thenReturn(getCampanhasTimeCoracao());
		Mockito.when(client.associarClienteCampanha(associacao1)).thenReturn(associacao1);
		Mockito.when(client.associarClienteCampanha(associacao2)).thenReturn(associacao2);
		
		Cliente clienteCadastrado = service.cadastrar(cliente);
		Assert.assertNotNull(clienteCadastrado);
		Assert.assertNotNull(clienteCadastrado.getId()); //Se o ID retornar, é sinal de que o objeto foi persistido.
	}
	
	/**
	 * Método testa atualização de cadastro de cliente existente, quando seu time do coração não possui campanhas ativas a que o cliente ainda não esteja associado.
	 * 
	 * Resultado esperado:
	 * 		Nada será alterado. O cliente continuará associado às mesmas campanhas, uma vez que nenhuma nova campanha foi encontrada.
	 */
	@Test
	public void testCadastrarClienteExistenteQuandoTimeDoCoracaoNaoPossuiCampanhasAtivas() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Cliente cliente = new Cliente("Jannis Joplin", "jannis@gmail.com", sdf.parse("1963-05-05"), 2L);  //Time do coração diferente do time retornado pelo método getCampanhasTimeCoracao()
		
		clienteRepository.save(cliente);
		
		AssociacaoClienteCampanha associacao1 = new AssociacaoClienteCampanha(cliente.getId(), 1L);
		AssociacaoClienteCampanha associacao2 = new AssociacaoClienteCampanha(cliente.getId(), 2L);
		
		Mockito.when(client.getCampanhasTimeCoracao(cliente.getIdTimeCoracao())).thenReturn(getCampanhasTimeCoracao());
		Mockito.when(client.associarClienteCampanha(associacao1)).thenReturn(associacao1);
		Mockito.when(client.associarClienteCampanha(associacao2)).thenReturn(associacao2);
		
		Cliente clienteCadastrado = service.cadastrar(cliente);
		Assert.assertNotNull(clienteCadastrado);
		Assert.assertNotNull(clienteCadastrado.getId()); //Se o ID retornar, é sinal de que o objeto foi persistido.
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
