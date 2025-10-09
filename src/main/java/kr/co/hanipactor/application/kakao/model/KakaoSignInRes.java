package kr.co.hanipactor.application.kakao.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;
import java.util.Map;

@Getter
@Setter
public class KakaoSignInRes {
    private String token_type;
    private String access_token;
    private String error_description;
    private String state;
    private Integer expires_in;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String scope;
    private String id_token;


    private String picture;
    private String nickname;
    private String sub;
}
