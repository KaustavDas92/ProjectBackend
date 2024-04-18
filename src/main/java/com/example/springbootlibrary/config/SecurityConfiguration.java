package com.example.springbootlibrary.config;

import com.okta.spring.boot.oauth.Okta;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {

        //disable cross site request forgery
        security.csrf().disable();

        //Protects endpoints at api/<type>/secure
//        security.authorizeHttpRequests(configurer ->
//                configurer
//                        .requestMatchers("/api/books/secure/**")
//                        .authenticated())
//                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
//                        httpSecurityOAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults()));

        security

                .authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers(
                                "/api/books/secure/**",
                                "/api/reviews/secure/**",
                                "/api/messages/secure/**")
                        .authenticated()
                        .anyRequest().
                        permitAll())
                .oauth2ResourceServer().jwt();

        //add cors filters
//        security.cors(customizer -> customizer.disable());
            security.cors();

        //add content negotiation strategy
        security.setSharedObject(ContentNegotiationStrategy.class,new HeaderContentNegotiationStrategy());


        //force a non empty response body for 401's to make the response friendly
//        Okta.configureResourceServer401ResponseBody(security);


        return security.build();
    }
}
