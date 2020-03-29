package cn.firstep.theflow.provider;

import cn.firstep.theflow.common.Permission;
import cn.firstep.theflow.model.FlowUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * A Sample of Authenticate Provider.
 *
 * @author Alvin4u
 */
@Component
public class SampleAuthenticateProvider implements AuthenticateProvider {
    @Override
    public FlowUser doAuthentication(Map<String, Object> payload) {
        String username = (String)payload.get("username");
        String password = (String)payload.get("password");

        //TODO Implement your own authentication.
        String group = (String)payload.get("group");
        String tenant = (String)payload.get("tenant");

        FlowUser user = new FlowUser();
        user.setId(username);
        user.setName(username);
        user.setTenantId(StringUtils.isEmpty(tenant) ? "sample" : tenant);
        user.setGroups(new String[]{group});
        user.setPermissions(Permission.ALL_MANAGER);

        return user;
    }
}
