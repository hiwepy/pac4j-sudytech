package org.springframework.security.boot;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.boot.biz.userdetails.UserDetailsServiceAdapter;
import org.springframework.security.boot.sudytech.authentication.sudytechAuthenticationProvider;
import org.springframework.security.boot.sudytech.authentication.sudytechMatchedAuthenticationEntryPoint;
import org.springframework.security.boot.sudytech.authentication.sudytechMatchedAuthenticationFailureHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import com.sudytech.auth.basic.ids.login.spi.AppStoreLoginCheckFilter;

import cn.sudytech.open.Authorize;

@Configuration
@AutoConfigureBefore(SecurityBizAutoConfiguration.class)
@ConditionalOnProperty(prefix = SecuritySudytechProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ SecuritySudytechProperties.class })
public class SecuritySudytechAutoConfiguration {

	/**
	 * 在程序文件中导入并使用AppID与AppSecret来初始化cn.sudytech.open.Authorize
	 */
	@Bean("sudytechLoginCheckFilter")
	public FilterRegistrationBean<AppStoreLoginCheckFilter> sudytechLoginCheckFilter(SecuritySudytechAuthcProperties authcProperties) {
		 FilterRegistrationBean<AppStoreLoginCheckFilter> filterRegistration = new FilterRegistrationBean<AppStoreLoginCheckFilter>();
		 
        filterRegistration.setFilter(new AppStoreLoginCheckFilter());
        filterRegistration.addInitParameter("secretKey", authcProperties.getSecretKey());
        filterRegistration.addInitParameter("loginUrl", authcProperties.getLoginUrl());
        filterRegistration.addInitParameter("dlmSessionKey", authcProperties.getDlmSessionKey());
	    filterRegistration.setEnabled(false); 
		    
		return filterRegistration;
	}
	

	@Bean("sudytechInvalidSessionStrategy")
	public InvalidSessionStrategy sudytechInvalidSessionStrategy(SecuritySudytechAuthcProperties authcProperties) {
		SimpleRedirectInvalidSessionStrategy invalidSessionStrategy = new SimpleRedirectInvalidSessionStrategy(
				authcProperties.getRedirectUrl());
		invalidSessionStrategy.setCreateNewSession(authcProperties.getSessionMgt().isAllowSessionCreation());
		return invalidSessionStrategy;
	}

	@Bean("sudytechExpiredSessionStrategy")
	public SessionInformationExpiredStrategy sudytechExpiredSessionStrategy(SecuritySudytechAuthcProperties authcProperties, RedirectStrategy redirectStrategy) {
		return new SimpleRedirectSessionInformationExpiredStrategy(authcProperties.getRedirectUrl(),
				redirectStrategy);
	}

	@Bean("sudytechSecurityContextLogoutHandler")
	public SecurityContextLogoutHandler sudytechSecurityContextLogoutHandler(SecuritySudytechAuthcProperties authcProperties) {

		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.setClearAuthentication(authcProperties.getLogout().isClearAuthentication());
		logoutHandler.setInvalidateHttpSession(authcProperties.getLogout().isInvalidateHttpSession());

		return logoutHandler;
	}
 

	@Bean
	@ConditionalOnMissingBean
	public sudytechMatchedAuthenticationEntryPoint sudytechMatchedAuthenticationEntryPoint() {
		return new sudytechMatchedAuthenticationEntryPoint();
	}

	@Bean
	@ConditionalOnMissingBean
	public sudytechMatchedAuthenticationFailureHandler sudytechMatchedAuthenticationFailureHandler() {
		return new sudytechMatchedAuthenticationFailureHandler();
	}

	@Bean
	public sudytechAuthenticationProvider sudytechAuthenticationProvider(UserDetailsServiceAdapter userDetailsService,
			PasswordEncoder passwordEncoder) {
		return new sudytechAuthenticationProvider(userDetailsService);
	}

}
