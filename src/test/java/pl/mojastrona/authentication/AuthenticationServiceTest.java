package pl.mojastrona.authentication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import pl.mojastrona.BaseUnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthenticationServiceTest extends BaseUnitTest {

    @InjectMocks
    private AuthenticationService underTest;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;


    @Test
    void givenCorrectAuthenticationUser_whenAuthentication_thenCreateToken(){

        String username = "StrzeleckiRR";
        String password = "xhsJkm98&16xXd17p";

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mockito.mock(AbstractAuthenticationToken.class));

       Jwt mockJwt = Mockito.mock(Jwt.class);
       Mockito.when(jwtEncoder.encode(Mockito.any(JwtEncoderParameters.class))).thenReturn(mockJwt);

       AuthenticateRequest request = new AuthenticateRequest(username, password);
       Jwt resultToken = underTest.authenticate(request);

        assertNotNull(resultToken);
        assertEquals(mockJwt, resultToken);

    }

    @Test
    void givenInCorrectAuthenticationUser_whenAuthentication_thenThrowException(){

        String username = "StrzeleckiRR";
        String password = "xhsJkm98&16xXd17p";

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("InCorrect Authentication"));

        AuthenticateRequest request = new AuthenticateRequest(username, password);

        Assertions.assertThrows(BadCredentialsException.class, () -> underTest.authenticate(request));

        Mockito.verifyNoInteractions(jwtEncoder);

    }
}