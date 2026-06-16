package pl.mojastrona.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;

@Configuration
public class ObjectMapperConfig {

    @Autowired
    public void configureObjectMapper(ObjectMapper objectMapper){
        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

        SimpleModule customPageImpSerializationModule = new SimpleModule();
        customPageImpSerializationModule.addSerializer(PageImpl.class, new PageImplSerializer());
        objectMapper.registerModule(customPageImpSerializationModule);


    }
}
