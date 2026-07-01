package pl.mojastrona.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    void authenticate(AuthenticateRequest authenticateRequest){
        AbstractAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .unauthenticated(authenticateRequest.getLogin(), authenticateRequest.getPassword());

        authenticationManager.authenticate(authenticationToken);
    }
}
