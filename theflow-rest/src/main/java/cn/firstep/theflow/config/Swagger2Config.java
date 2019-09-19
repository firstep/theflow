package cn.firstep.theflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger Config.
 *
 * @author Alvin4u
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = "app.swagger.enabled", matchIfMissing = true)
public class Swagger2Config {

    @Value("#{ @environment['app.swagger.title'] ?: 'TheFlow' }")
    private String title;

    @Value("#{ @environment['app.swagger.description'] ?: 'TheFlow RESTful API' }")
    private String desc;

    @Value("#{ @environment['app.swagger.termsUrl'] ?: 'https://github.com/firstep' }")
    private String termsUrl;

    @Value("#{ @environment['app.swagger.version'] ?: '1.0' }")
    private String version;

    @Value("#{ @environment['app.swagger.contact.name'] ?: 'Alvin4u' }")
    private String contactName;

    @Value("#{ @environment['app.swagger.contact.url'] ?: 'https://github.com/firstep' }")
    private String contactUrl;

    @Value("#{ @environment['app.swagger.contact.email'] ?: 'firstep@qq.com' }")
    private String contactEmail;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors
                        .basePackage("cn.firstep.theflow.api"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(desc)
                .termsOfServiceUrl(termsUrl)
                .version(version)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .build();
    }

    /** swagger-ui.html support Authorization header */

    private List<ApiKey> securitySchemes() {
        List<ApiKey> schemes = new ArrayList<>();
        schemes.add(new ApiKey(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION, "header"));
        return schemes;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.any())
                        .build()
        );
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference(HttpHeaders.AUTHORIZATION, authorizationScopes));
        return securityReferences;
    }

}
