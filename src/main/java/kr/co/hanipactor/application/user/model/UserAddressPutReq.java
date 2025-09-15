package kr.co.hanipactor.application.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserAddressPutReq {
    private long id;
    private String title;
    private String address;
    private String postcode;
    private String addressDetail;
}
