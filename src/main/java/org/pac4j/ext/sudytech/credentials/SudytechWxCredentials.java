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
package org.pac4j.ext.sudytech.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">wandl</a>
 */
@SuppressWarnings("serial")
public class SudytechWxCredentials extends Credentials {

	private final String uname;
	/**
	 * 第三方平台OpenID（通常指第三方账号体系下某应用中用户的唯一ID）
	 */
	private final String openid;

    public SudytechWxCredentials(String uname, String openid) {
        this.uname = uname;
        this.openid = openid;
    }

	public String getUname() {
		return uname;
	}

	public String getOpenid() {
		return openid;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SudytechWxCredentials that = (SudytechWxCredentials) o;

        return !(uname != null ? !uname.equals(that.uname) : (openid != null ? !openid.equals(that.openid) : that.openid != null));
    }
	
    @Override
    public int hashCode() {
        return uname != null ? uname.hashCode() : (openid != null ? openid.hashCode() : 0);
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "uname", this.uname, "openid", this.openid);
    }
    
}
