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
package org.pac4j.ext.sudytech.profile.creator;

import java.util.Optional;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.ext.sudytech.credentials.SudytechWxCredentials;
import org.pac4j.ext.sudytech.profile.SudytechWxProfile;

/**
 * Sudytech Wx profile creator.
 * @author 		： <a href="https://github.com/hiwepy">wandl</a>
 */
public class SudytechWxProfileCreator implements ProfileCreator<SudytechWxCredentials> {
    
    @Override
    public Optional<UserProfile> create(final SudytechWxCredentials credentials, final WebContext context) {
        return Optional.ofNullable(new SudytechWxProfile(credentials.getUname(), credentials.getOpenid()));
    }
	
}