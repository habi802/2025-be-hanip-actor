package kr.co.hanipactor.application.manager.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import kr.co.hanipactor.entity.User;
import kr.co.hanipactor.entity.UserAddress;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 유저 검색 조건을 리턴하는 메소드가 들어있는 클래스
public class UserSpecification {
    public static Specification<User> hasStartDate(String startDate) {
        return (root, query, cb) -> {
            if (startDate == null || startDate.isEmpty()) {
                return null;
            }

            // 정확한 검색을 위해 00:00:00 으로 시간 설정까지 해줬음
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
        };
    }

    public static Specification<User> hasEndDate(String endDate) {
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

    public static Specification<User> hasLoginId(String loginId) {
        return (root, query, cb) -> {
            if (loginId == null || loginId.isEmpty()) {
                return null;
            }

            return cb.like(root.get("loginId"), "%" + loginId + "%");
        };
    }

    public static Specification<User> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }

            return cb.like(root.get("name"), "%" + name + "%");
        };
    }

    public static Specification<User> hasAddress(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isEmpty()) {
                return null;
            }

            // User 엔터티와 UserAddress 엔터티를 left join 함
            Join<User, UserAddress> join = root.join("addresses", JoinType.LEFT);

            // isMain 컬럼이 1인(즉, 메인 주소인) 조건을 Predicate 타입의 변수에 저장
            // Predicate 타입은 'boolean 값을 리턴하는 함수형 인터페이스' 라고 함
            Predicate mainPredicate = cb.equal(join.get("isMain"), 1);

            Predicate searchPredicate = cb.or(
                cb.like(join.get("postcode"), "%" + address + "%"),
                cb.like(join.get("address"), "%" + address + "%"),
                cb.like(join.get("addressDetail"), "%" + address + "%")
            );

            return cb.and(mainPredicate, searchPredicate);
        };
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, query, cb) -> {
            if (phone == null || phone.isEmpty()) {
                return null;
            }

            return cb.like(root.get("phone"), "%" + phone + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) {
                return null;
            }

            return cb.like(root.get("email"), "%" + email + "%");
        };
    }

    public static Specification<User> hasProviderType(String providerType) {
        return (root, query, cb) -> {
            if (providerType == null || providerType.isEmpty() || "00".equals(providerType)) {
                return null;
            }

            return cb.equal(root.get("providerType"), providerType);
        };
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {
            if (role == null || role.isEmpty() || "00".equals(role)) {
                return null;
            }

            return cb.equal(root.get("role"), role);
        };
    }
}
