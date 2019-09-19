package cn.firstep.theflow.auth;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.SystemCode;
import cn.firstep.theflow.model.response.ErrorResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.session.NoSessionCreationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Stateless Filter.
 *
 * @author Alvin4u
 */
public class StatelessFilter extends NoSessionCreationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatelessFilter.class);

    private static final String APP_NAME = "FlowApp";


    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        super.onPreHandle(request, response, mappedValue);

        Subject subject = SecurityUtils.getSubject();

        if (subject.isAuthenticated()) {
            return true;
        }

        try {
            StatelessToken token = new StatelessToken(getToken(request));
            subject.login(token);
            token = (StatelessToken) subject.getPrincipal();
            if (token.isRefreshable()) {// 当token刷新，返回新的token
                WebUtils.toHttp(response).setHeader(HttpHeaders.AUTHORIZATION, token.getToken());
            }
            authSuccess(token);
        } catch (Exception e) {
            String message = transMessage(e);
            sendChallenge(response, message);
            return false;
        }

        return true;
    }

    protected void authSuccess(StatelessToken token) {
        TokenHolder.setToken(token);
    }

    @Override
    public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception)
            throws Exception {
        super.afterCompletion(request, response, exception);
        TokenHolder.removeToken();
    }

    private String transMessage(Exception e) {
        AppException except = e instanceof AppException ? (AppException) e : AppException.of(SystemCode.UNAUTHENTICATION);
        if (!(e instanceof AppException)) LOGGER.error("Authentication required", e);
        return ErrorResponse.of(except).toJSONString();
    }

    private void sendChallenge(ServletResponse response, String message) {
        HttpServletResponse resp = WebUtils.toHttp(response);
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"" + APP_NAME + "\"");
        resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            resp.getWriter().print(message);
            resp.getWriter().flush();
        } catch (IOException e) {
            LOGGER.error("Sending 401 Authentication challenge response failed.", e);
        }
    }

    private String getToken(ServletRequest request) {
        String authzHeader = WebUtils.toHttp(request).getHeader(HttpHeaders.AUTHORIZATION);
        if (authzHeader == null || !authzHeader.toLowerCase(Locale.ENGLISH).startsWith("bearer")) {
            return null;
        }
        String[] authTokens = authzHeader.split(" ");
        if (authTokens.length < 2) {
            return null;
        }
        return authTokens[1];
    }
}
