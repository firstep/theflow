package cn.firstep.theflow.config;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.SystemCode;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.InputStream;
import java.util.Properties;

/**
 * Load default configuation.
 *
 * @author Alvin4u
 */
public class DefaultConfig implements EnvironmentPostProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultConfig.class);

    private static final String PROPERTIES_PATH = "classpath:theflow-core.properties";

    @SuppressWarnings("deprecation")
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        InputStream is = null;
        try {
            is = new DefaultResourceLoader().getResource(PROPERTIES_PATH).getInputStream();

            Properties properties = new Properties();
            properties.load(is);
            PropertiesPropertySource propertySource = new PropertiesPropertySource("theflow", properties);
            environment.getPropertySources().addLast(propertySource);
            is.close();
        } catch (Exception e) {
            LOGGER.error("Load TheFlow default configuration file error.", e);
            throw AppException.of(SystemCode.START_CONFIG_ERROR);
        } finally {
            IOUtils.closeQuietly(is);
        }

    }

}
