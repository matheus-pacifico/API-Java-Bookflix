package pacifico.mvm.bookflix.exception;

public class TokenProcessingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public TokenProcessingException(String msg) {
        super(msg);
    }

    public TokenProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
