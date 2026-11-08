package pacifico.mvm.bookflix.controller;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.exceptions.B2Exception;

import jakarta.servlet.http.HttpServletResponse;
import pacifico.mvm.bookflix.dto.UploadResponseDTO;
import pacifico.mvm.bookflix.service.ArquivoService;

@RestController
@RequestMapping(value = "/api/v1/arquivo")
public class ArquivoController {

	private final ArquivoService arquivoService;
	private final String SECRET_DELETE_FILE;
	
	public ArquivoController(ArquivoService arquivoService, 
			@Value("${secret-delete-file}") String secretDeleteFile) {
		this.arquivoService = arquivoService;
		this.SECRET_DELETE_FILE = secretDeleteFile;
	}

	@PostMapping(value = "/upload")
	public ResponseEntity<UploadResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) {
		String token = arquivoService.upload(file);
		return ResponseEntity.status(HttpStatus.CREATED).body(new UploadResponseDTO(token));
	}

	@GetMapping(value = "/download/{fileIdentifier}")
	public void downloadFile(
			@PathVariable String fileIdentifier,
			@RequestParam(defaultValue = "false", name = "i") boolean fetchByIfsn,
			@RequestParam(defaultValue = "false") boolean forceDownload,
			@RequestParam(defaultValue = "document", name = "n") String fileNameFromIfsn,
			HttpServletResponse response)
					throws IOException, B2Exception {
		String fileName = !fetchByIfsn ? fileNameFromIfsn : fileIdentifier;
		response.setContentType("application/pdf");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				(forceDownload ? "attachment" : "inline") + "; filename=\"" + (fileName + ".pdf") + "\"");
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
		response.setHeader(HttpHeaders.PRAGMA, "no-cache");
		response.setHeader(HttpHeaders.EXPIRES, "0");
		if (!fetchByIfsn) { 
			arquivoService.downloadById(fileIdentifier, response);
		} else {
			arquivoService.downloadByObraIfsn(fileIdentifier, response);
		}
    }

	@DeleteMapping(value = "/delete")
	public ResponseEntity<Void> deleteArquivo(@RequestParam String ifsn, @RequestParam(name = "ofn") String originalFileName) throws B2Exception {
		arquivoService.deleteByIfsnAndFileName(ifsn, originalFileName);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/delete-files-tokens")
	public ResponseEntity<?> deleteArquivosByTokens(
			@RequestBody Set<String> tokens,
			@RequestHeader("X-SECRET") String secret) {
		if (tokens == null || tokens.isEmpty()) {
			return ResponseEntity.noContent().build();
		} 	
		if (!SECRET_DELETE_FILE.equals(secret)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Secret inválida.");
		}
		CompletableFuture.runAsync(() -> {
			try {
				arquivoService.deleteByTokens(tokens);
			} catch (Exception e) { }
	    });
	    return ResponseEntity.accepted().build();
	}

}
