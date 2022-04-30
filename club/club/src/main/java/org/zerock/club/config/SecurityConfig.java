package org.zerock.club.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zerock.club.security.filter.ApiCheckFilter;
import org.zerock.club.security.filter.ApiLoginFilter;
import org.zerock.club.security.handler.ApiLoginFailHandler;
import org.zerock.club.security.handler.ClubLoginSuccessHandler;
import org.zerock.club.security.service.ClubUserDetailsService;
import org.zerock.club.security.util.JWTUtil;

import javax.servlet.Filter;

@Configuration
@Log4j2
// securedEnabled는 @Secure 어노테이션이 사용 가능한지, prePostEnableds는 @PreAuthorize를 사용 가능한지 결정
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClubUserDetailsService userDetailsService; // 주입

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        http.authorizeHttpRequests()
//                .antMatchers("/sample/all").permitAll()
//                .antMatchers("/sample/member").hasRole("USER");
        
        http.formLogin(); // 인가(authentication) or 인증(authorization)에 문제 발생 시 로그인 화면 보여줌
        http.csrf().disable(); // CSRF 토큰 발행하지 않도록 설정
        // 로그아웃 처리, CSRF 토큰을 사용할 때는 반드시 POST 방식으로만 로그아웃을 처리함, CSRF 토큰 사용 안하면 GET 방식('/logout')으로도 로그아웃 처리 가능
        http.logout();
        http.oauth2Login().successHandler(successHandler());
        // 7일 동안 쿠키를 유지, 한번 로그인한 사용자가 브라우저를 닫은 후에 다시 서비스에 접속해도 별도의 로그인 절차없이 바로 로그인 처리가 진행됨
        http.rememberMe().tokenValiditySeconds(60*60*7).userDetailsService(userDetailsService); // 초 단위로 설정

        // apiCheckFilter를 UsernamePasswordAuthenticationFilter 이전에 동작하도록 지정
        http.addFilterBefore(apiCheckFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiLoginFilter(), UsernamePasswordAuthenticationFilter.class);
   }

    @Bean
    public ApiLoginFilter apiLoginFilter() throws Exception{ // '/api/login'라는 경로로 접근할 때 동작

        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/api/login", jwtUtil());
        apiLoginFilter.setAuthenticationManager(authenticationManager());

        apiLoginFilter.setAuthenticationFailureHandler(new ApiLoginFailHandler());

        return apiLoginFilter;
    }

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }

    @Bean
    public ApiCheckFilter apiCheckFilter(){
        return new ApiCheckFilter("/notes/**/*",jwtUtil());
    }

    @Bean
    public ClubLoginSuccessHandler successHandler(){ // ClubLoginSuccessHandler를 생성하는 메서드
        return new ClubLoginSuccessHandler(passwordEncoder());
    }

}
