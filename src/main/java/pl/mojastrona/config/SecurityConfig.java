package pl.mojastrona.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.mojastrona.authentication.JwtTokenToUsernameTokenConverter;
import pl.mojastrona.user.UserRole;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity()
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenToUsernameTokenConverter converter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        //Publiczne dla każdego
                        .requestMatchers(HttpMethod.POST, "/api/users", "/api/posts/find", "/api/authentication").permitAll()
                        // Publiczne przeglądanie postów i komentarzy przez gości
                        .requestMatchers(HttpMethod.GET, "/api/posts/*", "/api/posts", "/api/comments", "/api/comments/*").permitAll()

                        // Dla zalogowanych USER/ADMIN wymagany TokenJWT
                        .requestMatchers(HttpMethod.GET, "/api/posts/findForLogged").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/profile", "/api/posts", "/api/comments").authenticated()

                        // Tylko dla ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/groups-info").hasAuthority(UserRole.ADMIN.name())

                        // Wszystko inne wymaga zalogowania
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));

        return http.build();
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder){
//
//        UserDetails user1 = User.withUsername("StrzeleckiRR")
//                .password(passwordEncoder.encode("marcin123"))
//                .authorities("ADMIN")
//                .build();
//
//        UserDetails user2 = User.withUsername("MarcinS")
//                .password(passwordEncoder.encode("marcin321"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtEncoder jwtEncoder(){
       return new NimbusJwtEncoder(new ImmutableSecret<>(secret.getBytes()));
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        SecretKey secretKey = new SecretKeySpec(
               secret.getBytes(), "AES"
        );
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
