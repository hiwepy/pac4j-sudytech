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
package org.pac4j.ext.sudytech.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.ext.sudytech.credentials.SudytechWxAuthenticator;
import org.pac4j.ext.sudytech.credentials.SudytechWxCredentials;
import org.pac4j.ext.sudytech.credentials.extractor.SudytechWxParameterExtractor;
import org.pac4j.ext.sudytech.profile.creator.SudytechWxProfileCreator;

import com.sudytech.auth.basic.BasicEnv;
import com.sudytech.auth.basic.ids.login.spi.AppStoreLoginNameResolver;

public class SudytechWxClient extends DirectClient<SudytechWxCredentials> {

	private boolean supportGetRequest = true;

	private boolean supportPostRequest;

	private String charset = StandardCharsets.UTF_8.name();

	/**
	 * The location of the client login URL, i.e. https://localhost:8080/myapp/login
	 */
	private String loginUrl;

	private String secretKey;
	private String dlmSessionKey;
	private String publicPaths;
	private Map<String, String> publicPathMap;

	public SudytechWxClient() {
	}

	@Override
	protected void clientInit() {
		defaultAuthenticator(new SudytechWxAuthenticator());
		defaultProfileCreator(new SudytechWxProfileCreator());
		defaultCredentialsExtractor(new SudytechWxParameterExtractor( this.isSupportGetRequest(), this.isSupportPostRequest(), this.getCharset()));
		// ensures components have been properly initialized
		CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
		CommonHelper.assertNotNull("authenticator", getAuthenticator());
		CommonHelper.assertNotNull("profileCreator", getProfileCreator());
		try {
			loadPublicPathMap(this.getPublicPaths());
		} catch (Exception ex) {
		}

	}

	private void loadPublicPathMap(String publicPath) {
		if ((publicPath == null) || (publicPath.length() == 0)) {
			return;
		}
		String[] publicPaths = publicPath.split("\\|");
		for (String path : publicPaths) {
			if (StringUtils.isNotEmpty(path)) {
				this.publicPathMap.put(path, "1");
			}
		}
	}

	@Override
	protected Optional<SudytechWxCredentials> retrieveCredentials(WebContext context) {
		init();
		try {
			
			JEEContext jeeContext = (JEEContext) context;
			
			HttpServletRequest request = jeeContext.getNativeRequest();
			AppStoreLoginNameResolver userManager = new AppStoreLoginNameResolver(request);
			String loginName = null;
			try {
				loginName = userManager.findLoginNameBySecretKey(this.secretKey);
			} catch (Exception ex) {
				System.out.println("-----解析用户时异常-----");
				ex.printStackTrace();
			}
			if (StringUtils.isNotEmpty(loginName)) {
				HttpSession httpSess = request.getSession();
				if (StringUtils.isNotEmpty(this.dlmSessionKey)) {
					httpSess.setAttribute(this.dlmSessionKey, loginName);
				}
				httpSess.setAttribute("mids_loginName", loginName);
				final Optional<SudytechWxCredentials> credentials = getCredentialsExtractor().extract(context);
				if (!credentials.isPresent()) {
					// redirect to the login page
					logger.debug("redirectionUrl: {}", getLoginUrl());
					throw RedirectionActionHelper.buildRedirectUrlAction(context, getLoginUrl());
				}
				return credentials;
			} else {
				if (isWeChatLoginUrl()) {
					if (isWeChat(request)) {
						// redirect to the login page
						String redirectUrl = buildRedirectUrl(request);
						logger.debug("redirectionUrl: {}", redirectUrl);
						throw RedirectionActionHelper.buildRedirectUrlAction(context, redirectUrl);
					} else {
						final Optional<SudytechWxCredentials> credentials = getCredentialsExtractor().extract(context);
						if (!credentials.isPresent()) {
							// redirect to the login page
							logger.debug("redirectionUrl: {}", getLoginUrl());
							throw RedirectionActionHelper.buildRedirectUrlAction(context, getLoginUrl());
						}
						return credentials;
					}
				} else if (isWeChat(request)) {
					final Optional<SudytechWxCredentials> credentials = getCredentialsExtractor().extract(context);
					if (!credentials.isPresent()) {
						// redirect to the login page
						logger.debug("redirectionUrl: {}", getLoginUrl());
						throw RedirectionActionHelper.buildRedirectUrlAction(context, getLoginUrl());
					}
					return credentials;
				} else {
					// redirect to the login page
					String redirectUrl = buildRedirectUrl(request);
					logger.debug("redirectionUrl: {}", redirectUrl);
					throw RedirectionActionHelper.buildRedirectUrlAction(context, redirectUrl);
				}
			}
		} catch (CredentialsException e) {
			logger.error("Failed to retrieve or validate Token credentials", e);
			return Optional.empty();
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to retrieve or validate Token credentials", e);
			return Optional.empty();
		}
	}
	

	private boolean isWeChatLoginUrl() {
		return this.loginUrl.contains("?appId=");
	}

	private boolean isWeChat(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		return ((userAgent != null) && (userAgent.contains(" MicroMessenger/")));
	}

	private String buildRedirectUrl(HttpServletRequest request) throws UnsupportedEncodingException {
		String loginUrl = this.loginUrl;
		String serviceUrl = BasicEnv.findServiceUrl(request);
		return loginUrl.replace("[ReturnURL]", URLEncoder.encode(serviceUrl, "UTF-8"));
	}

	public boolean isSupportGetRequest() {
		return supportGetRequest;
	}

	public void setSupportGetRequest(boolean supportGetRequest) {
		this.supportGetRequest = supportGetRequest;
	}

	public boolean isSupportPostRequest() {
		return supportPostRequest;
	}

	public void setSupportPostRequest(boolean supportPostRequest) {
		this.supportPostRequest = supportPostRequest;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getDlmSessionKey() {
		return dlmSessionKey;
	}

	public void setDlmSessionKey(String dlmSessionKey) {
		this.dlmSessionKey = dlmSessionKey;
	}

	public String getPublicPaths() {
		return publicPaths;
	}

	public void setPublicPaths(String publicPaths) {
		this.publicPaths = publicPaths;
	}

	public Map<String, String> getPublicPathMap() {
		return publicPathMap;
	}

	public void setPublicPathMap(Map<String, String> publicPathMap) {
		this.publicPathMap = publicPathMap;
	}

}
