package cn.firstep.theflow.api;

import cn.firstep.theflow.auth.TokenHelper;
import cn.firstep.theflow.auth.TokenHolder;
import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.ResponseEntitys;
import cn.firstep.theflow.common.code.SystemCode;
import cn.firstep.theflow.model.FlowUser;
import cn.firstep.theflow.provider.AuthenticateProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Authentication API.
 *
 * @author Alvin4u
 */
@RestController
public class AuthApi {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthApi.class);

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private AuthenticateProvider authProvider;

    @PostMapping("auth")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> payload) {
        FlowUser user;
        try {
            user = authProvider.doAuthentication(payload);
        } catch (AppException e) {
            LOGGER.error("login failed.", e);
            throw e;
        } catch (Exception e) {
            throw AppException.of(SystemCode.AUTHENTICATE_FAILD);
        }

        String token = tokenHelper.sign(user);
        if (StringUtils.isEmpty(token)) {
            throw AppException.of(SystemCode.SIGN_TOKEN_FAILD);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(user);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout() {
        tokenHelper.logout(TokenHolder.getToken());
        return ResponseEntitys.updated();
    }

}
