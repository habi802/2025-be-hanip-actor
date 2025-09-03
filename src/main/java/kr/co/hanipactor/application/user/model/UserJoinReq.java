package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.application.store.model.StoreJoinReq;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserJoinReq {
    private int id;
    private String name;
    private String loginId;
    private String loginPw;
    private String postcode;
    private String address;
    private String addressDetail;
    private String phone;
    private String email;
    private String imagePath;
    private EnumUserRole role;
    private final StoreJoinReq storeJoinReq;
}
