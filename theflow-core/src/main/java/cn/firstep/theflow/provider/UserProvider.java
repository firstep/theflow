package cn.firstep.theflow.provider;

import cn.firstep.theflow.model.FlowUser;
import org.apache.commons.lang3.StringUtils;
import org.flowable.ui.common.tenant.TenantProvider;

/**
 * User Provider.
 *
 * @author Alvin4u
 */
public interface UserProvider extends TenantProvider {

    default String getTenantId() {
        return getUser().getTenantId();
    }

    default boolean hasTenantId() {
        return StringUtils.isNotEmpty(getTenantId());
    }

    default String getId() {
        return getUser().getId();
    }

    default String[] getGroups() {
        return getUser().getGroups();
    }

    FlowUser getUser();

}
