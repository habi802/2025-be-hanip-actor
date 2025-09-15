package kr.co.hanipactor.application.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserPutReq {
    private String name;
    private String loginPw;
    private String newLoginPw;
    private String phone;
    private String email;
    private String imagePath;
}
