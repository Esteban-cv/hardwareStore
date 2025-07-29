package co.edu.sena.HardwareStore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/login", "/css/**", "/js/**", "/img/**").permitAll().anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/home", true).failureUrl("/login?error=true").permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true").permitAll()
                );

        return http.build();
    }
}
