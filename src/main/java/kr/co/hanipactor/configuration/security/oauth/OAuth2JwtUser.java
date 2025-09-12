package kr.co.hanipactor.configuration.security.oauth;



import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import lombok.Getter;

import java.util.List;

@Getter
public class OAuth2JwtUser extends JwtUser {
    private final String nickName;
    private final String pic;

    public OAuth2JwtUser(String nickName, String pic, long signedUserId, EnumUserRole role) {
        super(signedUserId, role);
        this.nickName = nickName;
        this.pic = pic;
    }
}
