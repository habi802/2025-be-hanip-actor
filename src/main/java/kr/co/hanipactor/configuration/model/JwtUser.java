package kr.co.hanipactor.configuration.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class JwtUser {
    private long signedUserId;
    private EnumUserRole role; //인가 처리 때 사용
}