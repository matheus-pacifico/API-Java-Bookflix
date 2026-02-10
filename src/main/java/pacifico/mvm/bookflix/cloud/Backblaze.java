package pacifico.mvm.bookflix.cloud;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.contentSources.B2ContentTypes;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2DownloadByIdRequest;
import com.backblaze.b2.client.structures.B2DownloadByNameRequest;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2UploadFileRequest;

import jakarta.servlet.http.HttpServletResponse;

public class Backblaze {

    private final B2StorageClient b2Client;
    private final B2Bucket bucket;
 
    Backblaze(B2StorageClient b2Client, B2Bucket bucket)  {
        this.b2Client = b2Client;
		this.bucket = bucket;
    }
	
	public B2FileVersion getFileInfoByName(String fileName) throws B2Exception {
		return b2Client.getFileInfoByName(bucket.getBucketName(), fileName);
	}
	
	public B2FileVersion getFileInfoById(String fileId) throws B2Exception {
		return b2Client.getFileInfo(fileId);
	}

	public B2FileVersion uploadFile(MultipartFile file, String path) throws B2Exception, IOException {
            B2ContentSource contentSource = StreamingB2ContentSource.builder(file).build();
            String contentType = file.getContentType() != null 
            		? file.getContentType() 
            		: B2ContentTypes.APPLICATION_OCTET;
            B2UploadFileRequest uploadRequest = B2UploadFileRequest.builder(
                    bucket.getBucketId(),
                    path,
                    contentType,
                    contentSource
                    ).build();
           return b2Client.uploadSmallFile(uploadRequest);
    }
    
    public void downloadFileByName(String fileName, HttpServletResponse response) throws B2Exception, IOException {
    	B2DownloadByNameRequest request = B2DownloadByNameRequest.builder(bucket.getBucketName(), fileName).build();
    	StreamingB2ContentSink b2ContentSink = new StreamingB2ContentSink(response.getOutputStream(), response);
        b2Client.downloadByName(request, b2ContentSink);
    }
    
    public void downloadFileById(String fileId, HttpServletResponse response) throws B2Exception, IOException {
    	B2DownloadByIdRequest request = B2DownloadByIdRequest.builder(fileId).build();
    	StreamingB2ContentSink b2ContentSink = new StreamingB2ContentSink(response.getOutputStream(), response);
        b2Client.downloadById(request, b2ContentSink);
    }
    
    public void deleteFile(String fileName, String fileId) throws B2Exception {
        b2Client.deleteFileVersion(fileName, fileId);
    }

}
