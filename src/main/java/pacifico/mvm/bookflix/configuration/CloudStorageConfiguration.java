package pacifico.mvm.bookflix.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pacifico.mvm.bookflix.cloud.BackblazeFactory;

@Configuration
public class CloudStorageConfiguration {
	
	@Value("${backblaze.applicationKeyId}")
	private String applicationKeyId;
	@Value("${backblaze.applicationKey}")
	private String applicationKey;
	@Value("${backblaze.userAgent}")
	private String userAgent;

    @Bean
    BackblazeFactory backblazeFactory() {
        return new BackblazeFactory(applicationKeyId, applicationKey, userAgent);
    }
    
}
