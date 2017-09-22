package br.com.microservices.cliente.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Campanha {
	
	@Id
	private Long id;
	
	private Long idTimeCoracao;
	
	private String nome;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date inicioDataVigencia;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date fimDataVigencia;
	
	public Campanha() {	}
	
	public Campanha(String nome, Long idTimeCoracao, Date inicioDataVigencia, Date fimDataVigencia) {
		this.nome = nome;
		this.idTimeCoracao = idTimeCoracao;
		this.inicioDataVigencia = inicioDataVigencia;
		this.fimDataVigencia = fimDataVigencia;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdTimeCoracao() {
		return idTimeCoracao;
	}

	public void setIdTimeCoracao(Long idTimeCoracao) {
		this.idTimeCoracao = idTimeCoracao;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getInicioDataVigencia() {
		return inicioDataVigencia;
	}

	public void setInicioDataVigencia(Date inicioDataVigencia) {
		this.inicioDataVigencia = inicioDataVigencia;
	}

	public Date getFimDataVigencia() {
		return fimDataVigencia;
	}

	public void setFimDataVigencia(Date fimDataVigencia) {
		this.fimDataVigencia = fimDataVigencia;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fimDataVigencia == null) ? 0 : fimDataVigencia.hashCode());
		result = prime * result + ((idTimeCoracao == null) ? 0 : idTimeCoracao.hashCode());
		result = prime * result + ((inicioDataVigencia == null) ? 0 : inicioDataVigencia.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Campanha other = (Campanha) obj;
		if (fimDataVigencia == null) {
			if (other.fimDataVigencia != null)
				return false;
		} else if (!fimDataVigencia.equals(other.fimDataVigencia))
			return false;
		if (idTimeCoracao == null) {
			if (other.idTimeCoracao != null)
				return false;
		} else if (!idTimeCoracao.equals(other.idTimeCoracao))
			return false;
		if (inicioDataVigencia == null) {
			if (other.inicioDataVigencia != null)
				return false;
		} else if (!inicioDataVigencia.equals(other.inicioDataVigencia))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Campanha [id=" + id + ", idTimeCoracao=" + idTimeCoracao + ", nome=" + nome + ", inicioDataVigencia="
				+ inicioDataVigencia + ", fimDataVigencia=" + fimDataVigencia + "]";
	}

}
