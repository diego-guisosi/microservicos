package br.com.microservices.cliente.exception;

public class ClienteJaCadastradoException extends Exception {

	/*** Identificador para serialização */
	private static final long serialVersionUID = 1L;
	
	public ClienteJaCadastradoException(String mensagem) {
		super(mensagem);
	}

}
