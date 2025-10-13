package kr.co.hanipactor.application.naver;


import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.application.kakao.model.KakaoSignInRes;
import kr.co.hanipactor.application.naver.model.NaverLoginRes;
import kr.co.hanipactor.application.naver.model.NaverTokenRes;
import kr.co.hanipactor.configuration.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/naver-login")
@RestController
@RequiredArgsConstructor
public class NaverController {
    private final NaverService naverService;

    @PostMapping("/token")
    public ResponseEntity<ResultResponse<NaverTokenRes>> NaverToken(@RequestBody Map<String,String> body) throws Exception{

        String code = body.get("code");
        String state = body.get("state");
        NaverTokenRes res = naverService.naverToken(code,state);

        return ResponseEntity.ok(new ResultResponse<>(200, "토큰 발급 성공", res));
    }

    @PostMapping("/nid")
    public ResponseEntity<ResultResponse<NaverLoginRes>> NaverNid(@RequestBody Map<String,String> body, HttpServletResponse response) throws Exception{

        String token = body.get("token");
        NaverLoginRes res = naverService.naverLogin(token,response);

        return ResponseEntity.ok(new ResultResponse<>(200, "토큰 발급 성공", res));
    }
}
