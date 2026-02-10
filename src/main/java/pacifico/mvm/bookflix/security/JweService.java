package pacifico.mvm.bookflix.security;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import pacifico.mvm.bookflix.exception.TokenGenerationException;
import pacifico.mvm.bookflix.exception.TokenProcessingException;

@Service	
public final class JweService extends BaseJwtService {

	private final JWEEncrypter jweEncrypter;
	private final JWEDecrypter jweDecrypter;
	private static final JWSHeader JWS_HEADER_RS256;
	private static final JWEHeader JWE_HEADER_RSA_OAEP_256_A256GCM;
	
	static {
		JWS_HEADER_RS256 = new JWSHeader(JWSAlgorithm.RS256);
		JWE_HEADER_RSA_OAEP_256_A256GCM = new JWEHeader.Builder(
				JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM
				)
				.contentType("JWT")
				.build();
	}

	public JweService(JWSSigner jwsSigner, JWSVerifier jwsVerifier, JWEEncrypter jweEncrypter,
			JWEDecrypter jweDecrypter) {
		super(jwsSigner, jwsVerifier);
		this.jweEncrypter = jweEncrypter;
		this.jweDecrypter = jweDecrypter;
	}

	public String generateToken(
			Map<String, Object> claims, 
			String subject, 
			long expiresIn) throws TokenGenerationException {
		Instant now = Instant.now();
		JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
				.issuer("bookflix-jwe")
				.issueTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(expiresIn)))
				.subject(subject);
		addClaimsToBuilder(claimsBuilder, claims);
		JWTClaimsSet claimsSet = claimsBuilder.build();
		SignedJWT signedJWT = new SignedJWT(JWS_HEADER_RS256, claimsSet);
		try {
			signedJWT.sign(jwsSigner);
			JWEObject jweObject = new JWEObject(JWE_HEADER_RSA_OAEP_256_A256GCM, new Payload(signedJWT));
			jweObject.encrypt(jweEncrypter);
			return jweObject.serialize();
		} catch (JOSEException e) {
			throw new TokenGenerationException("Erro ao gerar token");
		}		
	}
	
	private void addClaimsToBuilder(JWTClaimsSet.Builder builder, Map<String, Object> claims) {
		for (Map.Entry<String, Object> entry : claims.entrySet()) {
			builder.claim(entry.getKey(), entry.getValue());
		}
	}
	
	private SignedJWT parseToken(String token) throws ParseException, JOSEException, IllegalStateException {
		JWEObject jweObject = JWEObject.parse(token);
		jweObject.decrypt(jweDecrypter);
		return jweObject.getPayload().toSignedJWT();
	}
	
	private JWTClaimsSet validateToken(SignedJWT signedJWT, Instant now) 
			throws SecurityException, JOSEException, ParseException {
		verifySignature(signedJWT);
		JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
		verifyIfTokenIsFromFuture(claims.getIssueTime().toInstant(), now);
		return claims;
	}
	
	public JWTClaimsSet getClaimsIfIsAValidToken(String token) throws SecurityException, TokenProcessingException {
		Instant now = Instant.now(); 
		JWTClaimsSet claims;
		try {
			claims = validateToken(parseToken(token), now);
		} catch (IllegalStateException | JOSEException | ParseException e) {
			throw new TokenProcessingException("Erro no processamento do token.");
		}
		Date expirationDate = claims.getExpirationTime();
		if (expirationDate == null || now.isAfter(expirationDate.toInstant())) {
			throw new SecurityException("Token expirado.");
		}
		return claims;
	}
	
	public JWTClaimsSet getClaimsIfIsAValidExpiratedToken(String token) throws SecurityException, TokenProcessingException {
		Instant now = Instant.now();
		JWTClaimsSet claims;
		try {
			claims = validateToken(parseToken(token), now);
		} catch (IllegalStateException | JOSEException | ParseException e) {
			throw new TokenProcessingException("Erro no processamento do token.");
		}
		Date expirationDate = claims.getExpirationTime();
		if (expirationDate == null || now.isBefore(expirationDate.toInstant())) {
			throw new SecurityException("Token inválido para essa operação.");
		}
		return claims;
	}
	  
}
