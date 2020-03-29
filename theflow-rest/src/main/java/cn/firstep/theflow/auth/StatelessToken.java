package cn.firstep.theflow.auth;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.SystemCode;
import cn.firstep.theflow.model.FlowUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Stateless Token.
 *
 * @author Alvin4u
 */
public class StatelessToken implements AuthenticationToken {

    private static final long serialVersionUID = -4443711953910712081L;

    public static final String KEY_REFRESH = "refresh";

    public static final String KEY_PERMISSION = "permission";

    public static final String KEY_GROUP = "group";

    public static final String KEY_TENANT = "tenant";

    @Getter
    private String token;

    @Getter
    private boolean refreshable = Boolean.FALSE;

    @Getter
    private FlowUser user;

    @Getter
    private Long refreshTime = -1L;

    public StatelessToken(String token) {
        this.token = token;
        this.user = new FlowUser();
        parseToken();
    }

    public void refresh(String newToken) {
        this.refreshable = true;
        this.token = newToken;
        parseToken();
    }

    private void parseToken() {
        if (StringUtils.isEmpty(this.token)) {
            throw AppException.of(SystemCode.TOKEN_IS_NULL);
        }
        try {
            DecodedJWT decoder = JWT.decode(token);
            this.user.setId(decoder.getSubject());
            this.user.setTenantId(decoder.getClaim(KEY_TENANT).asString());
            this.user.setGroups(decoder.getClaim(KEY_GROUP).asArray(String.class));
            this.user.setPermissions(decoder.getClaim(KEY_PERMISSION).asArray(String.class));
            this.refreshTime = decoder.getClaim(KEY_REFRESH).asLong();
        } catch (Exception e) {
            throw AppException.of(SystemCode.BAD_TOKEN);
        }
        if (StringUtils.isEmpty(this.user.getId()) || StringUtils.isEmpty(this.user.getTenantId())) {
            throw AppException.of(SystemCode.BAD_TOKEN);
        }
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return Boolean.TRUE;
    }
}