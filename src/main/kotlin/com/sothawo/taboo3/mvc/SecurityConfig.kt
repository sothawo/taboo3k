/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.mvc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * security configuration.

 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Configuration
@EnableWebSecurity
class SecurityConfig
@Autowired constructor(private val userService: Taboo3UserService) : WebSecurityConfigurerAdapter() {

    /**
     * configures the UserService.
     * @param auth
     * *         auth builder to use
     * *
     * @throws Exception
     * *         on error
     */
    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userService)?.passwordEncoder(BCryptPasswordEncoder())
    }

    /**
     * configure http basic auth with a custom login page. Allow the static assets and the login page, restrict all
     * other.
     *
     * @param http
     * *         the security to configure
     * *
     * @throws Exception
     * *         on error
     */
    override fun configure(http: HttpSecurity) {

        http.formLogin()
                .loginPage("/login")
                .and()
                .rememberMe().key("taboo3")
                .and()
                .httpBasic().realmName("taboo3")
                .and()
                .logout().logoutSuccessUrl("/login?logout")
                .and()
                .csrf().ignoringAntMatchers("/bookmark/loadtitle", "/bookmark/upload", "/bookmark/dump")
                .and()
                .authorizeRequests()
                .regexMatchers("/(images|css|js|fonts)/.*").permitAll()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()

    }
}
