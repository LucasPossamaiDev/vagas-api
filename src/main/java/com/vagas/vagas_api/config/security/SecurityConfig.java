package com.vagas.vagas_api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Rotas de autenticação
                        .requestMatchers("/auth/**").permitAll()

                        // Cadastro de candidatos e empresas
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/empresas").permitAll()

                        // Visualização pública de vagas e tecnologias
                        .requestMatchers(HttpMethod.GET, "/vagas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tecnologias/**").permitAll()

                        // Endpoints exclusivos de Empresas
                        .requestMatchers(HttpMethod.POST, "/vagas").hasRole("EMPRESA")
                        .requestMatchers(HttpMethod.PATCH, "/vagas/**").hasRole("EMPRESA")
                        .requestMatchers(HttpMethod.PATCH, "/candidaturas/**").hasRole("EMPRESA")

                        // Endpoints exclusivos de Candidatos
                        .requestMatchers(HttpMethod.POST, "/candidaturas").hasRole("CANDIDATO")

                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
