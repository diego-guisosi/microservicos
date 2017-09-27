package br.com.microservices.cliente.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import br.com.microservices.cliente.Application;
import br.com.microservices.cliente.client.CampanhaServiceClient;
import br.com.microservices.cliente.model.Cliente;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Application.class, loader=AnnotationConfigContextLoader.class)
@DataJpaTest
public class ClienteRepositoryTest {
	
	@MockBean(value = {CampanhaServiceClient.class})
	@Qualifier("br.com.microservices.cliente.client.CampanhaServiceClient")
	private CampanhaServiceClient client;
	
	@Autowired
	private ClienteRepository repository;

	@Test
	public void testFindByEmailCorrect() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		repository.save(new Cliente("James Bond", "jamesbond007@gmail.com", sdf.parse("1988-05-05"), 1L)); 
		repository.save(new Cliente("Jannis Bind", "jannisbind@yahoo.com", sdf.parse("1993-02-27"), 1L)); 
		
		Cliente jamesBond = repository.findByEmail("jamesbond007@gmail.com");
		Assert.assertTrue(jamesBond.getNome().equals("James Bond"));
		
		Cliente jannisBind = repository.findByEmail("jannisbind@yahoo.com");
		Assert.assertTrue(jannisBind.getNome().equals("Jannis Bind"));
	}
	
	@Test
	public void testFindByEmailIncorrect() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		repository.save(new Cliente("James Bond", "jamesbond007@gmail.com", sdf.parse("1988-05-05"), 1L));
		
		Cliente jamesBond = repository.findByEmail("jamesbondgoldeneye@gmail.com");
		Assert.assertNull(jamesBond);
		
	}
	

}
