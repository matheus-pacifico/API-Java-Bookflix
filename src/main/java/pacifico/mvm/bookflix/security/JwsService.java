package pacifico.mvm.bookflix.security;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import pacifico.mvm.bookflix.exception.TokenGenerationException;
import pacifico.mvm.bookflix.exception.TokenProcessingException;

@Service	
public final class JwsService extends BaseJwtService {
	  
	private static final JWSHeader JWS_HEADER_RS256;
	
	static {
		JWS_HEADER_RS256 = new JWSHeader(JWSAlgorithm.RS256);
	}

	public JwsService(JWSSigner jwsSigner, JWSVerifier jwsVerifier) {
		super(jwsSigner, jwsVerifier);
	}

	public String generateToken(
			Map<String, Object> claims, 
			String subject, 
			long expiresIn) throws TokenGenerationException {
		Instant now = Instant.now();
		JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
				.issuer("bookflix-jws")
				.issueTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(expiresIn)))
				.subject(subject);
		addClaimsToBuilder(claimsBuilder, claims);
		JWTClaimsSet claimsSet = claimsBuilder.build();
		SignedJWT signedJWT = new SignedJWT(JWS_HEADER_RS256, claimsSet);
		try {
			signedJWT.sign(jwsSigner);
			return signedJWT.serialize();
		} catch (JOSEException e) {
			throw new TokenGenerationException("Erro ao gerar token");
		}
	}
	
	private void addClaimsToBuilder(JWTClaimsSet.Builder builder, Map<String, Object> claims) {
		for (Map.Entry<String, Object> entry : claims.entrySet()) {
			builder.claim(entry.getKey(), entry.getValue());
		}
	}
	
	private SignedJWT parseToken(String token) throws ParseException  {
		return SignedJWT.parse(token);
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
		} catch (JOSEException | ParseException e) {
			throw new TokenProcessingException("Ocorreu um erro interno ao processar o token.");
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
		} catch (JOSEException | ParseException e) {
			throw new TokenProcessingException("Ocorreu um erro interno ao processar o token.");
		}
		Date expirationDate = claims.getExpirationTime();
		if (expirationDate == null || now.isBefore(expirationDate.toInstant())) {
			throw new SecurityException("Token inválido para essa operação.");
		}
		return claims;
	}
	  
}
