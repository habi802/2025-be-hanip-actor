package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumDayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Store extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false, length = 12)
    private String businessNumber;

    @Column(nullable = false, length = 200)
    private String licensePath;

    @Column(length = 200)
    private String imagePath;

    @Column(nullable = false, length = 12)
    private String postcode;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(length = 100)
    private String addressDetail;

    @Column(nullable = false, length = 20)
    private String tel;

    @Column(nullable = false, length = 20)
    private String ownerName;

    @Column(nullable = false, columnDefinition = "DATE")
    private String openDate;

    @Column(nullable = false, columnDefinition = "INT(11) DEFAULT '0'")
    private int minDeliveryFee;

    @Column(nullable = false, columnDefinition = "INT(11) DEFAULT '0'")
    private int maxDeliveryFee;

    @Column(nullable = false, columnDefinition = "INT(11) DEFAULT '0'")
    private int minAmount;

    @Column(nullable = false, columnDefinition = "TIME DEFAULT '11:00:00'")
    private String openTime;

    @Column(nullable = false, columnDefinition = "TIME DEFAULT '23:00:00'")
    private String closeTime;

    @Column(columnDefinition = "TEXT")
    private String eventComment;

    @Column(columnDefinition = "VARCHAR(2)")
    private EnumDayOfWeek closedDay;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    private Integer isOpen;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    private Integer isPickUp;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    private Integer isActive;
}
