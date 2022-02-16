/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig{

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		
		http
			.authorizeExchange()
			.pathMatchers("/**")
			.permitAll()
			.and()
			.csrf()
			.disable()
			.formLogin()
			.loginPage("/logins")
			.disable();
		
		return http.build();
	}
    
}
