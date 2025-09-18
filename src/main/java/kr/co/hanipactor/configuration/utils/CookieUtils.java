package kr.co.hanipactor.configuration.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtils {
    private final Environment environment;

    public void setCookie(HttpServletResponse response, String name, String value, int maxAge, String path, String domain) {
        /*
            쿠버네티스에서 실행되면 프로파일 2개로 실행(prod, kubernetes)
            prod는 도커 이미지를 만들 때 실행명령어에 prod로 서버를 기동하라는 내용 포함되어 있음
            kubernetes는 쿠버네티스가 서버 기동할 때 포함 시킴
         */
        String[] activeProfiles = environment.getActiveProfiles();

        //프로파일에 prod가 포함되어 있는지 확인
        if (domain != null && Arrays.asList(activeProfiles).contains("prod")) {
            //쿠키 생성 방법 (1) ResponseCookie.from 스태틱 메소드 이용
            log.info("CookieUtils - 프로파일에 prod가 있음");
            ResponseCookie cookie = ResponseCookie.from(name, value)
                    .path(path)
                    .maxAge(maxAge)
                    .httpOnly(true)
                    .domain(domain)
                    .secure(true) //https일 때만 쿠키 전송된다.
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        } else {
            Cookie cookie = new Cookie(name, value);
            cookie.setPath(path);
            cookie.setMaxAge(maxAge);
            cookie.setHttpOnly(true); //보안 쿠키 설정
            response.addCookie(cookie);
        }
    }

    public void setCookie(HttpServletResponse res, String name, Object value, int maxAge, String path, String domain) {
        this.setCookie(res, name, serializeObject(value), maxAge, path, domain);
    }

    public String getValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        if(cookie == null) { return null; }
        return cookie.getValue();
    }

    public <T> T getValue(HttpServletRequest req, String name, Class<T> valueType) {
        Cookie cookie = getCookie(req, name);
        if (cookie == null) { return null; }
        if(valueType == String.class) {
            return (T) cookie.getValue();
        }
        return deserializeCookie(cookie, valueType);
    }

    private String serializeObject(Object obj) {
        return Base64.getUrlEncoder().encodeToString( SerializationUtils.serialize(obj) );
    }

    //역직렬화, 문자열값을 객체로 변환
    private <T> T deserializeCookie(Cookie cookie, Class<T> valueType) {
        return valueType.cast(
                SerializationUtils.deserialize( Base64.getUrlDecoder().decode(cookie.getValue()) )
        );
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public void deleteCookie(HttpServletResponse response, String name, String path, String domain) {
        String[] activeProfiles = environment.getActiveProfiles();

        if (domain != null && Arrays.asList(activeProfiles).contains("prod")) {
            setCookie(response, name, null, 0, path, domain);
        } else {
            setCookie(response, name, null, 0, path, null);
        }
    }
}
