package com.shop.system.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.shop.system.security.resource.AuthenticationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtTokenService {

	private final static Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

	private static final Duration JWT_TOKEN_VALIDITY = Duration.ofHours(12);

	private final Algorithm hmac512;

	private final JWTVerifier verifier;

	public JwtTokenService(@Value("${jwt.secret}") final String secret) {
		this.hmac512 = Algorithm.HMAC512(secret);
		this.verifier = JWT.require(this.hmac512).build();
	}

	public AuthenticationResponse generateToken(final UserDetails userDetails) {
		final Instant exprires = Instant.now().plusMillis(JWT_TOKEN_VALIDITY.toMillis());
		String acessToken = JWT.create().withSubject(userDetails.getUsername()).withIssuer("app")
				.withIssuedAt(Instant.now()).withExpiresAt(exprires).sign(this.hmac512);
		return AuthenticationResponse.of(acessToken, exprires);
	}

	public String validateTokenAndGetUsername(final String token) {
		try {
			return verifier.verify(token).getSubject();
		}
		catch (final JWTVerificationException verificationEx) {
			logger.warn("token invalid: {}", verificationEx.getMessage());
			return null;
		}
	}

}
