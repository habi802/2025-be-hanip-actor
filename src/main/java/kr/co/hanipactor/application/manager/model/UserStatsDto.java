package kr.co.hanipactor.application.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
public class UserStatsDto {
    private String type;
    private List<String> periods;
}
