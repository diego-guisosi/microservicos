package br.com.microservices.campanha.exception;

public class NotificacaoException extends Exception {

	/*** Identificador para serialização */
	private static final long serialVersionUID = 1L;

	public NotificacaoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
