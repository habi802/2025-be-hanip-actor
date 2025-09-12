package kr.co.hanipactor.configuration.security.oauth;



import kr.co.hanipactor.application.user.UserRepository;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.model.UserPrincipal;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.configuration.security.oauth.userinfo.Oauth2UserInfo;
import kr.co.hanipactor.configuration.security.oauth.userinfo.Oauth2UserInfoFactory;
import kr.co.hanipactor.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final Oauth2UserInfoFactory oauth2UserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        try {
            return process(req);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest req) {
        OAuth2User oAuth2User = super.loadUser(req);
        /*
        req.getClientRegistration().getRegistrationId(); 소셜로그인 신청한 플랫폼 문자열값이 넘어온다.
        플랫폼 문자열값은 spring.security.oauth2.client.registration 아래에 있는 속성값들이다. (google, kakao, naver)
         */
        SignInProviderType signInProviderType = SignInProviderType.valueOf(req.getClientRegistration()
                .getRegistrationId()
                .toUpperCase());
        //사용하기 편하도록 규격화된 객체로 변환
        Oauth2UserInfo oauth2UserInfo = oauth2UserInfoFactory.getOauth2UserInfo(signInProviderType, oAuth2User.getAttributes());

        //기존에 회원가입이 되어있는지 체크
        User user = userRepository.findByLoginIdAndProviderType(oauth2UserInfo.getId(), signInProviderType);
        if(user == null) { // 최초 로그인 상황 > 회원가입 처리
            user = new User();
            user.setLoginId(oauth2UserInfo.getId());
            user.setProviderType(signInProviderType);
            user.setLoginPw("");
            user.setName(oauth2UserInfo.getName());
            user.setImagePath(oauth2UserInfo.getProfileImageUrl());
            // 최초 소셜 로그인은 회원가입으로 권한은 고객으로 처리
            user.setRole(EnumUserRole.CUSTOMER);
            userRepository.save(user);
        } else {
            // 프로필 업데이트 동기화
            user.setName(oauth2UserInfo.getName());
            user.setImagePath(oauth2UserInfo.getProfileImageUrl());
        }
        EnumUserRole role = user.getRole();

        String nickName = user.getName() == null ? user.getLoginId() : user.getName();
        JwtUser jwtUser = new OAuth2JwtUser(nickName, user.getImagePath(), user.getId(), role);

        UserPrincipal myUserDetails = new UserPrincipal(jwtUser);
        return myUserDetails;
    }
}






