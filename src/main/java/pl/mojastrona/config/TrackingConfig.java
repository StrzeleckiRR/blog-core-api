package pl.mojastrona.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.mojastrona.tracking.SeparatorFilter;
import pl.mojastrona.tracking.TrackingFilter;

@Configuration
public class TrackingConfig {
    @Bean
    FilterRegistrationBean<TrackingFilter> registerTrackingFilter(){
        FilterRegistrationBean<TrackingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TrackingFilter());
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    FilterRegistrationBean<SeparatorFilter> registerSeparatorFilter(){
        FilterRegistrationBean<SeparatorFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SeparatorFilter());
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
