package kr.co.hanipactor.configuration.enumcode.model;

import jakarta.persistence.Converter;
import kr.co.hanipactor.configuration.enumcode.AbstractEnumCodeConverter;
import kr.co.hanipactor.configuration.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EnumStoreCategory implements EnumMapperType {
    KOREAN("01", "한식"),
    CHINESE("02", "중식"),
    JAPANESE("03", "일식"),
    WESTERN("04", "양식"),
    DESSERT("05", "디저트"),
    SNACK("06", "분식"),
    FAST("07", "패스트푸드"),
    ASIAN("08", "아시안"),
    CHICKEN("09", "치킨"),
    PIZZA("10", "피자"),
    NIGHT("11", "야식");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumStoreCategory> {
        public CodeConverter() {
            super(EnumStoreCategory.class, false);
        }
    }

    public static EnumStoreCategory valueOfCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown EnumStoreCategory code: " + code));
    }
}
