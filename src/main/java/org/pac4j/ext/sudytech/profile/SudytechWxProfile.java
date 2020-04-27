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
package org.pac4j.ext.sudytech.profile;


import org.pac4j.core.profile.CommonProfile;

/**
 * <p>This class is the user profile for signature with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.core.ext.client.SudytechWxClient}.</p>
 *
 * @author 		： <a href="https://github.com/hiwepy">wandl</a>
 */
public class SudytechWxProfile extends CommonProfile {
     
	private final String uname;
	/**
	 * 第三方平台OpenID（通常指第三方账号体系下某应用中用户的唯一ID）
	 */
	private final String openid;

    public SudytechWxProfile(String uname, String openid) {
        this.uname = uname;
        this.openid = openid;
    }

    @Override
    public String getId() {
    	return this.getUname();
    }
    
	public String getUname() {
		return uname;
	}

	public String getOpenid() {
		return openid;
	}
}
