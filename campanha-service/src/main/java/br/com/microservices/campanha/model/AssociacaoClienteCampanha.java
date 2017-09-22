package br.com.microservices.campanha.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AssociacaoClienteCampanha {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long clienteId;
	
	private Long campanhaId;

	public AssociacaoClienteCampanha() { }
	
	public AssociacaoClienteCampanha(Long clienteId, Long campanhaId) {
		this.clienteId = clienteId;
		this.campanhaId = campanhaId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public Long getCampanhaId() {
		return campanhaId;
	}

	public void setCampanhaId(Long campanhaId) {
		this.campanhaId = campanhaId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campanhaId == null) ? 0 : campanhaId.hashCode());
		result = prime * result + ((clienteId == null) ? 0 : clienteId.hashCode());
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
		AssociacaoClienteCampanha other = (AssociacaoClienteCampanha) obj;
		if (campanhaId == null) {
			if (other.campanhaId != null)
				return false;
		} else if (!campanhaId.equals(other.campanhaId))
			return false;
		if (clienteId == null) {
			if (other.clienteId != null)
				return false;
		} else if (!clienteId.equals(other.clienteId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AssociacaoClienteCampanha [id=" + id + ", clienteId=" + clienteId + ", campanhaId=" + campanhaId + "]";
	}
	
}
