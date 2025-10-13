package kr.co.hanipactor.openfeign.naver;

import kr.co.hanipactor.application.naver.model.NaverLoginRes;
import kr.co.hanipactor.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "hanip-action",
        contextId = "NaverLoginClient",
        url = "https://openapi.naver.com/v1",
        configuration = FeignConfiguration.class)
public interface NaverLoginClient {
    @PostMapping(value = "/nid/me", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    NaverLoginRes getAccessToken(@RequestHeader HttpHeaders formData);
}
