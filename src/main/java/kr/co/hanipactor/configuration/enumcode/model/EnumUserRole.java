package kr.co.hanipactor.configuration.enumcode.model;

import jakarta.persistence.Converter;
import kr.co.hanipactor.configuration.enumcode.AbstractEnumCodeConverter;
import kr.co.hanipactor.configuration.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumUserRole implements EnumMapperType {
    CUSTOMER("01", "고객"),
    OWNER("02", "사장"),
    RIDER("03", "배달원"),
    ADMIN("04", "관리자");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumUserRole> {
        public CodeConverter() {
            super(EnumUserRole.class, false);
        }
    }
}
