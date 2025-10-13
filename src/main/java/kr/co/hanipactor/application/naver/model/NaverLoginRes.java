package kr.co.hanipactor.application.naver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginRes {
    private String resultcode;
    private String message;
    private NaverProfile response;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class NaverProfile {
        private String id;
        private String name;
        private String email;
        private String mobile;
        private String profile_image;
    }

}
