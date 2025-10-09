package kr.co.hanipactor.application.kakao;


import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.application.kakao.model.KakaoSignInRes;
import kr.co.hanipactor.configuration.constants.ConstKaKaoLogin;
import kr.co.hanipactor.configuration.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/kakao-login")
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;
    private final ConstKaKaoLogin constKaKaoLogin;


    @PostMapping("/login")
    public ResponseEntity<ResultResponse<KakaoSignInRes>> KakaoLogin(@RequestBody Map<String,String> body, HttpServletResponse response) throws Exception{

        String code = body.get("code");
        KakaoSignInRes res = kakaoService.getKakaoLogin(code,response);

        return ResponseEntity.ok(new ResultResponse<>(200, "토큰 발급 성공", res));
    }


    @GetMapping
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String clientId = constKaKaoLogin.getClientId();
        String redirectUri = constKaKaoLogin.getRedirectUri();
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri ;
        response.sendRedirect(kakaoAuthUrl);
    }

}
