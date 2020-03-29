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
    @Override
    public FlowUser getUser() {
        return TokenHolder.getToken().getUser();
    }

}
