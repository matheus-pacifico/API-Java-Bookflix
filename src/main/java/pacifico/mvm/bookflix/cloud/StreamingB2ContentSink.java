package pacifico.mvm.bookflix.cloud;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.backblaze.b2.client.contentHandlers.B2ContentSink;
import com.backblaze.b2.client.contentSources.B2Headers;
import com.backblaze.b2.client.exceptions.B2Exception;

import jakarta.servlet.http.HttpServletResponse;

public class StreamingB2ContentSink implements B2ContentSink {

    private final OutputStream outputStream;
    private HttpServletResponse response;

    public StreamingB2ContentSink(OutputStream outputStream, HttpServletResponse response) {
        this.outputStream = outputStream;
        this.response = response;
    }
    
    @Override
    public void readContent(B2Headers responseHeaders, InputStream in) throws B2Exception, IOException {
        response.setContentLengthLong(responseHeaders.getContentLength());
        int BUFFER_SIZE = 8192; // 8KB
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            String msg = e.getMessage();

            // Client aborted the download
            if (msg != null && (
                    msg.contains("Broken pipe") ||
                    msg.contains("Connection reset by peer") ||
                    msg.contains("Stream closed")
            )) {
                return;
            }

            throw e;
        }
    }
 
}
