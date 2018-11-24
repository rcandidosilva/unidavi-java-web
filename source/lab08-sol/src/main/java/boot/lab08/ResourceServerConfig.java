package boot.lab08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

   @Override
   public void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
           .antMatchers("/users/ping").permitAll()
           .antMatchers("/users/current").authenticated()
           .anyRequest().authenticated();
   }

   @Autowired DefaultTokenServices tokenServices;

   @Override
   public void configure(ResourceServerSecurityConfigurer config) {
       config.tokenServices(tokenServices);
   }   
}