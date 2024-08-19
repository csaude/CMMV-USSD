package mz.org.fgh.vmmc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SuppressWarnings("deprecation")
@Configuration
@EnableSwagger2
public class SwaggerConfig  extends WebMvcConfigurerAdapter {    
  
   @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("mz.org.fgh"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo());
    }

    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("USSD CMMV API Documentation")
            .description("API documentation for CMMV USSD application")
            .version("1.0")
            .build();
    } 
    
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
