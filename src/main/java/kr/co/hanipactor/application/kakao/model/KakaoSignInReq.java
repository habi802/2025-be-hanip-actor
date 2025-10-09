package kr.co.hanipactor.application.kakao.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoSignInReq {
    private String clientId;
    private String redirectUri;
    private String responseType;
}
