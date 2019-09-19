package cn.firstep.theflow.common;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Application context.
 *
 * @author Alvin4u
 */
@Component
public class AppContext implements MessageSourceAware {

    private static MessageSource messageSource;


    public static String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        AppContext.messageSource = messageSource;
    }
}
