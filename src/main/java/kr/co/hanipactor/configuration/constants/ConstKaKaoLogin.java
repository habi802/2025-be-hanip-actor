package kr.co.hanipactor.configuration.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
public class ConstKaKaoLogin {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
