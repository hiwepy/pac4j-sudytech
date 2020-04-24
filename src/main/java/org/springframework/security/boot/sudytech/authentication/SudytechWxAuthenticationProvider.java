package org.springframework.security.boot.sudytech.authentication;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.boot.biz.userdetails.SecurityPrincipal;
import org.springframework.security.boot.biz.userdetails.UserDetailsServiceAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

public class SudytechWxAuthenticationProvider implements AuthenticationProvider {
	
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserDetailsServiceAdapter userDetailsService;
    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    
    public SudytechWxAuthenticationProvider(final UserDetailsServiceAdapter userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 
     * <p>完成匹配Token的认证，这里返回的对象最终会通过：SecurityContextHolder.getContext().setAuthentication(authResult); 放置在上下文中</p>
     * @author 		：<a href="https://github.com/hiwepy">wandl</a>
     * @param authentication  {@link sudytechAuthenticationToken IdentityCodeAuthenticationToken} 对象
     * @return 认证结果{@link Authentication}对象
     * @throws AuthenticationException  认证失败会抛出异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
    	Assert.notNull(authentication, "No authentication data provided");
    	
    	if (logger.isDebugEnabled()) {
			logger.debug("Processing authentication request : " + authentication);
		}
 
        String token = (String) authentication.getPrincipal();
        
		if (!StringUtils.isBlank(token)) {
			logger.debug("No principal found in request.");
			throw new BadCredentialsException("No principal found in request.");
		}
		
		/**
		  {
			  "status":"success",
			  "info":{
			    "yb_userid":"易班用户id",
			    "yb_username":"用户名",
			    "yb_usernick":"用户昵称",
			    "yb_sex":"性别",
			    "yb_money":"持有网薪",
			    "yb_exp":"经验值",
			    "yb_userhead":"用户头像",
			    "yb_schoolid":"所在学校id",
			    "yb_schoolname":"所在学校名称",
			    "yb_realname":"真实姓名",
			    "yb_birthday":"生日",
			    "yb_studentid":"学校首选认证类型编号",//如对认证信息的类型敏感，该字段建议使用user/verify_me接口代替
			    "yb_identity":"用户身份"//枚举，学生、老师、辅导员、未认证
			  }
			}
		 */
        
        UserDetails ud = getUserDetailsService().loadUserDetails(authentication);
        
        // User Status Check
        getUserDetailsChecker().check(ud);
        
        SudytechWxAuthenticationToken authenticationToken = null;
        if(SecurityPrincipal.class.isAssignableFrom(ud.getClass())) {
        	authenticationToken = new SudytechWxAuthenticationToken(ud, ud.getPassword(), ud.getAuthorities());        	
        } else {
        	authenticationToken = new SudytechWxAuthenticationToken(ud.getUsername(), ud.getPassword(), ud.getAuthorities());
		}
        authenticationToken.setDetails(authentication.getDetails());
        
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (SudytechWxAuthenticationToken.class.isAssignableFrom(authentication));
    }

	public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
		this.userDetailsChecker = userDetailsChecker;
	}

	public UserDetailsChecker getUserDetailsChecker() {
		return userDetailsChecker;
	}

	public UserDetailsServiceAdapter getUserDetailsService() {
		return userDetailsService;
	}
    
}
