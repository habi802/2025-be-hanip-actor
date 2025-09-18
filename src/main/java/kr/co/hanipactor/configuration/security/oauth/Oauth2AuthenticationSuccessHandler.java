package kr.co.hanipactor.configuration.security.oauth;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.configuration.constants.ConstJwt;
import kr.co.hanipactor.configuration.constants.ConstOAuth2;
import kr.co.hanipactor.configuration.jwt.JwtTokenManager;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.model.UserPrincipal;
import kr.co.hanipactor.configuration.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final Oauth2AuthenticationRequestBasedOnCookieRepository repository;
    private final JwtTokenManager tokenProvider;
    private final ConstOAuth2 constOAuth2;
    private final CookieUtils cookieUtils;
    private final ConstJwt constJwt;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
        throws IOException, ServletException {
        if(res.isCommitted()) { //응답 객체가 만료된 경우 (이전 프로세스에서 응답처리를 했는 상태)
            log.error("onAuthenticationSuccess called with a committed response {}", res);
            return;
        }
        String targetUrl = this.determineTargetUrl(req, res, auth);
        log.info("onAuthenticationSuccess targetUrl={}", targetUrl);
        clearAuthenticationAttributes(req, res);
        getRedirectStrategy().sendRedirect(req, res, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
        String redirectUrl = cookieUtils.getValue(req, constOAuth2.redirectUriParamCookieName, String.class);

        log.info("determineTargetUrl > getDefaultTargetUrl(): {}", getDefaultTargetUrl());

        String targetUrl = redirectUrl == null ? getDefaultTargetUrl() : redirectUrl;

        //쿼리스트링 생성을 위한 준비과정
        UserPrincipal myUserDetails = (UserPrincipal) auth.getPrincipal();

        OAuth2JwtUser oAuth2JwtUser = (OAuth2JwtUser)myUserDetails.getJwtUser();

        JwtUser jwtUser = new JwtUser(myUserDetails.getSignedUserId(), oAuth2JwtUser.getRole());

        //AT, RT 생성
        String accessToken = tokenProvider.generateAccessToken(jwtUser);
        String refreshToken = tokenProvider.generateRefreshToken(jwtUser);

        cookieUtils.setCookie(res, constJwt.getAccessTokenCookieName()
                , accessToken
                , constJwt.getAccessTokenCookieValiditySeconds()
                , constJwt.getAccessTokenCookiePath(), null);

        cookieUtils.setCookie(res, constJwt.getRefreshTokenCookieName()
                , refreshToken
                , constJwt.getRefreshTokenCookieValiditySeconds()
                , constJwt.getRefreshTokenCookiePath(), null);

        /*
            쿼리스트링 생성
            targetUrl: /fe/redirect
            accessToken: aaa
            userId: 20
            nickName: 홍길동
            pic: abc.jpg
            값이 있다고 가정하고
            "fe/redirect?access_token=aaa&user_id=20&nick_name=홍길동&pic=abc.jpg"
         */
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("user_id", oAuth2JwtUser.getSignedUserId())
                .queryParam("nick_name", oAuth2JwtUser.getNickName()).encode()
                .queryParam("pic", oAuth2JwtUser.getPic())
                .build()
                .toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest req, HttpServletResponse res) {
        super.clearAuthenticationAttributes(req);
        repository.removeAuthorizationCookies(res);
    }
}
