package pl.mojastrona.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenToUsernameTokenConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
    private final UserDetailsService userDetailsService;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getClaim(AuthenticationService.JWT_CLAIM_LOGIN));
        return UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());
    }

}
