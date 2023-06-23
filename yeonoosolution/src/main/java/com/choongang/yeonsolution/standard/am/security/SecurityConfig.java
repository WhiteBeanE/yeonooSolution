package com.choongang.yeonsolution.standard.am.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Security설정파일
 * @author 장성태
 * @since 2023/05/23
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Autowired // 사용자 로그인 성공 시 호출되는 핸들러
	private AuthenticationSuccessHandler userLoginSuccessHandler;
	@Autowired // 인증되지 않은 요청이 접근하려고 할 때 호출되는 핸들러
	private AuthenticationEntryPoint customAuthenticationEntryPoint;
	@Autowired // 사용자 로그인 실패 시 호출되는 핸들러
	private AuthenticationFailureHandler userLoginFailureHandler;
	@Autowired // 인증된 사용자가 요청에 접근할 수 없을 때 호출되는 핸들러
	private AccessDeniedHandler customAccessDeniedHandler;
	@Autowired // 사용자의 상세 정보를 로드하는 인터페이스의 구현체
	private UserDetailsService memberDetailsServiceImpl;
	
	/**
	 * SecurityFilterChain 설정
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
		
		httpSecurity
					.httpBasic().disable() // HTTP 기본 인증을 비활성화
					.csrf().disable() // csrf 보호 기능을 비활성화
				    .cors() // cors 설정을 활성화
			    	.and()
					//인증 허용 범위 설정
					.authorizeRequests() //  요청에 대한 인가를 설정
						.antMatchers("/images/**").permitAll() //static resources 인증 제외
						.antMatchers("/standard/login", "/standard/not-authorized").permitAll() //로그인 페이지, 권한 부족 페이지
						.antMatchers(HttpMethod.POST, "/**").hasAnyRole("ADMIN", "MANAGER") // POST 요청은 ADMIN과 MANAGER 권한을 가진 사용자 모두 접근 가능
						.antMatchers(HttpMethod.PUT, "/**").hasAnyRole("ADMIN", "MANAGER") // PUT 요청은 ADMIN과 MANAGER 권한을 가진 사용자 모두 접근 가능
						.antMatchers(HttpMethod.DELETE, "/**").hasAnyRole("ADMIN", "MANAGER") // DELETE 요청은 ADMIN과 MANAGER 권한을 가진 사용자 모두 접근 가능
						.antMatchers(HttpMethod.PATCH, "/**").hasAnyRole("ADMIN", "MANAGER") // PATCH 요청은 ADMIN과 MANAGER 권한을 가진 사용자 모두 접근 가능
						.antMatchers("/standard/**").authenticated() // 로그인 인증을 필오하나 특정 권한은 확인하지 않음
						.antMatchers("/product/**").hasAnyRole("PRODUCT", "ADMIN")
						.antMatchers("/sales/**").hasAnyRole("SALES", "ADMIN")
						.anyRequest()
						.authenticated()
						.and()
					//exceptionHandling 설정
					.exceptionHandling()
						.authenticationEntryPoint(customAuthenticationEntryPoint)
						.accessDeniedHandler(customAccessDeniedHandler)
						.and()
					//로그인 설정
					// MemberDetailsServiceImpl은 UserDetailsService를 구현한 클래스로
					// Spring Security에서 자동으로 이를 사용하여 로그인 확인 로직을 처리함
					.formLogin()
						.loginPage("/standard/login") //custom 로그인 페이지 설정, GET 로그인 요청
						.loginProcessingUrl("/standard/login") //POST 로그인 요청
						.usernameParameter("memberId")
						.passwordParameter("password")
						.successHandler(userLoginSuccessHandler) // 로그인 성공시 실행되는 핸들러
						.failureHandler(userLoginFailureHandler) // 로그인 실패시 실행되는 핸들러
						.and()
					//로그아웃 설정	
					.logout()
						.logoutUrl("/standard/logout")
						.logoutSuccessUrl("/")
						.and()
					.rememberMe()
						.key("secret")
						.rememberMeParameter("remember")
						.tokenValiditySeconds(600)
						.alwaysRemember(false)
						.userDetailsService(memberDetailsServiceImpl)
						.authenticationSuccessHandler(userLoginSuccessHandler);
		
		return httpSecurity.build();
	}

}//end class
