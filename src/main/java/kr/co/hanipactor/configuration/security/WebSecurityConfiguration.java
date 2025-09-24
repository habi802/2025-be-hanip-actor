package kr.co.hanipactor.configuration.security;

import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

/*
@Configuration - bean 등록, Bean 메소드가 있다.
Bean 메소드는 무조건 싱글톤으로 처리된다.
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;

    //Bean 메소드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasicSpec -> httpBasicSpec.disable())
                .formLogin(formLoginSpec -> formLoginSpec.disable())
                .csrf(csrfSpec -> csrfSpec.disable())
                //.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(req -> req
                        // 인증 없이 허용할 API들
                        .requestMatchers("/api/user/login", "/api/user/join", "/api/hanip-manager/actor/login", "/api/store").permitAll()

                        // 사장 계정만 가능한 API
                        .requestMatchers(HttpMethod.PATCH, "/api/store/{storeId}").hasRole(EnumUserRole.OWNER.name())
                        .requestMatchers("/api/store/owner").hasRole(EnumUserRole.OWNER.name())

                        // 관리자 계정만 가능한 API
                        .requestMatchers("/api/hanip-manager/actor/**").hasRole(EnumUserRole.MANAGER.name())

                        // 그 외는 모두 허용
                        .anyRequest().permitAll()
                )
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(tokenAuthenticationEntryPoint))
                .build();
    }



    // ⭐️ CORS 설정
//    CorsConfigurationSource corsConfigurationSource() {
//        return request -> {
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowedHeaders(Collections.singletonList("*"));
//            config.setAllowedMethods(Collections.singletonList("*"));
//            config.setAllowedOriginPatterns(Collections.singletonList("*")); // ⭐️ 허용할 origin
//            config.setAllowCredentials(true);
//            return config;
//        };
//    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

}