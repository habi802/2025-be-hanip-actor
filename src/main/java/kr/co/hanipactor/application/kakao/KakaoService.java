package kr.co.hanipactor.application.kakao;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.application.kakao.model.KakaoSignInReq;
import kr.co.hanipactor.application.kakao.model.KakaoSignInRes;
import kr.co.hanipactor.application.user.UserRepository;
import kr.co.hanipactor.application.user.model.UserLoginDto;
import kr.co.hanipactor.application.user.model.UserLoginRes;
import kr.co.hanipactor.configuration.constants.ConstKaKaoLogin;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.jwt.JwtTokenManager;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.entity.User;
import kr.co.hanipactor.openfeign.kakao.KakaoAuthClient;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final ConstKaKaoLogin constKakao;
    private final KakaoAuthClient kakaoAuthClient;
    private final UserRepository  userRepository;
    private final JwtTokenManager jwtTokenManager;

    public KakaoSignInRes getKakaoLogin(String code, HttpServletResponse response) throws Exception {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "authorization_code");
        body.add("client_id", constKakao.getClientId());
        body.add("redirect_uri", constKakao.getRedirectUri());
        body.add("code", code);

        KakaoSignInRes res =  kakaoAuthClient.getAccessToken(body);
        res = decodeIdToken(res);

        // 여기서 회원가입인지 로그인인지 처리 하려고 함..
        User user = userRepository.findByLoginId(res.getSub());
        Integer storeId = 0;
        if (user == null) {
            // 신규 회원 → 가입 처리

            SignInProviderType kakao = SignInProviderType.KAKAO;
            EnumUserRole role = EnumUserRole.CUSTOMER;

            User userInfo = new User();

            userInfo.setProviderType(kakao);
            userInfo.setRole(role);
            userInfo.setName(res.getNickname());
            userInfo.setLoginId(res.getSub());
            userInfo.setImagePath(res.getPicture());
            // 비밀번호 뭐로 할지 고민
            String pw = "kakao_LoginPw_Hanip_Custromer_info";
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
    public KakaoSignInRes decodeIdToken(KakaoSignInRes res) throws Exception {
        String[] parts = res.getId_token().split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("잘못된 ID 토큰");

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

        res.setNickname((String) payload.get("nickname"));
        res.setPicture((String) payload.get("picture"));
        res.setSub((String) payload.get("sub"));
        return res;
    }
}
