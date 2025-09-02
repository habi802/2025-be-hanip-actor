package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.config.model.JwtUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginDto {
    private UserLoginRes userLoginRes;
    private JwtUser jwtUser;
}
