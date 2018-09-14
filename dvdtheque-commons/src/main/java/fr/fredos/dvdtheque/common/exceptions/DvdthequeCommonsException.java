package fr.fredos.dvdtheque.common.exceptions;

public class DvdthequeCommonsException extends Exception{
	private static final long serialVersionUID = -5901858045281040462L;
	/**
	 * Constructor with error message.
	 * 
	 * @param msg the error message associated with the exception
	 */
	public DvdthequeCommonsException(String msg) {
		super(msg);
	}
	/**
	 * 
	 * @param ex
	 */
	public DvdthequeCommonsException(Exception ex) {
		super(ex);
	}
	/**
	 * Constructor with error message and root cause.
	 * 
	 * @param msg the error message associated with the exception
	 * @param cause the root cause of the exception
	 */
	public DvdthequeCommonsException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
