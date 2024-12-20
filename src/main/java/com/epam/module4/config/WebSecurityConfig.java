package com.epam.module4.config;

import com.epam.module4.domain.Authority;
import com.epam.module4.domain.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        HttpStatusEntryPoint unauthorizedEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
        http
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/users/register").permitAll()
                                .requestMatchers("/users/create").hasAuthority(Authority.CREATE.name())
                                .requestMatchers("/users").hasAnyRole(Role.ADMIN.name(), Role.OBSERVER.name())
                                .requestMatchers( "/users/access/**").hasAuthority(Authority.MANAGE_ACCESS.name())
                                .requestMatchers(HttpMethod.GET, "/users/**").hasAuthority(Authority.READ.name())
                                .requestMatchers(HttpMethod.PUT, "/users/**").hasAuthority(Authority.UPDATE.name())
                                .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority(Authority.DELETE.name())
                                .requestMatchers("/users/**").hasRole(Role.USER.name())
                                .anyRequest().permitAll()
                )
                .formLogin(form -> form.defaultSuccessUrl("/users/hello")
                        .failureHandler(new AuthenticationEntryPointFailureHandler(unauthorizedEntryPoint)))
                .logout(LogoutConfigurer::permitAll)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(conf -> conf.authenticationEntryPoint(unauthorizedEntryPoint));
        return http.build();
    }


}
