package kr.co.hanipactor.application.naver;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.application.naver.model.NaverLoginRes;
import kr.co.hanipactor.application.naver.model.NaverTokenRes;
import kr.co.hanipactor.application.user.UserRepository;
import kr.co.hanipactor.application.user.model.UserLoginDto;
import kr.co.hanipactor.application.user.model.UserLoginRes;
import kr.co.hanipactor.configuration.constants.ConstNaverLogin;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.jwt.JwtTokenManager;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.entity.User;
import kr.co.hanipactor.openfeign.naver.NaverAuthClient;
import kr.co.hanipactor.openfeign.naver.NaverLoginClient;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverService {
    private final ConstNaverLogin constNaverLogin;
    private final NaverAuthClient naverAuthClient;
    private final NaverLoginClient naverLoginClient;
    private final UserRepository userRepository;
    private final JwtTokenManager jwtTokenManager;

    public NaverTokenRes naverToken(String code,String state)throws Exception {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", constNaverLogin.getClientId());
        body.add("client_secret", constNaverLogin.getClientSecret());
        body.add("redirect_uri", constNaverLogin.getRedirectUri());
        body.add("code", code);
        body.add("state", state);

        NaverTokenRes res =  naverAuthClient.getAccessToken(body);

        return res;
    }

    public NaverLoginRes naverLogin(String token, HttpServletResponse response)throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer " + token);

        NaverLoginRes res = naverLoginClient.getAccessToken(headers);

        // 여기서 회원가입인지 로그인인지 처리 하려고 함..
        User user = userRepository.findByLoginId(res.getResponse().getId());
        Integer storeId = 0;
        if (user == null) {
            SignInProviderType naver = SignInProviderType.NAVER;
            EnumUserRole role = EnumUserRole.CUSTOMER;

            User userInfo = new User();

            userInfo.setProviderType(naver);
            userInfo.setRole(role);
            userInfo.setName(res.getResponse().getName());
            userInfo.setPhone(res.getResponse().getMobile());
            userInfo.setLoginId(res.getResponse().getId());
            userInfo.setImagePath(res.getResponse().getProfile_image());
            userInfo.setEmail(res.getResponse().getEmail());
            // 비밀번호 뭐로 할지 고민
            String pw = "naver_LoginPw_Hanip_Custromer_info";
            String hashedPw = BCrypt.hashpw(pw, BCrypt.gensalt());
            userInfo.setLoginPw(hashedPw);
            userRepository.save(userInfo);

            JwtUser jwtUser =  new JwtUser(userInfo.getId(), userInfo.getRole());

            UserLoginRes userLoginRes = UserLoginRes.builder()
                    .id(userInfo.getId())
                    .storeId(storeId)
                    .role(userInfo.getRole())
                    .build();

            UserLoginDto userLogDto = UserLoginDto.builder()
                    .jwtUser(jwtUser)
                    .userLoginRes(userLoginRes)
                    .build();


            jwtTokenManager.issue(response, userLogDto.getJwtUser());
        } else {
            JwtUser jwtUser =  new JwtUser(user.getId(), user.getRole());

            UserLoginRes userLoginRes = UserLoginRes.builder()
                    .id(user.getId())
                    .storeId(storeId)
                    .role(user.getRole())
                    .build();
            UserLoginDto userLogDto = UserLoginDto.builder()
                    .jwtUser(jwtUser)
                    .userLoginRes(userLoginRes)
                    .build();
            jwtTokenManager.issue(response, userLogDto.getJwtUser());
        }

        return res;
    }
}
