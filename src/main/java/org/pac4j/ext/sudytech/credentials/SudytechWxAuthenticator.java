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

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.ext.sudytech.profile.SudytechWxProfile;
import org.pac4j.ext.sudytech.profile.SudytechWxProfileDefinition;

/**
 * TODO
 * @author ： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SudytechWxAuthenticator extends ProfileDefinitionAware<SudytechWxProfile> implements Authenticator<SudytechWxCredentials> {

	@Override
	protected void internalInit() {
		defaultProfileDefinition(new SudytechWxProfileDefinition(x -> new SudytechWxProfile(null, null)));
	}

	@Override
	public void validate(SudytechWxCredentials credentials, WebContext context) {

	}

}
