package cn.firstep.theflow.auth;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.SystemCode;

/**
 * Token Holder.
 *
 * @author Alvin4u
 */
public class TokenHolder {
    private static ThreadLocal<StatelessToken> TOKEN = new ThreadLocal<>();

    public static StatelessToken getToken() {
        StatelessToken token = TOKEN.get();
        if (token == null) {
            throw AppException.of(SystemCode.TOKEN_IS_NULL);
        }
        return token;
    }

    public static void setToken(StatelessToken token) {
        TOKEN.set(token);
    }

    public static void removeToken() {
        TOKEN.remove();
    }

}
