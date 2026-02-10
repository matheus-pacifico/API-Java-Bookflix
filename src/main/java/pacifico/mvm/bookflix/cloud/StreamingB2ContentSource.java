package pacifico.mvm.bookflix.cloud;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.contentSources.B2ContentSource;

public class StreamingB2ContentSource implements B2ContentSource {

    private final MultipartFile source;
    private final String sha1OrNull;

    public static Builder builder(MultipartFile source) {
        return new Builder(source);
    }

    public static StreamingB2ContentSource build(MultipartFile source) {
        return builder(source).build();
    }

    private StreamingB2ContentSource(MultipartFile source, String sha1) {
        this.source = source;
        this.sha1OrNull = sha1;
    }

    @Override
    public String getSha1OrNull() throws IOException {
        return sha1OrNull;
    }

    @Override
    public Long getSrcLastModifiedMillisOrNull() throws IOException {
        return Instant.now().toEpochMilli();
    }

    @Override
    public long getContentLength() throws IOException {
        return source.getSize();
    }

    @Override
    public InputStream createInputStream() throws IOException {
        return source.getInputStream();
    }

    public static class Builder {
        private final MultipartFile source;
        private String sha1;

        private Builder(MultipartFile source) {
            this.source = source;
        }

        /**
         * @param sha1 the sha1 for this file.
         * @see B2ContentSource#getSha1OrNull()
         */
        public Builder setSha1(String sha1) {
            this.sha1 = sha1;
            return this;
        }

        public StreamingB2ContentSource build() {
            return new StreamingB2ContentSource(source, sha1);
        }
    }
    
}