package cn.firstep.theflow.auth;

import cn.firstep.theflow.cache.Cache;
import cn.firstep.theflow.model.FlowUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * TOKEN 签发、验证及刷新
 * 验证规则：签发时，把所签发token的时间放置于缓存中，并设置过期时间(通常token过期时间比缓存中刷新标志过期时间短)，验证时，需要把token中的签发时间和缓存中的比较，相互一致才算验证成功
 * 刷新规则：如果token已过期，同样比较token签发时间和缓存中的是否一致，如果不一致，说明已经过期或被新的登陆签发操作覆盖，此时刷新失败
 *
 * @author Alvin4u
 */
@Component
public class TokenHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(TokenHelper.class);

    private static String CACHE_PREFIX = "TOKEN#";

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.accessExpire}")
    private Long accessExpire;

    @Value("${app.token.refreshExpire}")
    private Long refreshExpire;

    //是否提出其他用户，默认为true，建议正式环境开启，开发环境可以关掉
    @Value("#{ @environment['app.token.kickUser'] ?: 'true' }")
    private boolean kickUser;

    @Autowired
    private Cache<String, String> cache;

    public void logout(String token) {
        StatelessToken tk = new StatelessToken(token);
        logout(tk);
    }

    public void logout(StatelessToken token) {
        if (verify(token)) {
            cache.remove(CACHE_PREFIX + token.getUser().getId());
        }
    }

    public String sign(FlowUser user) {
        if (user == null || StringUtils.isEmpty(user.getTenantId()) || StringUtils.isEmpty(user.getId())) {
            return null;
        }
        long now = System.currentTimeMillis();
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withSubject(user.getId())
                    .withClaim(StatelessToken.KEY_TENANT, user.getTenantId())
                    .withArrayClaim(StatelessToken.KEY_ROLE, user.getRoles())
                    .withArrayClaim(StatelessToken.KEY_PERMISSION, user.getPermissions())
                    .withClaim(StatelessToken.KEY_REFRESH, now)
                    .withExpiresAt(new Date(now + accessExpire))
                    .sign(algorithm);

            cache.put(CACHE_PREFIX + user.getId(), now + "", refreshExpire);
            return token;
        } catch (Exception e) {
            LOGGER.error("Sign token error.", e);
            return null;
        }
    }

    public boolean verify(StatelessToken tk) {
        FlowUser user = tk.getUser();
        String refreshTime = cache.get(CACHE_PREFIX + user.getId());
        //当缓存中没有refresh，表示过期（自动过期），或者账号登出了（强制过期）,当前token失效
        //当缓存中refresh变了，表示有新的登陆覆盖了，当前token失效
        if (kickUser && (refreshTime == null || !refreshTime.equals(tk.getRefreshTime().toString()))) {
            return false;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(user.getId())
                    .withClaim(StatelessToken.KEY_TENANT, user.getTenantId())
                    .withArrayClaim(StatelessToken.KEY_ROLE, user.getRoles())
                    .withArrayClaim(StatelessToken.KEY_PERMISSION, user.getPermissions())
                    .withClaim(StatelessToken.KEY_REFRESH, tk.getRefreshTime())
                    .build();
            verifier.verify(tk.getToken());
            return true;
        } catch (TokenExpiredException e) {
            //仅在当前token过期但refresh没过期且refresh没被覆盖，重新刷新token，并且更新缓存
            if (!kickUser || (refreshTime != null && refreshTime.equals(tk.getRefreshTime().toString()))) {
                String newToken = sign(user);
                tk.refresh(newToken);
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("Verify token error.", e);
            return false;
        }
    }

}
