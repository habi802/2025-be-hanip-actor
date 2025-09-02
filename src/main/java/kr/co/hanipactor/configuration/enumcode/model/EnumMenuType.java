package kr.co.hanipactor.configuration.enumcode.model;

import jakarta.persistence.Converter;
import kr.co.hanipactor.configuration.enumcode.AbstractEnumCodeConverter;
import kr.co.hanipactor.configuration.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
