package kr.co.hanipactor.application.manager.specification;

import kr.co.hanipactor.entity.Store;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StoreSpecification {
    public static Specification<Store> hasStartDate(String startDate) {
        return (root, query, cb) -> {
            if (startDate == null || startDate.isEmpty()) {
                return null;
            }

            // 정확한 검색을 위해 00:00:00 으로 시간 설정까지 해줬음
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
        };
    }

    public static Specification<Store> hasEndDate(String endDate) {
        return (root, query, cb) -> {
            if (endDate == null || endDate.isEmpty()) {
                return null;
            }

            // 단순히 날짜만 조건으로 하면 정확한 검색이 안됨
            // 예) 2025-09-01을 조건으로 검색하면 2025-09-01 11:00:00 에 등록한 데이터는 조회가 안됨
            // 그러므로 23:59:59 로 시간을 설정을 해줘야 함
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("createdAt"), endDateTime);
        };
    }
}
