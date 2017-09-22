package br.com.microservices.cliente.exception;

public class ClienteNaoEncontradoException extends Exception {

	/*** Identificador para serialização */
	private static final long serialVersionUID = 1L;
	
	public ClienteNaoEncontradoException(String mensagem) {
		super(mensagem);
	}

}
