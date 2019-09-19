package cn.firstep.theflow.config;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.SystemCode;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.ProcessEngineConfiguration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Alvin4u
 */
@Configuration
public class DatabaseConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    private static final String THEFLOW_CHANGELOG_PREFIX = "ACT_FO_";
    private static final String MODELER_CHANGELOG_PREFIX = "ACT_DE_";

    @Bean
    public ConfigurationCustomizer configurationCustomizer(ProcessEngineConfiguration engineConfig) {
        return configuration -> {
            String databaseTablePrefix = engineConfig.getDatabaseTablePrefix();
            String databaseType = engineConfig.getDatabaseType();

            Properties properties = new Properties();
            properties.put("prefix", databaseTablePrefix);
            properties.put("limitBefore", "");
            properties.put("limitAfter", "");
            properties.put("limitBetween", "");
            properties.put("limitOuterJoinBetween", "");
            properties.put("limitBeforeNativeQuery", "");
            properties.put("blobType", "BLOB");
            properties.put("boolValue", "TRUE");
            String wildcardEscapeClause = "";
            if (StringUtils.isNotEmpty(engineConfig.getDatabaseWildcardEscapeCharacter())) {
                wildcardEscapeClause = " escape '" + engineConfig.getDatabaseWildcardEscapeCharacter() + "'";
            }
            properties.put("wildcardEscapeClause", wildcardEscapeClause);
            if (databaseType != null) {
                try {
                    properties.load(getResourceAsStream(engineConfig, engineConfig.pathToEngineDbProperties()));
                } catch (IOException e) {
                    LOGGER.error("Error while set mybatis variable.", e);
                    throw AppException.of(SystemCode.START_CONFIG_ERROR);
                }
            }
            configuration.setVariables(properties);
        };
    }

    @Bean("theflow-liquibase")
    public Liquibase theFlowLiquibase(@Qualifier("dataSource") DataSource dataSource) {
        Liquibase liquibase = null;
        try {
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            database.setDatabaseChangeLogTableName(THEFLOW_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
            database.setDatabaseChangeLogLockTableName(THEFLOW_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());

            liquibase = new Liquibase("META-INF/liquibase/theflow-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("TheFlow");
            return liquibase;
        } catch (Exception e) {
            LOGGER.error("theflow-liquibase update error.", e);
            throw AppException.of(SystemCode.START_CONFIG_ERROR);
        } finally {
            closeDatabase(liquibase);
        }
    }

    @Bean("modeler-liquibase")
    public Liquibase modelerLiquibase(@Qualifier("dataSource")DataSource dataSource) {
        LOGGER.info("Configuring Liquibase");

        Liquibase liquibase = null;
        try {
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            database.setDatabaseChangeLogTableName(MODELER_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
            database.setDatabaseChangeLogLockTableName(MODELER_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());

            liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("flowable");
            return liquibase;

        } catch (Exception e) {
            LOGGER.error("modeler-liquibase update error.", e);
            throw AppException.of(SystemCode.START_CONFIG_ERROR);
        } finally {
            closeDatabase(liquibase);
        }
    }

    private void closeDatabase(Liquibase liquibase) {
        if (liquibase != null) {
            Database database = liquibase.getDatabase();
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException e) {
                    LOGGER.warn("Error closing database", e);
                }
            }
        }
    }

    private InputStream getResourceAsStream(ProcessEngineConfiguration engineConfig, String resource) {
        ClassLoader classLoader = engineConfig.getClassLoader();
        if (classLoader != null) {
            return classLoader.getResourceAsStream(resource);
        } else {
            return this.getClass().getClassLoader().getResourceAsStream(resource);
        }
    }
}
