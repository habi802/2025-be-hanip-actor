package kr.co.hanipactor.application.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserAddressPostReq {
    private String title;
    private Integer isMain;
    private String address;
    private String postcode;
    private String addressDetail;
}
