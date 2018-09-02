/*
 * Copyright 2016 SimplifyOps, Inc. (http://simplifyops.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
* Policy.java
* 
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: Nov 16, 2010 12:31:26 PM
* 
*/
package com.dtolabs.rundeck.core.authorization.providers;

import com.dtolabs.rundeck.core.authorization.AclRuleSetSource;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Policy is contains a set of {@link RuleSetConstructor} with corresponding usernames and/or groups
 * associated with the each Acl.
 * 
 * The policy is a reference to a phycial policy stored on persistantly.
 *
 * @author noahcampbell
 */
public interface Policy extends AclRuleSetSource {

    /**
     * Return a list of usernames as strings associated with this policy.
     * 
     * @return usernames
     */
    public Set<String> getUsernames();


    /**
     * 
     * Return a list of group objects associated with this policy.
     * 
     * @return groups
     */
    public Set<String> getGroups();


    /**
     * @return the environmental context to test the Policy against an input environment
     *
     */
    public EnvironmentalContext getEnvironment();

    /**
     * @return description of the policy
     */
    String getDescription();
}
