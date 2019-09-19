package cn.firstep.theflow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Message basename support wildcard, base on {@link MessageSourceAutoConfiguration}
 *
 * @author Alvin4u
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties
public class MessageSourceConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(MessageSourceConfig.class);

    private static final String PROPERTIES_SUFFIX = ".properties";

    private static final String DEFAULT_MESSAGE_BASENAME_PATH = "i18n/";

    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        String[] baseNames = matchBasename(properties.getBasename());
        messageSource.setBasenames(baseNames);
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());

        return messageSource;
    }

    private String[] matchBasename(String configBasename) {
        List<String> basenamePatterns = new ArrayList<>();
        basenamePatterns.add(DEFAULT_MESSAGE_BASENAME_PATH + "**");
        if (StringUtils.hasText(configBasename)) {
            for (String basename : StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(configBasename))) {
                if (!basename.startsWith(DEFAULT_MESSAGE_BASENAME_PATH)) {
                    basenamePatterns.add(basename);
                }
            }
        }

        PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        return basenamePatterns.stream().flatMap(basenamePattern -> {
            try {
                Resource[] resources = resourceResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basenamePattern + PROPERTIES_SUFFIX);
                return Arrays.stream(resources).map(resource -> {
                    return getMessageBaseName(resource.getFilename(), basenamePattern);
                });
            } catch (IOException e) {
                LOGGER.error("Get message source error.", e);
                return null;
            }
        }).filter(item -> item != null).distinct().toArray(String[]::new);
    }

    private String getMessageBaseName(String fileName, String basenamePattern) {
        if(fileName == null) {
            return null;
        }
        int index = fileName.indexOf('_');
        fileName = fileName.substring(0, index > -1 ? index : fileName.lastIndexOf('.'));

        index = basenamePattern.lastIndexOf('/');
        String folder = index > -1 ? basenamePattern.substring(0, index + 1) : "";

        return folder + fileName;
    }
}
