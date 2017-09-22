package br.com.microservices.campanha.exception;

public class CampanhaNaoEncontradaException extends Exception {

	/*** Identificador para serialização */
	private static final long serialVersionUID = 1L;
	
	public CampanhaNaoEncontradaException(String mensagem) {
		super(mensagem);
	}

}
