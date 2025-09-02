package kr.co.hanipactor.configuration.enumcode;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EnumMapperValue {
    private String code;
    private String value;

    public EnumMapperValue(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
