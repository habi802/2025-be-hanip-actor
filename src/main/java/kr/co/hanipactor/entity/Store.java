package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumDayOfWeek;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stores")
public class Store extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    @Comment("사장 아이디(users)")
    private User user;

    @Column(nullable = false, length = 50)
    @Comment("상호명")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Comment("가게 소개")
    private String comment;

    @Column(nullable = false, length = 12)
    @Comment("사업자 등록번호")
    private String businessNumber;

    @Column(nullable = false, length = 200)
    @Comment("사업자 등록증 이미지")
    private String licensePath;

    @Column(length = 200)
    @Comment("가게 이미지")
    private String imagePath;

    @Column(nullable = false, length = 12)
    @Comment("우편 번호")
    private String postcode;

    @Column(nullable = false, length = 100)
    @Comment("주소")
    private String address;

    @Column(length = 100)
    @Comment("상세 주소")
    private String addressDetail;

    @Column(nullable = false, length = 20)
    @Comment("전화번호")
    private String tel;

    @Column(nullable = false, length = 20)
    @Comment("대표자 이름")
    private String ownerName;

    @Column(nullable = false, columnDefinition = "DATE")
    @Comment("개업연월일")
    private String openDate;

    @Column(nullable = false, columnDefinition = "INT(11) DEFAULT '0'")
    @Comment("최소 배달 요금")
    private Integer minDeliveryFee;

    @Column(nullable = false, columnDefinition = "INT(11) DEFAULT '0'")
    @Comment("최대 배달 요금")
    private Integer maxDeliveryFee;

    @Column(nullable = false, columnDefinition = "INT(11) DEFAULT '0'")
    @Comment("최소 주문 금액")
    private Integer minAmount;

    @Column(nullable = false, columnDefinition = "TIME DEFAULT '11:00:00'")
    @Comment("영업 시작 시간")
    private String openTime;

    @Column(nullable = false, columnDefinition = "TIME DEFAULT '23:00:00'")
    @Comment("영업 종료 시간")
    private String closeTime;

    @Column(columnDefinition = "TEXT")
    @Comment("이벤트 알림")
    private String eventComment;

    @Column(columnDefinition = "VARCHAR(2)")
    @Comment("휴무일(01: 월요일, 02: 화요일, ... 07: 일요일")
    private EnumDayOfWeek closedDay;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    @Comment("영업중 여부(0: 영업 대기, 1: 영업중)")
    private Integer isOpen;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    @Comment("포장 가능 여부(0: 불가능, 1: 가능)")
    private Integer isPickUp;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    @Comment("영업 승인 여부(관리자 허용, 0: 비활성화, 1: 활성화)")
    private Integer isActive;

    @Column(nullable = false, columnDefinition = "DOUBLE(3, 2) DEFAULT '0'")
    @Comment("평균 별점")
    private Double rating;

    @Column(nullable = false, columnDefinition = "INT DEFAULT '0'")
    @Comment("총 찜 수")
    private Integer favorites;

    @PrePersist
    public void prePersist() {
        if (this.minDeliveryFee == null) {
            this.minDeliveryFee = 0;
        }
        if (this.maxDeliveryFee == null) {
            this.maxDeliveryFee = 0;
        }
        if (this.minAmount == null) {
            this.minAmount = 0;
        }
        if (this.openTime == null) {
            this.openTime = "11:00:00";
        }
        if (this.closeTime == null) {
            this.closeTime = "23:00:00";
        }
        if (this.isOpen == null) {
            this.isOpen = 0;
        }
        if (this.isPickUp == null) {
            this.isPickUp = 0;
        }
        if (this.isActive == null) {
            this.isActive = 0;
        }
        if (this.rating == null) {
            this.rating = 0.0;
        }
        if (this.favorites == null) {
            this.favorites = 0;
        }
    }
}
