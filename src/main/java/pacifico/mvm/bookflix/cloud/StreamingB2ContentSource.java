package pacifico.mvm.bookflix.cloud;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.contentSources.B2ContentSource;

public class StreamingB2ContentSource implements B2ContentSource {

	private final MultipartFile source;
	private final String sha1OrNull;

	public static Builder builder(MultipartFile source) {
		return new Builder(source);
	}

	private StreamingB2ContentSource(MultipartFile source, String sha1OrNull) {
		this.source = source;
		this.sha1OrNull = sha1OrNull;
	}

	@Override
	public String getSha1OrNull() throws IOException {
		return sha1OrNull;
	}

	@Override
	public Long getSrcLastModifiedMillisOrNull() throws IOException {
		return null;
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
			if (source == null) throw new IllegalArgumentException("Selecione um arquivo válido para envio");
			this.source = source;
		}

		/**
		 * @param sha1 the sha1 for this file.
		 * @see B2ContentSource#getSha1OrNull()
		 */
		public Builder sha1(String sha1) {
			this.sha1 = sha1;
			return this;
		}

		public StreamingB2ContentSource build() {
			return new StreamingB2ContentSource(source, sha1);
		}
	}

}
