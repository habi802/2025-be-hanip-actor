package kr.co.hanipactor.application.store.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class StorePatchReq {
    private Long id;
    private Double rating;
    private Integer favorites;
}
