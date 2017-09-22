package br.com.microservices.campanha.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.microservices.campanha.model.Campanha;

@Repository
public interface CampanhaRepository extends JpaRepository<Campanha, Long>{
	
	List<Campanha> findByIdNotAndIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(
			Long id, Long idTimeCoracao, Date inicioDataVigencia, Date fimDataVigencia);
	
	List<Campanha> findByIdTimeCoracaoAndInicioDataVigenciaLessThanEqualAndFimDataVigenciaGreaterThanEqualOrderByFimDataVigenciaAsc(Long idTimeCoracao, Date inicioDataVigencia, Date fimDataVigencia);
	
	List<Campanha> findByIdTimeCoracaoAndFimDataVigenciaGreaterThanEqual(Long idTimeCoracao, Date dataAtual);
	
	List<Campanha> findCampanhaByIdTimeCoracaoAndIdNotIn(Long idTimeCoracao, List<Long> campanhaIds);
	
	Campanha findByIdAndFimDataVigenciaGreaterThanEqual(Long id, Date dataAtual);
	
}
