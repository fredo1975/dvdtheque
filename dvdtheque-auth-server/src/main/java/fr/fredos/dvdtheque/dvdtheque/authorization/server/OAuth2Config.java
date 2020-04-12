package fr.fredos.dvdtheque.dvdtheque.authorization.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
	static final String CLIEN_ID = "dvdtheque-client";

	/*
	 * @Autowired private AuthenticationManager authenticationManager;
	 * 
	 * @Override public void configure(AuthorizationServerEndpointsConfigurer
	 * endpoints) throws Exception {
	 * endpoints.authenticationManager(this.authenticationManager).tokenStore(
	 * tokenStore()) .accessTokenConverter(accessTokenConverter()); }
	 * 
	 * @Override public void configure(AuthorizationServerSecurityConfigurer
	 * oauthServer) throws Exception { oauthServer.checkTokenAccess("permitAll()");
	 * }
	 * 
	 * @Bean public JwtAccessTokenConverter accessTokenConverter() { return new
	 * JwtAccessTokenConverter(); }
	 * 
	 * @Override public void configure(ClientDetailsServiceConfigurer clients)
	 * throws Exception { clients.jdbc(dataSource()); }
	 * 
	 * @Bean public JdbcTokenStore tokenStore() { return new
	 * JdbcTokenStore(dataSource()); }
	 * 
	 * @Bean
	 * 
	 * @ConfigurationProperties(prefix = "spring.datasource") public DataSource
	 * dataSource() { return DataSourceBuilder.create().build(); }
	 */

	@Override
	   public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
	      clients
	            .inMemory()
	            .withClient("a")//client username: a
	            .secret(passwordEncoder().encode("a"))//password: a
	            .authorities("ROLE_A","ROLE_B","ROLE_TRUSTED_CLIENT")
	            .scopes("all")
	            .authorizedGrantTypes("client_credentials")
	            .and()
	            .withClient("b")//client username: b
	            .secret(passwordEncoder().encode("b")) //password: b
	            .authorities("ROLE_C")
	            .scopes("all")
	            .authorizedGrantTypes("client_credentials");
	   }

	   @Override
	   public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//	    security.checkTokenAccess("permitAll()"); //if used permitAll() this will make oauth/check_token endpoint to be unsecure
	      security.checkTokenAccess("hasAuthority('ROLE_C')"); //secure the oauth/check_token endpoint
	   }

	   @Bean
	   public TokenStore tokenStore() {
	      return new InMemoryTokenStore();
	   }

	   @Bean
	   public PasswordEncoder passwordEncoder(){
	      return new BCryptPasswordEncoder(4);
	   }

}
