package kr.co.hanipactor.application.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserLoginRes {
    private long id;
    @JsonIgnore
    private int storeId;
    private EnumUserRole role;
    private String token;
    @JsonIgnore
    private String loginPw;
}
