package kr.co.hanipactor.application.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class StoreStatsDto {
    private String type;
    private List<String> periods;
}
