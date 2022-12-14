package com.beer.BeAPro.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class SwaggerConfig {

    @Bean
    @Profile({"!test && !dev"})
    public Docket disable() {
        return new Docket(DocumentationType.OAS_30).enable(false);
    }

    @Profile({"test || dev"})
    @Bean
    public Docket docket() {

        return new Docket(DocumentationType.OAS_30)
                .consumes(getConsumeContentTypes())
                .useDefaultResponseMessages(false)
                .securityContexts(List.of(this.securityContext()))
                .securitySchemes(List.of(this.apiKey()))
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.
                        basePackage("com.beer.BeAPro.Controller"))
                .paths(PathSelectors.any()).build();
    }

    // ApiInfo 정의
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("비어프로 API 문서")
                .version("0.0.1")
                .build();
    }

    // JWT SecurityContext 구성
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("Authorization", authorizationScopes));
    }

    // ApiKey 정의(JWT 토큰 사용)
    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }


    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("multipart/form-data");
        return consumes;
    }
}