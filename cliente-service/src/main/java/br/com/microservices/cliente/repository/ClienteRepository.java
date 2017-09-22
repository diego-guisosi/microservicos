package br.com.microservices.cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.cliente.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
	
	Cliente findByEmail(String email);

}
