package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.configuration.model.JwtUser;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserLoginDto {
    private UserLoginRes userLoginRes;
    private JwtUser jwtUser;
}
