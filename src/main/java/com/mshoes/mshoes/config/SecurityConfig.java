package com.mshoes.mshoes.config;

import com.mshoes.mshoes.securities.CustomUserDetailsService;
import com.mshoes.mshoes.securities.JwtAuthenticationEntryPoint;
import com.mshoes.mshoes.securities.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

	private final CustomUserDetailsService userDetailsService;

	private final JwtAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationEntryPoint authenticationEntryPoint) {
		this.userDetailsService = userDetailsService;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		return new JwtAuthenticationFilter();
	}
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeHttpRequests()
//						.requestMatchers(HttpMethod.POST,"/auth/**").permitAll()
//						.requestMatchers(HttpMethod.GET, "/products/**").permitAll()
//						.requestMatchers(HttpMethod.GET, "/users/**").permitAll()
//						.requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
						.antMatchers("/**").permitAll()
						.antMatchers("/error/**").permitAll()
						.antMatchers("/login").permitAll()
						.anyRequest().authenticated()
				.and()
						.logout()
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("jwtToken")
						.permitAll();
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	protected void filterChain(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService((userDetailsService)).passwordEncoder((passwordEncoder()));
	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
