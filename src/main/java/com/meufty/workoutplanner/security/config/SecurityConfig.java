package com.meufty.workoutplanner.security.config;

import com.meufty.workoutplanner.filters.JwtRequestFilter;
import com.meufty.workoutplanner.service.MyUserDetailsService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private final JwtRequestFilter jwtRequestFilter;
    @Autowired
    private final LogoutHandler logoutHandler;
    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost", "http://localhost:3000", "http://127.0.0.1", "http://127.0.0.1:3000"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.OPTIONS.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.PATCH.name()));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addExposedHeader("Accept");
        corsConfiguration.addExposedHeader("Content-Type");
        corsConfiguration.addExposedHeader("Referer");
        corsConfiguration.addExposedHeader("User-Agent");
        corsConfiguration.setMaxAge(1800L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        ((request, response, authException) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()
                            );
                        })
                )
                .and()
                .authorizeRequests()
                .antMatchers("/api/v*/registration/**", "/api/v*/login**", "/api/v*/refreshtoken**", "/api/public/home", "/api/public/**")
                .permitAll()
                .antMatchers("/api/v*/admin").hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v*/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                        (request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
}
