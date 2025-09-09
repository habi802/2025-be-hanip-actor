package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumMenuType;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menus")
public class Menu extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    @Comment("가게 아이디(stores)")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    @Comment("사장 아이디(users)")
    private User user;

    @Column(nullable = false, length = 50)
    @Comment("이름")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Comment("메뉴 소개")
    private String comment;

    @Column(nullable = false)
    @Comment("가격")
    private int price;

    @Column(length = 200)
    @Comment("메뉴 이미지")
    private String imagePath;

    @Column(nullable = false, columnDefinition = "VARCHAR(2)")
    @Comment("메뉴 종류(01: 단품, 02: 세트, 03: 사이드, 04: 음료)")
    private EnumMenuType menuType;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    @Comment("품절 여부(0: 품절, 1: 판매중)")
    private Integer isSoldOut;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    @Comment("숨김 여부(0: 숨김, 1: 공개)")
    private Integer isHide;

    @PrePersist
    public void prePersist() {
        if (this.isSoldOut == null) {
            this.isSoldOut = 0;
        }
        if (this.isHide == null) {
            this.isHide = 0;
        }
    }
}
