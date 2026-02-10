package pacifico.mvm.bookflix.exception;

public class FileUploadException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

	public FileUploadException(String msg) {
		super(msg);
	}

	public FileUploadException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
