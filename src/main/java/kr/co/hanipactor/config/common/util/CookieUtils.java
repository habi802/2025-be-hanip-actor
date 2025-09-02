package kr.co.hanipactor.config.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

@Slf4j
@Component
public class CookieUtils {

    public void setCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true); //보안 쿠키 설정
        response.addCookie(cookie);
    }

    public void setCookie(HttpServletResponse res, String name, Object value, int maxAge, String path) {
        this.setCookie(res, name, serializeObject(value), maxAge, path);
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

    public void deleteCookie(HttpServletResponse response, String name, String path) {
        setCookie(response, name, null, 0, path);
    }
}
