package kr.co.hanipactor.application.manager.specification;

import kr.co.hanipactor.entity.Store;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StoreSpecification {
    // 가게 등록일(시작)
    public static Specification<Store> hasStartDate(String startDate) {
        return (root, query, cb) -> {
            if (startDate == null || startDate.isEmpty()) {
                return null;
            }

            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
        };
    }

    // 가게 등록일(종료)
    public static Specification<Store> hasEndDate(String endDate) {
        return (root, query, cb) -> {
            if (endDate == null || endDate.isEmpty()) {
                return null;
            }

            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("createdAt"), endDateTime);
        };
    }

    // 개업연월일(시작)
    public static Specification<Store> hasStartOpenDate(String startDate) {
        return (root, query, cb) -> {
            if (startDate == null || startDate.isEmpty()) {
                return null;
            }

            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("openDate"), startDateTime);
        };
    }

    // 개업연월일(종료)
    public static Specification<Store> hasEndOpenDate(String endDate) {
        return (root, query, cb) -> {
            if (endDate == null || endDate.isEmpty()) {
                return null;
            }

            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("openDate"), endDateTime);
        };
    }

    // 상호명
    public static Specification<Store> hasName(String ownerName) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }

            return cb.like(root.get("name"), "%" + name + "%");
        };
    }

    // 상호명
    public static Specification<Store> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }

            return cb.like(root.get("name"), "%" + name + "%");
        };
    }
}
