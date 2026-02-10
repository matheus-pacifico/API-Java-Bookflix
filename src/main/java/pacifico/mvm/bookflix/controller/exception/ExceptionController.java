package pacifico.mvm.bookflix.controller.exception;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.backblaze.b2.client.exceptions.B2Exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.FileNotFoundException;
import pacifico.mvm.bookflix.exception.FileUploadException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.exception.StandardError;
import pacifico.mvm.bookflix.exception.TokenProcessingException;
import pacifico.mvm.bookflix.exception.ValidationError;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.NOT_FOUND.value(),
				"Não encontrado", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}

	@ExceptionHandler(DataIntegrityException.class)
	public ResponseEntity<StandardError> dataIntegrity(DataIntegrityException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.BAD_REQUEST.value(),
				"Integridade de dados", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
		ValidationError err = new ValidationError(Instant.now().toEpochMilli(), HttpStatus.UNPROCESSABLE_ENTITY.value(),
				"Erro de validação", "Um ou mais campos estão inválidos.", request.getRequestURI());
		for (FieldError x : e.getBindingResult().getFieldErrors()) {
			err.addError(x.getField(), x.getDefaultMessage());
		}
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<StandardError> illegalArgument(IllegalArgumentException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.BAD_REQUEST.value(),
				"Parâmetro inválido", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}
	
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<StandardError> illegalState(IllegalStateException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.BAD_REQUEST.value(),
				"Estado inválido", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}

	@ExceptionHandler(URISyntaxException.class)
	public ResponseEntity<StandardError> uriSyntax(URISyntaxException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"URI Syntax Exception", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<StandardError> maxUploadSize(MaxUploadSizeExceededException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.BAD_REQUEST.value(),
				"Tamanho do arquivo", "O tamanho máximo do arquivo é de 10MB", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}

	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<StandardError> fileNotFound(FileNotFoundException e, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.NOT_FOUND.value(),
				"Arquivo não Encontrado", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}
	
	@ExceptionHandler(B2Exception.class)
	public ResponseEntity<StandardError> cloudError(B2Exception e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Ocorreu um erro ao acessar o armazenamento em nuvem", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	}
	
	@ExceptionHandler(IOException.class)
	public void fileError(IOException e, HttpServletRequest request,
            HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	}	

	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<StandardError> fileNotFound(FileUploadException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Erro ao enviar arquivo", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	
	}
	
	@ExceptionHandler(TokenProcessingException.class)
	public ResponseEntity<StandardError> tokenProcessError(TokenProcessingException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Erro no processamento do token", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<StandardError> wrongArgumentType(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.BAD_REQUEST.value(),
				"O tipo do parâmetro informado está incorreto", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<StandardError> blankArgument(ConstraintViolationException e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.BAD_REQUEST.value(),
				"Parâmetro inválido", "O parâmetro não pode ser nulo, estar em branco ou estar vazio", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> unkownError(Exception e, HttpServletRequest request) {
		StandardError err = new StandardError(Instant.now().toEpochMilli(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Um erro desconhecido ocorreu", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	}

}
