package com.vmlg.bank.bank.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
            .csrf(csfr -> csfr.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> {
                authorize.requestMatchers(HttpMethod.POST,"/auth/register").permitAll();
                authorize.requestMatchers(HttpMethod.GET,"/users").hasRole("ADMIN");
                authorize.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
                authorize.requestMatchers(HttpMethod.POST,"/transactions").hasRole("ADMIN")
                .anyRequest().authenticated();
            })
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
            // .requestMatchers(mvc.pattern(HttpMethod.POST, "/auth/register")).hasRole("ADMIN")
            // .requestMatchers(mvc.pattern(HttpMethod.GET, "/users")).permitAll()
            // .requestMatchers(mvc.pattern(HttpMethod.GET, "/swagger-ui/**")).permitAll()
            // .anyRequest().authenticated()
            // )
            // .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration auth) throws Exception{
        return auth.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
