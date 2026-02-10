package pacifico.mvm.bookflix.security;

import java.time.Instant;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import pacifico.mvm.bookflix.exception.TokenGenerationException;
import pacifico.mvm.bookflix.exception.TokenProcessingException;

public sealed abstract class BaseJwtService permits JweService, JwsService {
	
	protected final JWSSigner jwsSigner;
	protected final JWSVerifier jwsVerifier;

	public BaseJwtService(JWSSigner jwsSigner, JWSVerifier jwsVerifier) {
		this.jwsSigner = jwsSigner;
		this.jwsVerifier = jwsVerifier;
	}

	/**
	 * @param expiresIn Time until expiration, in seconds.
	 * 
	 * @throws TokenGenerationException If the token couldn't be generated.
	 */
	public abstract String generateToken(Map<String, Object> claims, String subject, long expiresIn) throws TokenGenerationException;
	
	public abstract JWTClaimsSet getClaimsIfIsAValidToken(String token) throws SecurityException, TokenProcessingException;
	
	public abstract JWTClaimsSet getClaimsIfIsAValidExpiratedToken(String token) throws SecurityException, TokenProcessingException;
	
	protected void verifySignature(SignedJWT signedJWT) throws SecurityException, JOSEException {
		if (signedJWT == null) {
			throw new JOSEException("Token descriptografado não é um JWT assinado.");
		}
		if (!signedJWT.verify(jwsVerifier)) {
			throw new SecurityException("Assinatura JWT inválida.");
		}
	}
	
	protected void verifyIfTokenIsFromFuture(Instant issuedAt, Instant now) throws SecurityException {
		if (issuedAt == null || now.isBefore(issuedAt)) {
			throw new SecurityException("Token emitido no futuro (inválido).");
		}
	}
	
} 
