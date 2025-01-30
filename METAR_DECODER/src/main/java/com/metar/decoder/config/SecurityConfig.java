//security config class

package com.metar.decoder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// create in-memory users with roles
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails admin = User.withUsername("Kaushik")
				.password(passwordEncoder().encode("Kaushik@123"))
				.roles("ADMIN")
				.build();

		UserDetails user = User.withUsername("user")
				.password(passwordEncoder().encode("user@123"))
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(admin, user);
	}

	// Security configuration for HTTP requests and authentication
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeRequests(auth -> auth
						.requestMatchers("/login", "/logout", "login.html").permitAll()
						.requestMatchers("/api/weather/authorized/decoded").hasAnyRole("ADMIN", "USER")
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults()) // Enable Basic Authentication
				.formLogin(form -> form
						.loginPage("/login.html") // Custom login page
						.loginProcessingUrl("/login") // Login POST request handling
						.defaultSuccessUrl("/api/weather/authorized/decoded", true) // Redirect after successful login
						.failureUrl("/login.html?error=true") // Redirect on failure
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login.html?logout=true")
						.permitAll());

		return http.build();
	}

}