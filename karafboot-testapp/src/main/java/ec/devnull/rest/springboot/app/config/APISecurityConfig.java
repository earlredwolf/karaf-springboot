/*
 * mcm-rest-client
 *
 * Copyright (c) 2018.  FISA Group.
 *
 * This software is the confidential and proprietary information FISA GROUP
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with FISA Group.
 */

package ec.devnull.rest.springboot.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@Order(1)
public class APISecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${omnia.http.auth-token-header-name}")
    private String principalRequestHeader;

    @Autowired
    private APIAuthenticationManager apiAuthenticationManager;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);
        filter.setAuthenticationManager(apiAuthenticationManager);

        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(apiAuthenticationManager);

        httpSecurity.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(filter)
                .addFilter(jwtAuthorizationFilter)
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


}
