package kr.co.hanipactor.application.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLoginRes {
    private int id;
    @JsonIgnore
    private int storeId;
    private EnumUserRole role;
    @JsonIgnore
    private String loginPw;
}
