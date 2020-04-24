/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.security.boot.sudytech.authentication;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.boot.biz.exception.AuthenticationTokenNotFoundException;
import org.springframework.security.boot.utils.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import com.sudytech.auth.basic.ids.login.spi.AppStoreLoginCheckFilter;

/**
 * 微信门户公众号统一认证授权 (authorization)过滤器
 * 
 * @author ： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SudytechCasAuthorizationProcessingFilter extends AbstractAuthenticationProcessingFilter {
	
	public static final String AUTHORIZATION_PATH = "/login/sudytech";
	public static final String AUTHORIZATION_PARAM = "code";
	private String authorizationParamName = AUTHORIZATION_PARAM;
	
	private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();
	
	public SudytechCasAuthorizationProcessingFilter() {
		super(new AntPathRequestMatcher(AUTHORIZATION_PATH));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request is to process authentication");
		}

		Authentication authResult;

		try {
			
			// 调用苏迪封装的Filter（填充数据）
			checkFilter.doFilter(request, response, chain);
			
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				// return immediately as subclass has indicated that it hasn't completed
				// authentication
				return;
			}
			sessionStrategy.onAuthentication(authResult, request, response);
		}
		catch (InternalAuthenticationServiceException failed) {
			logger.error(
					"An internal error occurred while trying to authenticate the user.",
					failed);
			unsuccessfulAuthentication(request, response, failed);

			return;
		}
		catch (AuthenticationException failed) {
			// Authentication failed
			unsuccessfulAuthentication(request, response, failed);

			return;
		}
		
		successfulAuthentication(request, response, chain, authResult);

		// Authorization success
		chain.doFilter(request, response);
		
	}
	
	@Override
	public void setSessionAuthenticationStrategy(
			SessionAuthenticationStrategy sessionStrategy) {
		super.setSessionAuthenticationStrategy(sessionStrategy);
		this.sessionStrategy = sessionStrategy;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		String code = request.getParameter(getAuthorizationParamName());;

		if (code == null) {
			code = "";
		}

		code = code.trim();
		
		String token = authorize.querytoken(code, "");
		
		if(!StringUtils.hasText(token)) {
			throw new AuthenticationTokenNotFoundException("sudytech token not provided");
		}
		
		AbstractAuthenticationToken authRequest = new sudytechAuthenticationToken(token);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected void setDetails(HttpServletRequest request, AbstractAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}
	
	public void setIgnoreRequestMatcher(List<String> ignorePatterns) {
		if(!CollectionUtils.isEmpty(ignorePatterns)) {
			this.ignoreRequestMatchers = ignorePatterns.stream().map(pattern -> {
				return new AntPathRequestMatcher(pattern);
			}).collect(Collectors.toList());
		}
	}
	
	public void setIgnoreRequestMatchers(RequestMatcher ...ignoreRequestMatchers) {
		this.ignoreRequestMatchers = Arrays.asList(ignoreRequestMatchers);
	}

	public String getAuthorizationParamName() {
		return authorizationParamName;
	}

	public void setAuthorizationParamName(String authorizationParamName) {
		this.authorizationParamName = authorizationParamName;
	}

}