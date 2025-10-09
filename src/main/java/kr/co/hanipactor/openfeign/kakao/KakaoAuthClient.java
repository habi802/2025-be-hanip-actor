package kr.co.hanipactor.openfeign.kakao;

import kr.co.hanipactor.application.kakao.model.KakaoSignInReq;
import kr.co.hanipactor.application.kakao.model.KakaoSignInRes;
import kr.co.hanipactor.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "hanip-action",
        contextId = "kaKaoAuthClient",
        url = "https://kauth.kakao.com/oauth",
        configuration = FeignConfiguration.class)
public interface KakaoAuthClient {

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoSignInRes getAccessToken(@RequestBody MultiValueMap<String, String> formData);

}