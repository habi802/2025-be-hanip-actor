package kr.co.hanipactor.application.manager.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import kr.co.hanipactor.entity.Store;
import kr.co.hanipactor.entity.StoreCategory;
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

            return cb.greaterThanOrEqualTo(root.get("openDate"), startDate);
        };
    }

    // 개업연월일(종료)
    public static Specification<Store> hasEndOpenDate(String endDate) {
        return (root, query, cb) -> {
            if (endDate == null || endDate.isEmpty()) {
                return null;
            }

            return cb.lessThanOrEqualTo(root.get("openDate"), endDate);
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

    // 대표자명
    public static Specification<Store> hasOwnerName(String ownerName) {
        return (root, query, cb) -> {
            if (ownerName == null || ownerName.isEmpty()) {
                return null;
            }

            return cb.like(root.get("ownerName"), "%" + ownerName + "%");
        };
    }

    // 사업자 등록번호
    public static Specification<Store> hasBusinessNumber(String businessNumber) {
        return (root, query, cb) -> {
            if (businessNumber == null || businessNumber.isEmpty()) {
                return null;
            }

            return cb.like(root.get("businessNumber"), "%" + businessNumber + "%");
        };
    }

    // 카테고리
    public static Specification<Store> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isEmpty()) {
                return null;
            }

            Join<Store, StoreCategory> join = root.join("categories", JoinType.INNER);

            EnumStoreCategory enumStoreCategory = EnumStoreCategory.valueOfCode(category);
            return cb.equal(join.get("storeCategoryId").get("category"), enumStoreCategory);
        };
    }

    // 주소
    public static Specification<Store> hasAddress(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isEmpty()) {
                return null;
            }

            return cb.or(
                cb.like(root.get("postcode"), "%" + address + "%"),
                cb.like(root.get("address"), "%" + address + "%"),
                cb.like(root.get("addressDetail"), "%" + address + "%")
            );
        };
    }

    // 전화번호
    public static Specification<Store> hasTel(String tel) {
        return (root, query, cb) -> {
            if (tel == null || tel.isEmpty()) {
                return null;
            }

            return cb.like(root.get("tel"), "%" + tel + "%");
        };
    }

    // 영업 승인 상태
    public static Specification<Store> hasIsActive(String isActive) {
        return (root, query, cb) -> {
            if (isActive == null || isActive.isEmpty()) {
                return null;
            }

            return cb.equal(root.get("isActive"), Integer.valueOf(isActive));
        };
    }
}
