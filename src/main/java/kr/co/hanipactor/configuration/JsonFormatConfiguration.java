package kr.co.hanipactor.configuration;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonFormatConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        //응답시 Long타입 to String타입 형변환
        return builder -> builder.serializerByType(Long.class, ToStringSerializer.instance);
    }
}
