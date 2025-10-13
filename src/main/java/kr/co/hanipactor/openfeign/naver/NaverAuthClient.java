package kr.co.hanipactor.openfeign.naver;


import kr.co.hanipactor.application.naver.model.NaverTokenRes;
import kr.co.hanipactor.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "hanip-action",
        contextId = "NaverAuthClient",
        url = "https://nid.naver.com/oauth2.0",
        configuration = FeignConfiguration.class)
public interface NaverAuthClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    NaverTokenRes getAccessToken(@RequestBody MultiValueMap<String, String> formData);
}
