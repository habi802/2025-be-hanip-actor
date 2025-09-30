package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserPostReq {
    private String loginId;
    private EnumUserRole role;
}
