package kr.co.hanipactor.application.useraddress.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserAddressPostReq {
    private String title;
    private String postcode;
    private String address;
    private String addressDetail;
}
