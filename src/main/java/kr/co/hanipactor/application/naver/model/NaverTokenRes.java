package kr.co.hanipactor.application.naver.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverTokenRes {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
    private String error;
    private String error_description;
}
