package org.clc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @version 1.0
 * Swagger配置类，默认访问路径：<a href="http://localhost:8080/swagger-ui/index.html">...</a>
 * 增强版：<a href="http://localhost:8080/doc.html">...</a>
 */
@Configuration
@Slf4j
@CrossOrigin
public class SwaggerConfig {
    @Bean
    public OpenAPI adminApiDoc() {
        Info apiInfo = new Info()
                .title("知己学伴接口文档")
                .version("2.0")
                .description("知己学伴接口文档");
        return new OpenAPI().info(apiInfo);
    }

    @Bean
    public GroupedOpenApi adminGroup() {
        Info apiInfo = new Info()
                .title("知己学伴接口文档")
                .version("2.0")
                .description("知己学伴接口文档");
        return GroupedOpenApi.builder()
                .group("管理端接口")
                .pathsToMatch("/admin/**") // 匹配路径
                .addOpenApiCustomizer(openApi -> openApi.info(adminApiDoc().getInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi userGroup() {
        Info apiInfo = new Info()
                .title("知己学伴接口文档")
                .version("2.0")
                .description("知己学伴接口文档");
        return GroupedOpenApi.builder()
                .group("用户端接口")
                .pathsToMatch("/user/**") // 匹配路径
                .addOpenApiCustomizer(openApi -> openApi.info(adminApiDoc().getInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi loginGroup() {
        Info apiInfo = new Info()
                .title("知己学伴接口文档")
                .version("2.0")
                .description("知己学伴接口文档");
        return GroupedOpenApi.builder()
                .group("登录相关接口")
                .pathsToMatch("/login/**") // 匹配路径
                .addOpenApiCustomizer(openApi -> openApi.info(adminApiDoc().getInfo()))
                .build();
    }

    @Bean
    public GroupedOpenApi uploadGroup() {
        Info apiInfo = new Info()
                .title("知己学伴接口文档")
                .version("2.0")
                .description("知己学伴接口文档");
        return GroupedOpenApi.builder()
                .group("文件上传相关接口")
                .pathsToMatch("/upload/**") // 匹配路径
                .addOpenApiCustomizer(openApi -> openApi.info(adminApiDoc().getInfo()))
                .build();
    }

}
