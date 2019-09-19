package cn.firstep.theflow.provider;

import cn.firstep.theflow.model.FlowUser;

import java.util.Map;

/**
 * Authenticate Provider.
 *
 * @author Alvin4u
 */
public interface AuthenticateProvider {
    FlowUser doAuthentication(Map<String, Object> payload);
}
