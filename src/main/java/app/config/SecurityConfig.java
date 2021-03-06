package app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;

/**
 * Created by Gabriel on 20.03.2017.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource dataSource;

    @Autowired
    SecurityConfig(@Qualifier("dsUsers") DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        addCharacterEncodingFilter(http);
        http
                .authorizeRequests()
                    .antMatchers("/cart").hasAuthority("USER")
                    .anyRequest().permitAll()
                .and()
                .formLogin()
                    .loginPage("/users/login")
                    .permitAll()
                .and()
                .logout()
                    .logoutSuccessUrl("/users/login?logout")
                .and()
                .rememberMe()
                    .tokenValiditySeconds(3600)
                    .key("rememberKey")
                .and()
                .httpBasic()
                    .realmName("MyWebApp")
                .and()
                .requiresChannel()
                    .anyRequest().requiresSecure();
    }

    // sets proper filters order to avoid problems with encoding,
    // CharacterEncodingFilter must be added before CsrfFilter
    private void addCharacterEncodingFilter(HttpSecurity http) {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
