package com.tsu.mealtracker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import java.time.Duration;
import java.util.Locale;

@Configuration
public class LocalizationConfig {

    @Bean
    public LocaleResolver localeResolver()  {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.forLanguageTag("ka"));       // set default locale to GEORGIAN
        localeResolver.setCookieMaxAge(Duration.ofMinutes(60));
        // localeResolver.setCookieName("lang");  // remove because it's deprecated
        localeResolver.setCookiePath("/");
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
}
