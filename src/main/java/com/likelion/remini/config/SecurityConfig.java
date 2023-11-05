package com.likelion.remini.config;

import com.likelion.remini.jwt.JwtAccessDeniedHandler;
import com.likelion.remini.jwt.JwtAuthenticationEntryPoint;
import com.likelion.remini.jwt.JwtRequestFilter;
import com.likelion.remini.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests().requestMatchers(
                        new AntPathRequestMatcher("/**")
                ).permitAll()
                .and()
                // CORS 설정
                .cors(c -> {
                    CorsConfigurationSource source = request -> {
                        // CORS 허용 패턴
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(
                                List.of("*")
                        );
                        config.setAllowedMethods(
                                List.of("*")
                        );
                        config.setAllowedHeaders(
                                List.of("*")
                        );

                        return config;
                    };
                    c.configurationSource(source);
                });
        return http.build();
    }
}
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig{ //extends WebSecurityConfigurerAdapter, deprecated?
//
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JwtRequestFilter jwtRequestFilter;
//    private final JwtTokenProvider jwtTokenProvider;
//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//
//    private final String[] URL_TO_PERMIT = {
//            "/swagger-ui/index.html",
//            "/swagger-ui/swagger-ui-standalone-preset.js",
//            "/swagger-ui/swagger-initializer.js",
//            "/swagger-ui/swagger-ui-bundle.js",
//            "/swagger-ui/swagger-ui.css",
//            "/swagger-ui/index.css",
//            "/swagger-ui/favicon-32x32.png",
//            "/swagger-ui/favicon-16x16.png",
//            "/api-docs/json/swagger-config",
//            "/api-docs/json",
//            "/swagger-ui/springfox.css",
//            "/swagger-ui/springfox.js",
//            "/v3/api-docs/**",
//            "/api/auth/**",
//            "/kakao/callback/**"
//
//    };
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .accessDeniedHandler(jwtAccessDeniedHandler)
//
//                .and()
//                .authorizeHttpRequests()
//                .antMatchers(URL_TO_PERMIT).permitAll()
//                .anyRequest().authenticated()
//                //.requestMatchers(new AntPathRequestMatcher("/**"))
//                .and()
//                // CORS 설정
//                .cors(c -> {
//                    CorsConfigurationSource source = request -> {
//                        // CORS 허용 패턴
//                        CorsConfiguration config = new CorsConfiguration();
//                        config.setAllowedOrigins(
//                                List.of("*")
//                        );
//                        config.setAllowedMethods(
//                                List.of("*")
//                        );
//                        config.setAllowedHeaders(
//                                List.of("*")
//                        );
//
//                        return config;
//                    };
//                    c.configurationSource(source);
//                });
//
//        http
//                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers(
//                /* swagger v2 */
//                "/v2/api-docs",
//                "/swagger-resources",
//                "/swagger-resources/**",
//                "/configuration/ui",
//                "/configuration/security",
//                "/swagger-ui.html",
//                "/webjars/**",
//                /* swagger v3 */
//                "/v3/api-docs/**",
//                "/swagger-ui/**");
//    }
//}
