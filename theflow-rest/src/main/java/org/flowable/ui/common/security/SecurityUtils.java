/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.ui.common.security;

import cn.firstep.theflow.auth.TokenHolder;
import cn.firstep.theflow.model.FlowUser;
import cn.firstep.theflow.provider.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.flowable.idm.api.User;
import org.flowable.ui.common.model.RemoteUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for Spring Security.
 * <p>
 * Only overwite method[getCurrentUserObject] to achieve your own business.
 *
 * @author Alvin4u
 */
public class SecurityUtils {

    private static User assumeUser;

    private static List<String> DEFAULT_ADMIN_PRIVILEGES = Arrays.asList(
            DefaultPrivileges.ACCESS_MODELER,
            DefaultPrivileges.ACCESS_IDM,
            DefaultPrivileges.ACCESS_ADMIN,
            DefaultPrivileges.ACCESS_TASK,
            DefaultPrivileges.ACCESS_REST_API);

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentUserId() {
        User user = getCurrentUserObject();
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    /**
     * @return the {@link User} object associated with the current logged in user.
     */
    public static User getCurrentUserObject() {
        if (assumeUser != null) {
            return assumeUser;
        }

        FlowUser flowUser = TokenHolder.getToken().getUser();

        RemoteUser user = new RemoteUser();
        user.setId(flowUser.getId());
        user.setDisplayName(flowUser.getName());
        user.setFirstName(flowUser.getId());
        user.setLastName(flowUser.getId());
        user.setEmail(StringUtils.EMPTY);
        user.setPassword(flowUser.getId());

        if (UserHolder.isManager(flowUser.getRoles())) {
            user.setPrivileges(DEFAULT_ADMIN_PRIVILEGES);
        }
        return user;
    }

    public static FlowableAppUser getCurrentFlowableAppUser() {
        FlowableAppUser user = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null) {
            Object principal = securityContext.getAuthentication().getPrincipal();
            if (principal instanceof FlowableAppUser) {
                user = (FlowableAppUser) principal;
            }
        }
        return user;
    }

    public static boolean currentUserHasCapability(String capability) {
        FlowableAppUser user = getCurrentFlowableAppUser();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            if (capability.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static void assumeUser(User user) {
        assumeUser = user;
    }

    public static void clearAssumeUser() {
        assumeUser = null;
    }

}
