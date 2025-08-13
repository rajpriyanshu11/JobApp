package com.example.JobApp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

        return provider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for Postman
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/job")
//                        .permitAll()
                        .anyRequest().authenticated()
                )
//                .formLogin(Customizer.withDefaults())
//                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // H2 console
                .httpBasic(Customizer.withDefaults()); // ✅ enable basic authentication

        return http.build();

    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails user = User.withUsername("Raj").password("{noop}1234").roles("USER").build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}
