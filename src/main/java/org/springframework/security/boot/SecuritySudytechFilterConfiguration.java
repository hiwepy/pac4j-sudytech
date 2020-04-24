package org.springframework.security.boot;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.boot.biz.authentication.AuthenticatingFailureCounter;
import org.springframework.security.boot.biz.authentication.AuthenticationListener;
import org.springframework.security.boot.biz.authentication.PostRequestAuthenticationFailureHandler;
import org.springframework.security.boot.biz.authentication.PostRequestAuthenticationSuccessHandler;
import org.springframework.security.boot.biz.authentication.captcha.CaptchaResolver;
import org.springframework.security.boot.biz.authentication.nested.MatchedAuthenticationEntryPoint;
import org.springframework.security.boot.biz.authentication.nested.MatchedAuthenticationFailureHandler;
import org.springframework.security.boot.biz.authentication.nested.MatchedAuthenticationSuccessHandler;
import org.springframework.security.boot.biz.userdetails.UserDetailsServiceAdapter;
import org.springframework.security.boot.sudytech.authentication.sudytechAuthenticationProvider;
import org.springframework.security.boot.sudytech.authentication.sudytechAuthorizationProcessingFilter;
import org.springframework.security.boot.sudytech.authentication.sudytechPreAuthenticatedProcessingFilter;
import org.springframework.security.boot.utils.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudytech.auth.basic.ids.login.spi.AppStoreLoginCheckFilter;

@Configuration
@AutoConfigureBefore(name = { 
	"org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration"
})
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = SecuritySudytechProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ SecuritySudytechProperties.class, SecurityBizProperties.class, ServerProperties.class })
public class SecuritySudytechFilterConfiguration {
	
	@Configuration
	@EnableConfigurationProperties({ SecuritySudytechProperties.class, SecuritySudytechAuthcProperties.class, SecurityBizProperties.class })
	static class sudytechWebSecurityConfigurerAdapter extends SecurityBizConfigurerAdapter {
		
		private final SecuritySudytechAuthcProperties authcProperties;
		
		private final AuthenticatingFailureCounter authenticatingFailureCounter;
	    private final AuthenticationEntryPoint authenticationEntryPoint;
	    private final AuthenticationSuccessHandler authenticationSuccessHandler;
	    private final AuthenticationFailureHandler authenticationFailureHandler;
	    private final CaptchaResolver captchaResolver;
	    private final InvalidSessionStrategy invalidSessionStrategy;
	    private final LogoutHandler logoutHandler;
	    private final ObjectMapper objectMapper;
    	private final RequestCache requestCache;
    	private final RememberMeServices rememberMeServices;
    	private final SessionRegistry sessionRegistry;
		private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
		private final SessionInformationExpiredStrategy sessionInformationExpiredStrategy;
		private final UserDetailsServiceAdapter authcUserDetailsService;
		
		public sudytechWebSecurityConfigurerAdapter(
				
				SecurityBizProperties bizProperties,
				SecuritySudytechAuthcProperties authcProperties,
				
				ObjectProvider<sudytechAuthenticationProvider> authenticationProvider,
   				ObjectProvider<AuthenticationManager> authenticationManagerProvider,
   				ObjectProvider<AuthenticationListener> authenticationListenerProvider,
   				ObjectProvider<MatchedAuthenticationEntryPoint> authenticationEntryPointProvider,
   				ObjectProvider<MatchedAuthenticationSuccessHandler> authenticationSuccessHandlerProvider,
   				ObjectProvider<MatchedAuthenticationFailureHandler> authenticationFailureHandlerProvider,
   				ObjectProvider<CaptchaResolver> captchaResolverProvider,
   				ObjectProvider<LogoutHandler> logoutHandlerProvider,
   				ObjectProvider<ObjectMapper> objectMapperProvider, 
				ObjectProvider<UserDetailsServiceAdapter> authcUserDetailsService) {
			
			super(bizProperties, authcProperties, authenticationProvider.stream().collect(Collectors.toList()),
					authenticationManagerProvider.getIfAvailable());
			
			this.authcProperties = authcProperties;
			
			this.authenticatingFailureCounter = super.authenticatingFailureCounter();
   			List<AuthenticationListener> authenticationListeners = authenticationListenerProvider.stream().collect(Collectors.toList());
   			this.authenticationEntryPoint = super.authenticationEntryPoint(authenticationEntryPointProvider.stream().collect(Collectors.toList()));
   			this.authenticationSuccessHandler = super.authenticationSuccessHandler(authenticationListeners, authenticationSuccessHandlerProvider.stream().collect(Collectors.toList()));
   			this.authenticationFailureHandler = super.authenticationFailureHandler(authenticationListeners, authenticationFailureHandlerProvider.stream().collect(Collectors.toList()));
   			this.authcUserDetailsService = authcUserDetailsService.getIfAvailable();
   			this.captchaResolver = captchaResolverProvider.getIfAvailable();
   			this.invalidSessionStrategy = super.invalidSessionStrategy();
   			this.logoutHandler = super.logoutHandler(logoutHandlerProvider.stream().collect(Collectors.toList()));
   			this.objectMapper = objectMapperProvider.getIfAvailable();
   			this.requestCache = super.requestCache();
   			this.rememberMeServices = super.rememberMeServices();
   			this.sessionRegistry = super.sessionRegistry();
   			this.sessionAuthenticationStrategy = super.sessionAuthenticationStrategy();
   			this.sessionInformationExpiredStrategy = super.sessionInformationExpiredStrategy();
		}
		 
		
		@Bean
		public AppStoreLoginCheckFilter authenticatedProcessingFilter() throws Exception {
	    	
			AppStoreLoginCheckFilter authcFilter = new AppStoreLoginCheckFilter();
			
	        return authcFilter;
	    }
	
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {

   	    	//super.configure(http, authcProperties.getCros());
   	    	//super.configure(http, authcProperties.getCsrf());
   	    	//super.configure(http, authcProperties.getHeaders());
	    	super.configure(http);
			
		}

	}
	
}
