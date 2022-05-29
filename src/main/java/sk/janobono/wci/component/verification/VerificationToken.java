package sk.janobono.wci.component.verification;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import sk.janobono.wci.config.ConfigProperties;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class VerificationToken {

    private final Algorithm algorithm;
    private final String issuer;

    public VerificationToken(ConfigProperties configProperties) {
        this.algorithm = Algorithm.RSA256(
                getPublicKey(configProperties.jwtPublicKey()), getPrivateKey(configProperties.jwtPrivateKey())
        );
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

    public String generateToken(Map<String, String> data, Long issuedAt, Long expiresAt) {
        try {
            JWTCreator.Builder jwtBuilder = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(new Date(issuedAt))
                    .withExpiresAt(new Date(expiresAt));
            data.forEach(jwtBuilder::withClaim);
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

    public Map<String, String> parseToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        Map<String, String> result = new HashMap<>();
        jwt.getClaims().forEach((key, value) -> result.put(key, value.asString()));
        return result;
    }
}
