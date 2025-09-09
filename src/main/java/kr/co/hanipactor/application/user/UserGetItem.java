package kr.co.hanipactor.application.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserGetItem {
    private Long id;
    private String userNickName;
    private String userPic;
}
