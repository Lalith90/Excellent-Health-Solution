package lk.solution.health.excellent.general.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
/*
 * @EnableGlobalMethodSecurity(prePostEnabled = true)
 * using this we can manage method access
 *   @PreAuthorize("hasAnyRole('ADMIN')") ..........like
 * */


public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final DataSource dataSource;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public SecurityConfig(DataSource dataSource, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.dataSource = dataSource;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.
                jdbcAuthentication()
                .usersByUsernameQuery("select username, password, enabled from user where username=?")
                .authoritiesByUsernameQuery("select username, name from role join user on role.id = user.role_id where username=?")
                .dataSource(dataSource)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().and()
//                .authorizeRequests().antMatchers("/").permitAll();


        /* for developing easy to give permission all link*/

        http.
                authorizeRequests()
                //Always users can access without login
                .antMatchers(
                        "/index",
                        "/favicon.ico",
                        "/img/**",
                        "/css/**",
                        "/js/**",
                        "/font/**").permitAll()
                .antMatchers("/login", "/select/**").permitAll()

                //Need to login for access those are
                .antMatchers("/employee/**").hasRole("MANAGER")
                .antMatchers("/user/**").hasRole("MANAGER")
                .antMatchers("/invoiceProcess/add").hasRole("CHASHIER")
                .antMatchers("/cashier/**").hasRole("CHASHIER")
                .antMatchers("/doctor/**").hasAnyRole("CHASHIER", "MANAGER")
                .antMatchers("/patient/**").hasAnyRole("MANAGER", "CHASHIER")
                .antMatchers("/lab/authorize/**").hasRole("MLT1")
                .antMatchers("/lab/**").hasAnyRole("MLT1, MLT2")
                .antMatchers("/phlabotoProcess/**").hasRole("PHLEBOTOMISE")
                .anyRequest()
                .authenticated()
                .and()
                // Login form
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/mainwindow")
                //Username and password for validation
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                //Logout controlling
                .logout()
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/index")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling()
                .and()
                //Session Management
                .sessionManagement()
                .invalidSessionUrl("/login")
                .sessionFixation()
                .changeSessionId()
                .maximumSessions(10)
                .expiredUrl("/login")
                .and()
                //Cross site disable
                .and()
                .csrf()
                .disable();

    }
}
