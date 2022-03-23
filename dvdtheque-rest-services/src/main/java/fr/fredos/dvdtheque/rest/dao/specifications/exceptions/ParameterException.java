package fr.fredos.dvdtheque.rest.dao.specifications.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.parameter")
public class ParameterException extends RuntimeException{

	private static final long serialVersionUID = -6678071242278628956L;

}
