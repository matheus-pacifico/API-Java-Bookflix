package pacifico.mvm.bookflix.configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	@Value("${jws.private.key}")
	private RSAPrivateKey privJws;
	@Value("${jws.public.key}")
	private RSAPublicKey pubJws;
	@Value("${jwe.private.key}")
	private RSAPrivateKey privJwe;
	@Value("${jwe.public.key}")
	private RSAPublicKey pubJwe;

	@Bean
	@Order(2)
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .headers(headers -> headers
	            .frameOptions(frameOptions -> frameOptions.sameOrigin())
	        )
	        .authorizeHttpRequests(auth -> auth
	            .anyRequest().permitAll()
	        )
	        .httpBasic(httpBasic -> httpBasic.disable())
	        .formLogin(form -> form.disable());
	    
	    return http.build();
	}

	@Bean
	@Order(1)
	SecurityFilterChain downloadIFrameFilterChain(HttpSecurity http) throws Exception {
	    http
	        .securityMatcher("/api/v1/arquivo/download/*")
	        .headers(headers -> headers
	            .frameOptions(frameOptions -> frameOptions.disable())
	        );
	    
	    return http.build();
	}
	
	@Bean
	JWSSigner jwsSigner() {
		return new RSASSASigner(privJws);
	}
	  
	@Bean
	JWSVerifier jwsVerifier() {
		return new RSASSAVerifier(pubJws);
	}
	  
	@Bean
	JWEEncrypter jweEncrypter() {
		return new RSAEncrypter(pubJwe);
	}
	  
	@Bean
	JWEDecrypter jweDecrypter() {
		return new RSADecrypter(privJwe);
	}  
	  
}
