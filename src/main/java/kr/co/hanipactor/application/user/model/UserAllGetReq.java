package kr.co.hanipactor.application.user.model;

import lombok.Getter;
import lombok.ToString;

// 유저 전체 조회 검색용 객체
@Getter
@ToString
public class UserAllGetReq {
    private String startDate;
    private String endDate;
    private String loginId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String providerType;
    private String role;
}
