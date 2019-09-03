package fr.fredos.dvdtheque.rest.controller;

import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import fr.fredos.dvdtheque.rest.error.ApiError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "Malformed JSON request";
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		apiError.setTimestamp(LocalDateTime.now());
		return buildResponseEntity(apiError);
	}
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleServerError(Exception ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		apiError.setTimestamp(LocalDateTime.now());
		apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		return buildResponseEntity(apiError);
	}
	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
