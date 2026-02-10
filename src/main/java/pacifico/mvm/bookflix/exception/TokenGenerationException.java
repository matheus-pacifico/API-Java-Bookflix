package pacifico.mvm.bookflix.exception;

public class TokenGenerationException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

	public TokenGenerationException(String msg) {
		super(msg);
	}

	public TokenGenerationException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
}
