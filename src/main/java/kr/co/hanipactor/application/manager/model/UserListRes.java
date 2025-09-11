package kr.co.hanipactor.application.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

// 유저 전체 조회용 객체
@Getter
@Builder
@ToString
public class UserListRes {
    private long userId;
    private String name;
    private String loginId;
    private String address;
    private String phone;
    private String email;
    private String providerType;
    private String role;
    private String createdAt;
}
