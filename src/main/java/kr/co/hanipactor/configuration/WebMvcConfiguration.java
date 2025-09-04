package kr.co.hanipactor.configuration;

import kr.co.hanipactor.configuration.constants.ConstFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration //빈등록
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ConstFile constFile;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile/pic/**")
                .addResourceLocations("file:" + constFile.getUploadDirectory());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // RestController의 모든 URL에 "/api/auth" prefix를 설정
        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
    }

}
