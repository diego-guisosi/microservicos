package br.com.microservices.campanha.exception;

public class TodasCampanhasJaAssociadasException extends Exception {

	/*** Identificador para serialização */
	private static final long serialVersionUID = 1L;
	
	public TodasCampanhasJaAssociadasException(String mensagem) {
		super(mensagem);
	}

}
