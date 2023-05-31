package dev.artur.deliveryfeeservice.config;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//This configuration makes requestparams case-insensitive,
// so that both ?city=TaRtU and ?city=TARTU are fine
@Service
public class MVCconfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        ApplicationConversionService.configure(registry);
    }
}
