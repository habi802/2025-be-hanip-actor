package kr.co.hanipactor.configuration.utils;

import io.micrometer.common.util.StringUtils;
import kr.co.hanipactor.configuration.enumcode.EnumMapperType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumConvertUtils {
    // String(Code 값) to Enum
    public static <E extends Enum<E> & EnumMapperType> E ofCode(Class<E> enumClass, String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        return EnumSet.allOf(enumClass).stream() // enum을 Stream화 하기 위함
                .filter(item -> item.getCode().equals(code))
                .findFirst().orElse(null);
    }

    // Enum to String(Code 값)
    public static <E extends Enum<E> & EnumMapperType> String toCode(E enumItem) {
        if (enumItem == null) {
            return null;
        }

        return enumItem.getCode();
    }
}
