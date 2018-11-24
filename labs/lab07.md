# Laboratório 07

## Objetivos
- Implementando e manipulando segurança com o protocolo OAuth2

## Tarefas

### Implemente um serviço de segurança utilizando o protocolo OAuth2
- Utilize os projetos definidos no exercício anterior
- Adicione a dependência `spring-security-oauth2` no seu projeto
```xml
  <dependency>
      <groupId>org.springframework.security.oauth</groupId>
      <artifactId>spring-security-oauth2</artifactId>
  </dependency>
```
- Configure o serviço de autorização OAuth2 utilizando a anotação `@EnableAuthorizationServer`
```java
  @Configuration
  @EnableAuthorizationServer
  public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
      @Autowired
      @Qualifier("authenticationManagerBean")
      AuthenticationManager authenticationManager;

      @Override
      public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
          oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
      }

      @Override
      public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
          // TODO define the client details
      }

      @Override
      public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
          endpoints.authenticationManager(authenticationManager);
      }
  }
```
- Configure/modifique os detalhes de segurança (usuário, perfil, etc) para serem utilizados pela aplicação
```java
  @Configuration
  public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
      @Bean
      @Override
      public AuthenticationManager authenticationManagerBean() throws Exception {
          return super.authenticationManagerBean();
      }

      @Autowired
      public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
          auth.inMemoryAuthentication()
              .withUser("barry").password("t0ps3cr3t").roles("USER").and()
              .withUser("larry").password("t0ps3cr3t").roles("USER", "MANAGER").and()
              .withUser("root").password("t0ps3cr3t").roles("USER", "MANAGER", "ADMIN");
      }      

      @Override
    	public void configure(HttpSecurity http) throws Exception {
          http.csrf().disable()
            	.requestMatchers().antMatchers("/login", "/oauth/authorize").and()
            		.authorizeRequests().anyRequest().authenticated().and()
            		.formLogin().permitAll();
    	}
  }
```
- Configure o serviço de recursos OAuth2 utilizando a anotação `@EnableResourceServer`
```java
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
  }
```
- Implemente também um REST controller para retornar as informações dos usuários
```java
  @RestController
  @RequestMapping("/users")
  public class UserRestController {

      @RequestMapping("/current")
      public Principal current(Principal principal) {
          return principal;
      }

      @RequestMapping("/ping")
      public ResponseEntity<String> ping() {
          return ResponseEntity.ok("ping: " + System.currentTimeMillis());
      }
  }
```
- Adicione a seguinte configuração do arquivo `application.properties`
```
security.basic.enabled=false    
```
- Execute e teste a aplicação

### Teste o fluxo Resource Owner Password via protocolo OAuth2
- Modifique a configuração do `AuthServerConfig` para adicionar suporte ao fluxo de resource owner password
```java
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.inMemory().withClient("password")
           .secret("secret")
           .authorizedGrantTypes("password")
           .scopes("oauth2")
           .autoApprove(true) ;
  }
```
- Execute e teste a aplicação
  - Execute a seguinte requisição HTTP POST
    - `http://password:secret@localhost:8080/oauth/token?grant_type=password&username=barry&password=t0ps3cr3t`
  - Verifique como resultado o OAuth2 `access_token` retornado

### Teste o fluxo Client Credentials via protocolo OAuth2
- Modifique a configuração do `AuthServerConfig` para adicionar suporte ao fluxo de `Client Credentials`
```java
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      //...
        .and().withClient("credentials")
           .secret("secret")
           .authorizedGrantTypes("client_credentials")
           .scopes("oauth2")
           .autoApprove(true) ;
  }
```
- Execute e teste a aplicação
  - Execute a seguinte requisição HTTP POST
    - `http://credentials:secret@localhost:8080/oauth/token?grant_type=client_credentials`
  - Verifique como resultado o OAuth2 `access_token` retornado

### Teste o fluxo Authorization Code via protocolo OAuth2
- Modifique a configuração do `AuthServerConfig` para adicionar suporte ao fluxo de `Authorization Code`
```java
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      //...
      .and().withClient("auth-code")
           .secret("secret")
           .authorizedGrantTypes("authorization_code")
           .scopes("oauth2")
           .autoApprove(true) ;
  }
```
- Execute e teste a aplicação
  - Abra um web browser e accesse a seguinte URL
    - `http://localhost:8080/oauth/authorize?response_type=code&client_id=auth-code&scope=oauth2&redirect_uri=http://callback`
  - Verifique a tela de login sendo retornada e digite uma credencial válida (user: barry / pass: t0ps3cr3t)
  - Observe a URL de callback sendo retornada com um código de autorização OAuth2 `code` retornado
    - Exemplo: `http://callback/?code=WVewpf`
  - Execute a seguinte requisição HTTP POST
    - `http://auth-code:secret@localhost:8080/oauth/token?grant_type=authorization_code&code=WVewpf&redirect_uri=http://callback`
  - Verifique como resultado o OAuth2 `access_token` retornado

### Teste o fluxo Implicit via protocolo OAuth2
- Modifique a configuração do `AuthServerConfig` para adicionar suporte ao fluxo de `Implicit`
```java
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      //...
      .and().withClient("implicit")
           .secret("secret")
           .authorizedGrantTypes("implicit")
           .scopes("oauth2")
           .autoApprove(true) ;
  }
```
- Execute e teste a aplicação
  - Abra um web browser e acesse a seguinte URL
    - `http://localhost:8080/oauth/authorize?response_type=token&client_id=implicit&redirect_uri=http://callback`
  - Verifique como resultado o OAuth2 `access_token` sendo retornado implicitamente na URL de callback
