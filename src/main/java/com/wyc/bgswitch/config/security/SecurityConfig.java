package com.wyc.bgswitch.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Value("${prefix.api}")
    private String apiPrefix;
    @Value("${prefix.ws.endpoint.websocket}")
    private String websocketEndpoint;
    @Value("${prefix.ws.endpoint.sockjs}")
    private String sockjsEndpoint;
    @Value("${prefix.resources.bgs.web}")
    private String bgsWebPath;
    @Value("${prefix.resources.bgs.static}")
    private String bgsResourcesPath;
    @Value("${prefix.resources.static}")
    private String staticResourcesPath;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeHttpRequests((authorize) -> authorize
                    .requestMatchers("/").permitAll()
                    .requestMatchers(apiPrefix+"/public/**").permitAll()
                    .requestMatchers(apiPrefix+"/csrf").permitAll()
                    .requestMatchers(HttpMethod.POST, apiPrefix+"/login").permitAll()
                    .requestMatchers(apiPrefix+"/**").authenticated()
                    .requestMatchers(websocketEndpoint).permitAll()
                    .requestMatchers(sockjsEndpoint).permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers(HttpMethod.GET,staticResourcesPath+"/**").permitAll() // 公共静态文件目录的映射地址
                    .requestMatchers(HttpMethod.GET,bgsWebPath+"/**").permitAll() // 用户访问bgs的地址
                    .requestMatchers(HttpMethod.GET,bgsResourcesPath+"/**").permitAll() // bgs文件目录的映射地址
                    .requestMatchers(HttpMethod.GET,"/**").permitAll() // 由于metro的subfolder功能尚未上线，目前只能这样补救
                    .anyRequest().authenticated()
//                        .anyRequest().permitAll()
                )
                .csrf((csrf) -> csrf.ignoringRequestMatchers(
                        apiPrefix+"/login",
                        apiPrefix+"/token",
                        apiPrefix+"/refresh"
                )).httpBasic(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())
//                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
//                .oauth2Login(Customizer.withDefaults())
//                .exceptionHandling((exceptions) -> exceptions
////                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
//                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
//                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
//                )
        ;
        // @formatter:on
        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder().username("wyc").password("password").roles("USER", "WYC").build(),
                User.withDefaultPasswordEncoder().username("lzz").password("password").roles("USER", "LZZ").build()
        );
    }

    @Bean(name = "myAuthenticationManager")
    public AuthenticationManager myAuthenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:[*]", "http://192.168.1.*:[*]"));
        configuration.setAllowedOrigins(List.of("https://3229nr8294.yicp.fun/"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // 默认不允许任何header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
