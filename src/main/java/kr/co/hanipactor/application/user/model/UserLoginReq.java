package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.Getter;

@Getter
public class UserLoginReq {
    private String loginId;
    private String loginPw;
    private EnumUserRole role;
}
