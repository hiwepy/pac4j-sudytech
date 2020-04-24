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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.sudytech.auth.basic.ids.login.spi.AppStoreLoginCheckFilter;
import com.sudytech.auth.basic.ids.login.spi.AppStoreLoginNameResolver;
import com.sudytech.auth.basic.ids.login.spi.MidsLoginNameResolver;

public class SudytechWxAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private FilterRegistrationBean<AppStoreLoginCheckFilter> checkFilter = new FilterRegistrationBean<AppStoreLoginCheckFilter>();

	public SudytechWxAuthenticatedProcessingFilter(@Qualifier("sudytechLoginCheckFilter") FilterRegistrationBean<AppStoreLoginCheckFilter> sudytechLoginCheckFilter ) {
		super();
		this.checkFilter = sudytechLoginCheckFilter; 
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		checkFilter.getFilter().doFilter(request, response, chain);
		super.doFilter(request, response, chain);
	}

	protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpRequest) {
		MidsLoginNameResolver resolver = new AppStoreLoginNameResolver(httpRequest);
		String loignName = resolver.findLoginNameByCache();
		return loignName;
	}

	protected Object getPreAuthenticatedCredentials(HttpServletRequest httpRequest) {
		return "N/A";
	}

}