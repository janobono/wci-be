package sk.janobono.wci.component.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import sk.janobono.wci.api.service.so.UserSO;
import sk.janobono.wci.common.Authority;
import sk.janobono.wci.config.ConfigProperties;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtToken {

    private final Algorithm algorithm;
    private final Long expiration;
    private final String issuer;

    public JwtToken(ConfigProperties configProperties) {
        this.algorithm = Algorithm.RSA256(
                getPublicKey(configProperties.jwtPublicKey()), getPrivateKey(configProperties.jwtPrivateKey())
        );
        this.expiration = TimeUnit.SECONDS.toMillis(configProperties.jwtExpiration());
        this.issuer = configProperties.issuer();
    }

    private RSAPublicKey getPublicKey(String base64PublicKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RSAPrivateKey getPrivateKey(String base64PrivateKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Long expiresAt(Long issuedAt) {
        return issuedAt + expiration;
    }

    public String generateToken(UserSO user, Long issuedAt) {
        try {
            JWTCreator.Builder jwtBuilder = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(new Date(issuedAt))
                    .withExpiresAt(new Date(expiresAt(issuedAt)));
            // id
            jwtBuilder.withClaim("id", user.id().toString());
            // username
            jwtBuilder.withSubject(user.username());
            // titleBefore
            jwtBuilder.withClaim("titleBefore", user.titleBefore());
            // firstName
            jwtBuilder.withClaim("firstName", user.firstName());
            // midName
            jwtBuilder.withClaim("midName", user.midName());
            // lastName
            jwtBuilder.withClaim("lastName", user.lastName());
            // titleAfter
            jwtBuilder.withClaim("titleAfter", user.titleAfter());
            // email
            jwtBuilder.withClaim("email", user.email());
            // gdpr
            jwtBuilder.withClaim("gdpr", user.gdpr());
            // confirmed
            jwtBuilder.withClaim("confirmed", user.confirmed());
            // enabled
            jwtBuilder.withClaim("enabled", user.enabled());
            // authorities
            jwtBuilder.withArrayClaim("authorities",
                    user.authorities().stream().map(Authority::toString).toArray(String[]::new)
            );
            return jwtBuilder.sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DecodedJWT decodeToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    public UserSO parseToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        // id
        UUID id = UUID.fromString(jwt.getClaims().get("id").asString());
        // username
        String username = jwt.getSubject();
        // titleBefore
        String titleBefore = jwt.getClaims().containsKey("titleBefore") ? jwt.getClaims().get("titleBefore").asString() : null;
        // firstName
        String firstName = jwt.getClaims().get("firstName").asString();
        // midName
        String midName = jwt.getClaims().containsKey("midName") ? jwt.getClaims().get("midName").asString() : null;
        // lastName
        String lastName = jwt.getClaims().get("lastName").asString();
        // titleAfter
        String titleAfter = jwt.getClaims().containsKey("titleAfter") ? jwt.getClaims().get("titleAfter").asString() : null;
        // email
        String email = jwt.getClaims().get("email").asString();
        // gdpr
        Boolean gdpr = jwt.getClaims().get("gdpr").asBoolean();
        // confirmed
        Boolean confirmed = jwt.getClaims().get("confirmed").asBoolean();
        // enabled
        Boolean enabled = jwt.getClaims().get("enabled").asBoolean();
        // authorities
        String[] authorities = jwt.getClaims().get("authorities").asArray(String.class);

        return new UserSO(
                id,
                username,
                titleBefore,
                firstName,
                midName,
                lastName,
                titleAfter,
                email,
                gdpr,
                confirmed,
                enabled,
                Arrays.stream(authorities).map(Authority::byValue).collect(Collectors.toList())
        );
    }
}
