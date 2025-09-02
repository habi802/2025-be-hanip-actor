package kr.co.hanipactor.configuration.security.model;

import jakarta.persistence.Converter;
import kr.co.hanipactor.configuration.enumcode.AbstractEnumCodeConverter;
import kr.co.hanipactor.configuration.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SignInProviderType implements EnumMapperType {
      LOCAL("01", "로컬")
    , KAKAO("02", "카카오")
    , NAVER("03", "네이버");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<SignInProviderType> {
        public CodeConverter() {
            super(SignInProviderType.class, false);
        }
    }
}
