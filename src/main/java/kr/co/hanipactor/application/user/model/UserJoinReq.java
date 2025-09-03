package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.application.store.model.StoreJoinReq;
import kr.co.hanipactor.application.useraddress.model.UserAddressPostReq;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class UserJoinReq {
    private int id;
    private String name;
    private String loginId;
    private String loginPw;
    private String phone;
    private String email;
    private String imagePath;
    private EnumUserRole role;
    private StoreJoinReq storeJoinReq;
    private List<UserAddressPostReq> userAddressPostReq;
}
