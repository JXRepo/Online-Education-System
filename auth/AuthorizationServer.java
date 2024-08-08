package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import javax.annotation.Resource;

/**
 * @description Authorization Server Configuration
 * @author Mr.M
 * @date 2022/9/26 22:25
 * @version 1.0
 */
 @Configuration
 @EnableAuthorizationServer
 public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

  @Resource(name="authorizationServerTokenServicesCustom")
  private AuthorizationServerTokenServices authorizationServerTokenServices;

 @Autowired
 private AuthenticationManager authenticationManager;

  //Client Details Service
  @Override
  public void configure(ClientDetailsServiceConfigurer clients)
          throws Exception {
        clients.inMemory()// Use in-memory storage
                .withClient("XcWebApp")// client_id
//                .secret("XcWebApp")//Client Secret
                .secret(new BCryptPasswordEncoder().encode("XcWebApp"))//Client Secret
                .resourceIds("xuecheng-plus")//Resource List
                .authorizedGrantTypes("authorization_code", "password","client_credentials","implicit","refresh_token")// Authorized grant types for this client authorization_code,password,refresh_token,implicit,client_credentials
                .scopes("all")// Allowed scopes
                .autoApprove(false)//`false` redirects to the authorization page
                //Client redirection URL for receiving the authorization code
                .redirectUris("http://www.google.com")
   ;
  }


  //Access configuration for the token endpoint
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
   endpoints
           .authenticationManager(authenticationManager)//Authentication Manager
           .tokenServices(authorizationServerTokenServices)//Token Management Service
           .allowedTokenEndpointRequestMethods(HttpMethod.POST);
  }

  //Security Configuration for the Token Endpoint
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security){
   security
           .tokenKeyAccess("permitAll()")                    //The oauth/token_key is public.
           .checkTokenAccess("permitAll()")                  //The `oauth/check_token` endpoint is public.
           .allowFormAuthenticationForClients()				//Form-based authentication (token request)
   ;
  }



 }
