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
    private final HttpServletResponse response;

    public StreamingB2ContentSink(OutputStream outputStream, HttpServletResponse response) {
        this.outputStream = outputStream;
        this.response = response;
    }
    
    @Override
    public void readContent(B2Headers responseHeaders, InputStream in) throws B2Exception, IOException {
        response.setContentLengthLong(responseHeaders.getContentLength());
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
    }
 
}
