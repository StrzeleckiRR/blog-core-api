package pl.mojastrona.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    public static final String JWT_CLAIM_LOGIN = "login";
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expirationInSeconds:1800}")
    private long jwtExpirationInSeconds;



    Jwt authenticate(AuthenticateRequest authenticateRequest){
        AbstractAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .unauthenticated(authenticateRequest.getLogin(), authenticateRequest.getPassword());

        authenticationManager.authenticate(authenticationToken);

        //Generowanie tokenu JWT
        Jwt token = createToken(authenticateRequest.getLogin());
        return token;
    }

    private Jwt createToken(String login) {
        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        JwtClaimsSet.builder()
                                .claim(JWT_CLAIM_LOGIN, login)
                                .expiresAt(Instant.now().plusSeconds(jwtExpirationInSeconds))
                                .build()
                )
        );
    }


}
