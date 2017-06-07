package br.com.alm.workflow.exception;
/**
 * @author David Makson do Nascimento Tavares
 */
public class BusinesException extends Exception{
 
	private static final long serialVersionUID = -890236152164118223L;

	public BusinesException() {
		throw new RuntimeException("Não foi possivel executar o acesso ao banco de dados");
	}
	
	public BusinesException(String message){
		super(message);
	}
	
	public BusinesException(String message, Throwable cause){
		super(message,cause);
	}
}
