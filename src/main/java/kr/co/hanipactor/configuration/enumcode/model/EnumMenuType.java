package kr.co.hanipactor.configuration.enumcode.model;

import jakarta.persistence.Converter;
import kr.co.hanipactor.configuration.enumcode.AbstractEnumCodeConverter;
import kr.co.hanipactor.configuration.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EnumMenuType implements EnumMapperType {
    SINGLE("01", "단품"),
    SET("02", "세트"),
    SIDE("03", "사이드"),
    DRINK("04", "음료수");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumMenuType> {
        public CodeConverter() {
            super(EnumMenuType.class, false);
        }
    }

    // 입력 받은 코드를 가진 enum을 찾아주는 메소드
    public static EnumMenuType fromCode(String code) {
        return Arrays.stream(EnumMenuType.values())
                     .filter(e -> e.code.equals(code))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 코드입니다."));
    }
}
