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
package org.springframework.security.boot.sudytech.userdetails;

import java.util.Collection;

import org.springframework.security.boot.biz.userdetails.SecurityPrincipal;
import org.springframework.security.core.GrantedAuthority;

/**
 *      易班用户基本信息
 * @author ： <a href="https://github.com/hiwepy">wandl</a>
 */
@SuppressWarnings("serial")
public class sudytechPrincipal extends SecurityPrincipal {

	/**
	 * 易班用户id
	 */
	protected String ybUid;
	/**
	 *持有网薪
	 */
	protected String money;
	/**
	 * 经验值
	 */
	protected String exp;
	/**
	 * 用户头像
	 */
	protected String userhead;
	/**
	 * 所在学校id
	 */
	protected String schoolid;
	/**
	 * 所在学校名称
	 */
	protected String schoolname;
	/**
	 * 真实姓名
	 */
	protected String realname;
	/**
	 * 生日
	 */
	protected String birthday;
	/**
	 * 学校首选认证类型编号：如对认证信息的类型敏感，该字段建议使用user/verify_me接口代替
	 */
	protected String studentid;
	/**
	 * 枚举，学生、老师、辅导员、未认证
	 */
	protected String identity;
	
	public sudytechPrincipal(String username, String password, String... roles) {
		super(username, password, roles);
	}

	public sudytechPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public sudytechPrincipal(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public String getYbUid() {
		return ybUid;
	}

	public void setYbUid(String ybUid) {
		this.ybUid = ybUid;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getUserhead() {
		return userhead;
	}

	public void setUserhead(String userhead) {
		this.userhead = userhead;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getSchoolname() {
		return schoolname;
	}

	public void setSchoolname(String schoolname) {
		this.schoolname = schoolname;
	}

}
