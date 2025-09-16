package kr.co.hanipactor.application.user.model;

import kr.co.hanipactor.entity.UserAddress;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserAddressGetRes {
    private long id;
    private long userId;
    private Integer isMain;
    private String title;
    private String postcode;
    private String address;
    private String addressDetail;

    public static UserAddressGetRes from(UserAddress userAddress) {
        return UserAddressGetRes.builder()
                .id(userAddress.getId())
                .userId(userAddress.getUser().getId())
                .isMain(userAddress.getIsMain())
                .title(userAddress.getTitle())
                .postcode(userAddress.getPostcode())
                .address(userAddress.getAddress())
                .addressDetail(userAddress.getAddressDetail())
                .build();
    }
}
