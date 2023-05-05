package com.video;

import com.video.controller.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 汤垚平
 * @version 1.0
 * 配置头像上传显示
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*addResoureHandler：指的是对外暴露的访问路径
        addResourceLocations：指的是内部文件放置的目录*/
        registry.addResourceHandler("/**").
                addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:E:/workshop_wxxcx/video-dev/")
                .addResourceLocations("file:E:/workshop_wxxcx/");
    }

    @Bean(initMethod="init")
    public ZKCurator zkCurator() {
        return new ZKCurator();
    }

    @Bean
    public MiniInterceptor miniInterceptor() {
        return new MiniInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
                .addPathPatterns("/video/upload", "/video/uploadCover",
                        "/video/userLike", "/video/userUnLike",
                        "/video/saveComment")
                .addPathPatterns("/bgm/list")
                .excludePathPatterns("/user/queryPublisher");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
