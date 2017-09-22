package br.com.microservices.campanha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.microservices.campanha.model.AssociacaoClienteCampanha;
import br.com.microservices.campanha.model.Campanha;

@Repository
public interface AssociacaoClienteCampanhaRepository extends JpaRepository<AssociacaoClienteCampanha, Long> {

	@Query("select c from Campanha c, AssociacaoClienteCampanha a where c.id = a.campanhaId and a.clienteId = :clienteId")
	List<Campanha> findCampanhaByClienteId(@Param("clienteId") Long clienteId);
	
}
