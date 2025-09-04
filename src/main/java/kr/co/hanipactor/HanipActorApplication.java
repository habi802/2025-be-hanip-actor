package kr.co.hanipactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ConfigurationPropertiesScan
@SpringBootApplication
public class HanipActorApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanipActorApplication.class, args);
    }

}
