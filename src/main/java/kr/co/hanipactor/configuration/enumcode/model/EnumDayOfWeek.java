package kr.co.hanipactor.configuration.enumcode.model;

import kr.co.hanipactor.configuration.enumcode.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumDayOfWeek implements EnumMapperType {
    MONDAY("01", "월요일"),
    TUESDAY("02", "화요일"),
    WEDNESDAY("03", "수요일"),
    THURSDAY("04", "목요일"),
    FRIDAY("05", "금요일"),
    SATURDAY("06", "토요일"),
    SUNDAY("07", "일요일");

    private final String code;
    private final String value;
}
