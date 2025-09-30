package kr.co.hanipactor.configuration.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "constants.file")
@RequiredArgsConstructor
@ToString
public class ConstFile {
    private final String uploadDirectory;
    private final String userPic;
    private final String storePic;
    private final String storeBannerPic;
    private final String storeMenuPic;
}