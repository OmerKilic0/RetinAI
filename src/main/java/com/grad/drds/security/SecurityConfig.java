package com.grad.drds.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.grad.drds.entity.User_;
import com.grad.drds.repository.UserRepository;

@Configuration
public class SecurityConfig {
	
	private CustomAuthenticationSuccessHandler successHandler;
	
	public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
		this.successHandler = successHandler;
	}
	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
	    return username -> {
	        User_ user = userRepository.findByEmail(username);
	        if (user == null) throw new UsernameNotFoundException("User not found");
	        String role = user.isAdmin() ? "ADMIN" : "USER";

	        return new org.springframework.security.core.userdetails.User(
	            user.getEmail(),
	            user.getPassword(),
	            List.of(new SimpleGrantedAuthority("ROLE_" + role))
	        );
	    };
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@SuppressWarnings("removal")
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/h2-console/**", 
                "/signup", 
                "/login",
                "/",
                "/css/**",
                "/assets/**",
                "/js/**",
                "/images/**",
                "/fonts/**"
            ).permitAll()
            .requestMatchers(HttpMethod.POST, "/signup").permitAll()
            .requestMatchers(HttpMethod.POST, "/submit").permitAll()
            .requestMatchers(HttpMethod.POST, "/generate-pdf").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .successHandler(successHandler)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        )
        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/signup", "/login", "/logout", "/submit", "/generate-pdf"))
        .headers(headers -> headers.frameOptions().disable());

    return http.build();
	}
}
