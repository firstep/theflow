package cn.firstep.theflow.auth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;

/**
 * Stateless Realm.
 * 此类所有Autowired需设置延迟加载，不然这些注入类中的所有aop都会失效
 *
 * @author Alvin4u
 */
public class StatelessRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private TokenHelper tokenHelper;

    @Override
    public String getName() {
        return "StatelessRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof StatelessToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object primaryPrincipal = principals.getPrimaryPrincipal();
        if (primaryPrincipal instanceof StatelessToken) {
            StatelessToken token = (StatelessToken) primaryPrincipal;
            SimpleAuthorizationInfo authz = new SimpleAuthorizationInfo();
            authz.addStringPermissions(Arrays.asList(token.getUser().getPermissions()));
            return authz;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token instanceof StatelessToken) {
            StatelessToken tk = (StatelessToken) token;

            if (!tokenHelper.verify(tk)) {
                return null;
            }
            return new SimpleAuthenticationInfo(tk, tk.getCredentials(), getName());
        }
        return null;
    }
}
