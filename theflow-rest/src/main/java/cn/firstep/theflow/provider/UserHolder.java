package cn.firstep.theflow.provider;

import cn.firstep.theflow.auth.TokenHolder;
import cn.firstep.theflow.model.FlowUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * User Holder.
 *
 * @author Alvin4u
 */
@Primary
@Component
public class UserHolder implements UserProvider {
    public static final String ADMIN_ROLE = "admin";

    @Override
    public FlowUser getUser() {
        return TokenHolder.getToken().getUser();
    }

    @Override
    public boolean isManager() {
        return UserHolder.isManager(getRoles());
    }

    public static boolean isManager(String[] roles) {
        return roles != null && Arrays.stream(roles).anyMatch(role -> ADMIN_ROLE.equalsIgnoreCase(role));
    }

}
