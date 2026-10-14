package com.template.templateapi.web.config;

import com.template.templateapi.common.RoleNames;
import com.template.templateapi.web.exception.SecurityExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.authorization.AuthorizationManagers.anyOf;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter, SecurityExceptionHandler securityExceptionHandler) {
        this.jwtFilter = jwtFilter;
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(this.securityExceptionHandler)
                        .accessDeniedHandler(this.securityExceptionHandler))
                .authorizeHttpRequests(customizeRequests ->
                        customizeRequests
                                // Auth
                                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                                .requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                                .requestMatchers(HttpMethod.GET,"/auth/me").authenticated()
                                .requestMatchers(HttpMethod.POST,"/auth/logout").authenticated()

                                // Users
                                .requestMatchers(HttpMethod.GET, "/users").access(withSuperAdminBypass("LIST_USER"))
                                .requestMatchers(HttpMethod.POST, "/users").access(withSuperAdminBypass("CREATE_USER"))
                                .requestMatchers(HttpMethod.GET, "/users/{id}").access(withSuperAdminBypass("SHOW_USER"))
                                .requestMatchers(HttpMethod.PUT, "/users/{id}").access(withSuperAdminBypass("UPDATE_USER"))
                                .requestMatchers(HttpMethod.DELETE, "/users/{id}").access(withSuperAdminBypass("DELETE_USER"))
                                .requestMatchers(HttpMethod.PUT, "/users/{id}/roles").access(withSuperAdminBypass("ASSIGN_ROLE_USER"))
                                .requestMatchers(HttpMethod.PUT, "/users/{id}/permissions").access(withSuperAdminBypass("ASSIGN_PERMISSION_USER"))

                                // Roles
                                .requestMatchers(HttpMethod.GET, "/roles").access(withSuperAdminBypass("LIST_ROLE"))
                                .requestMatchers(HttpMethod.POST, "/roles").access(withSuperAdminBypass("CREATE_ROLE"))
                                .requestMatchers(HttpMethod.GET, "/roles/{id}").access(withSuperAdminBypass("SHOW_ROLE"))
                                .requestMatchers(HttpMethod.PUT, "/roles/{id}").access(withSuperAdminBypass("UPDATE_ROLE"))
                                .requestMatchers(HttpMethod.DELETE, "/roles/{id}").access(withSuperAdminBypass("DELETE_ROLE"))
                                .requestMatchers(HttpMethod.PUT, "/roles/{id}/permissions").access(withSuperAdminBypass("ASSIGN_PERMISSION_ROLE"))

                                // Permissions
                                .requestMatchers(HttpMethod.GET, "/permissions").access(withSuperAdminBypass("LIST_PERMISSION"))
                                .requestMatchers(HttpMethod.POST, "/permissions").access(withSuperAdminBypass("CREATE_PERMISSION"))
                                .requestMatchers(HttpMethod.GET, "/permissions/{id}").access(withSuperAdminBypass("SHOW_PERMISSION"))
                                .requestMatchers(HttpMethod.PUT, "/permissions/{id}").access(withSuperAdminBypass("UPDATE_PERMISSION"))
                                .requestMatchers(HttpMethod.DELETE, "/permissions/{id}").access(withSuperAdminBypass("DELETE_PERMISSION"))
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private AuthorizationManager<RequestAuthorizationContext> withSuperAdminBypass(String... authorities) {
        return anyOf(
                AuthorityAuthorizationManager.hasRole(RoleNames.SUPER_ADMIN),
                AuthorityAuthorizationManager.hasAnyAuthority(authorities)
        );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
