package com.harry.core.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


@Configuration
@EnableSwagger2
public class SwaggerConfig {
        // 定义分隔符,配置Swagger多包
        private static final String splitor = ";";
        @Bean
        public Docket createRestApi() {
//            return new Docket(DocumentationType.SWAGGER_2)
//                    .apiInfo(apiInfo())
//                    .select()
//                    // 指定当前包路径，这里就添加了两个包，注意方法变成了basePackage，中间加上成员变量splitor
//                    .apis(basePackage("com.sooner.controller" +splitor+
//                                       "com.sooner.controller.acceptanceArrangement.acceptanceArrangement")//质量安排
//                                        )
//                    .paths(PathSelectors.any())
//                    .build();
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    //加了ApiOperation注解的类，才生成接口文档
                    .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                    .paths(PathSelectors.any())
                    .build()
                    .securitySchemes(security());
        }

        /**
         * 重写basePackage方法，使能够实现多包访问，复制贴上去
         * @author  teavamc
         * @date 2019/1/26
         * @param basePackage
         * @return com.google.common.base.Predicate<springfox.documentation.RequestHandler>
         */
        public static Predicate<RequestHandler> basePackage(final String basePackage) {
            return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
        }

        private static Function<Class<?>, Boolean> handlerPackage(final String basePackage)     {
            return input -> {
                // 循环判断匹配
                for (String strPackage : basePackage.split(splitor)) {
                    boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                    if (isMatch) {
                        return true;
                    }
                }
                return false;
            };
        }

        private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
            return Optional.fromNullable(input.declaringClass());
        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("swagger")
                    .description("描述")
                    .termsOfServiceUrl("")
                    //创建人
                    .contact(new Contact("harry", "http://harry.com", ""))
                    .version("1.0")
                    .build();
    }

    private List<ApiKey> security() {
        return newArrayList(
                new ApiKey("token", "token", "header")
        );
    }
}