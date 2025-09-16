package kr.co.hanipactor.application.manager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStatsReq {
    private String type; // "year", "month", "week", "day"
    private String date; // "2025", "2025-01", "2025-01-01", "2025-01-01"
}
