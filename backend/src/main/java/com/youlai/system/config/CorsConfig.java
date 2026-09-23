package com.youlai.system.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

/** Browser access is limited to the local UI; there are no account credentials. */
@Configuration
public class CorsConfig {
    public static final List<String> LOCAL_ORIGINS = List.of(
            "http://127.0.0.1:3000", "http://localhost:3000",
            "http://127.0.0.1:4173", "http://localhost:4173",
            "http://127.0.0.1:8989", "http://localhost:8989");

    @Bean
    public FilterRegistrationBean<LocalRequestFilter> localRequestFilter() {
        FilterRegistrationBean<LocalRequestFilter> bean = new FilterRegistrationBean<>(new LocalRequestFilter());
        bean.setOrder(-102);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(LOCAL_ORIGINS);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type", "Accept", "X-Requested-With"));
        cors.setExposedHeaders(List.of("Content-Disposition"));
        cors.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(-101);
        return bean;
    }
}
