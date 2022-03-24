package fr.fredos.dvdtheque.rest.dao.specifications.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED, reason = "error.not.implemented")
public class NotImplementedException extends RuntimeException{

	private static final long serialVersionUID = 9098300165728707439L;


}
