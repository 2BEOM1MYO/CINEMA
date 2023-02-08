package com.zb.cinema.config.security;

import com.zb.cinema.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtAuthenticationFilter authenticationFilter;

	@Bean
	public WebSecurityCustomizer configure() {

		return (web) -> web.ignoring().antMatchers("/ignore1");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//프론트엔드가 별도로 존재하여 rest Api로 구성한다고 가정
		http.httpBasic().disable();
		//csrf 사용안함
		http.csrf().disable();

		//URL 인증여부 설정.
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
			.antMatchers("/", "/member/signup", "/member/signin", "/notice/list/**",
				"/notice/detail/**", "/notice/info/**").permitAll()
			//JwtFilter 추가
			.and()
			.addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
