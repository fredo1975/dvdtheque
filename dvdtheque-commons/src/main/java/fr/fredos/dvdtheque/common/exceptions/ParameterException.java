package fr.fredos.dvdtheque.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.parameter")
public class ParameterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1616834990733238828L;

}
