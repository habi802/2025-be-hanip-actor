package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UserGetRes {
    private long id;
    private String name;
    private String loginId;
    private String phone;
    private String email;
    private String imagePath;
    private String postcode;
    private String address;
    private String addressDetail;
    private EnumUserRole role;
    private LocalDateTime created;
}
