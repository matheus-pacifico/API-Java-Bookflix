package pacifico.mvm.bookflix.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.persistence.Tuple;
import jakarta.servlet.http.HttpServletResponse;
import pacifico.mvm.bookflix.cloud.Backblaze;
import pacifico.mvm.bookflix.cloud.BackblazeFactory;
import pacifico.mvm.bookflix.exception.FileNotFoundException;
import pacifico.mvm.bookflix.exception.FileUploadException;
import pacifico.mvm.bookflix.exception.TokenGenerationException;
import pacifico.mvm.bookflix.repository.ArquivoRepository;
import pacifico.mvm.bookflix.security.JwsService;

@Service
public class ArquivoService {
	
	private final ArquivoRepository arquivoRepository;
	private final JwsService jwsService;
	private final Backblaze backblaze;

    private static final Tika tika = new Tika();
		
	public ArquivoService(
			ArquivoRepository arquivoRepository, 
			JwsService jwsService,
			BackblazeFactory factory,
			@Value("${backblaze.bucketName}") String bucketName
			) throws B2Exception {
		this.arquivoRepository = arquivoRepository;
		this.jwsService = jwsService;
        this.backblaze = factory.create(bucketName);
	}
	
	private void validatePdfFile(MultipartFile file) throws IOException, IllegalArgumentException {
	    if (file.isEmpty()) {
	        throw new IllegalArgumentException("O arquivo está vazio");
	    }
	    try (InputStream input = new BufferedInputStream(file.getInputStream())) {
	        input.mark(1024); 
	        if (!"application/pdf".equals(tika.detect(input))) {
	            throw new IllegalArgumentException("O arquivo tem que ser do tipo PDF!");
	        }
	        input.reset();
	    }
	}
	
	private String generateUniqueFileName(String extension) {
	    return Instant.now().toEpochMilli() 
	            + UUID.randomUUID().toString().substring(24, 36) 
	            + (extension.startsWith(".") ? extension : "." + extension);
	}
	
	private Map<String, Object> uploadFileAndGetFileInfo(MultipartFile file) throws IOException, B2Exception {
		validatePdfFile(file);
		String filePath = "docs/" + generateUniqueFileName("pdf");
		String rawFileName = file.getOriginalFilename();
		String fileName = (rawFileName != null && !rawFileName.isBlank()) 
		    ? StringUtils.getFilename(rawFileName) 
		    : "document.pdf";
		
		String fileId = backblaze.uploadFile(file, filePath).getFileId();
        
		Map<String, Object> fileInfo = new HashMap<>();
		fileInfo.put("name", fileName);
		fileInfo.put("path", filePath);
		fileInfo.put("id", fileId);
		return fileInfo;
	}

	public String uploadFile(MultipartFile file) throws IOException, B2Exception {
		final long TOKEN_VALIDITY = 7200L; // in seconds (2 hours)
		try {
			return jwsService.generateToken(
					uploadFileAndGetFileInfo(file), 
                    "file-upload", 
                    TOKEN_VALIDITY);
		} catch (TokenGenerationException e) {
			throw new FileUploadException("Erro ao gerar o token do arquivo");
		}
	}
	
	private void requireFileIdentifier(String value) {
	    if (value == null || value.isBlank()) {
	        throw new FileNotFoundException("O arquivo não foi encontrado");
	    }
	}

    public void downloadFileByObraIfsn(String ifsn, HttpServletResponse response) throws IOException {
    	String filePath = arquivoRepository.getCaminhoArquivoByObraIfsn(ifsn);
    	requireFileIdentifier(filePath);
    	try {
    		backblaze.downloadFileByName(filePath, response);
    	} catch (B2Exception e) {
    		throw new FileNotFoundException("O arquivo não foi encontrado");
    	}
    }
    
    public void downloadFileById(String fileId, HttpServletResponse response) throws IOException {
    	requireFileIdentifier(fileId);
		try {
			backblaze.downloadFileById(fileId, response);
		} catch (B2Exception e) {
			throw new FileNotFoundException("O arquivo não foi encontrado");
		}
    }
	
	public void deleteFile(String path, String id) throws B2Exception {
		if (path == null || id == null || path.isBlank() || id.isBlank()) {
			throw new IllegalArgumentException("Arquivo inválido");
		}
		backblaze.deleteFile(path, id);
	}
    
	public void deleteFileByIfsnAndFileName(String ifsn, String originalFileName) throws B2Exception {
		Tuple fileInfo = arquivoRepository.getPathAndIdToDeleteFile(ifsn, originalFileName);
		if(fileInfo == null) {
			throw new IllegalArgumentException("Não foi possível verificar o arquivo a ser excluído");
		}
		deleteFile(fileInfo.get("caminho_arquivo", String.class), 
				fileInfo.get("id_arquivo", String.class));
	}
	
	public void deleteFilesByTokens(Set<String> tokens) throws B2Exception, Exception {
		Map<String, String> filesInfo = new HashMap<>();
		tokens = tokens.stream().limit(40).collect(Collectors.toSet());
		for (String token : tokens) {
			if (token == null || token.isBlank()) {
				continue;
			}
			try {
				JWTClaimsSet fileInfo = jwsService.getClaimsIfIsAValidExpiratedToken(token);
				filesInfo.put(fileInfo.getClaim("id").toString(), fileInfo.getClaim("path").toString());
			} catch (Exception e) {
				
			}
		}
		if (filesInfo == null || filesInfo.isEmpty()) {
			return;
		}
		Set<String> nonRelatedFiles = arquivoRepository.getUnregisteredFilesIds(filesInfo.keySet().toArray(new String[0]));
		if (nonRelatedFiles == null || nonRelatedFiles.isEmpty()) {
			return;
		}
		deleteMultipleFiles(nonRelatedFiles, filesInfo);
	}
	
	public void deleteMultipleFiles(Set<String> fileIds, Map<String, String> filesInfo) {
	    ExecutorService executor = Executors.newFixedThreadPool(10);
	    for (String fileId : fileIds) {
	        executor.submit(() -> {
	            try {
	            	deleteFile(filesInfo.get(fileId).toString(), fileId);
	            } catch (Exception e) {
	            	System.out.println("Erro ao deletar arquivo: " + fileId);
	            }
	        });
	    }
	    executor.shutdown();
	}
	
}
