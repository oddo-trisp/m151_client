package gr.di.uoa.m151.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // The pages does not require login
        http.authorizeRequests().antMatchers("/signin", "/logout").permitAll();

        //Only for logged-in users
        http.authorizeRequests().antMatchers("/","/index").authenticated();

        // Config for Login Form
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/signin")//
                .defaultSuccessUrl("/welcome")//
                .failureUrl("/signin?error=true")//
                .usernameParameter("email")//
                .passwordParameter("password")
                // Config for Logout Page
                .and().logout().logoutSuccessUrl("/login?logoutSuccess=true");

        // Config Remember Me.
        /*http.authorizeRequests().and() //
                .rememberMe().tokenRepository(this.persistentTokenRepository()) //
                .tokenValiditySeconds(1 * 24 * 60 * 60); // 24h*/

    }

    /*@Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }*/

}

