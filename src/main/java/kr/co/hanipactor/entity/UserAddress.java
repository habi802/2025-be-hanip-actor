package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserAddress extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    @Comment("고객 아이디(users)")
    private User user;

    @Column(nullable = false, length = 50)
    @Comment("장소 이름(예: 우리집, 회사)")
    private String title;

    @Column(nullable = false, length = 12)
    @Comment("우편 번호")
    private String postcode;

    @Column(nullable = false, length = 100)
    @Comment("주소")
    private String address;

    @Column(length = 100)
    @Comment("상세 주소")
    private String addressDetail;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    @Comment("기본 주소 여부(0: 기본 주소 아님, 1: 기본 주소)")
    private Integer isMain;

    @PrePersist
    public void prePersist() {
        if (this.isMain == null) {
            this.isMain = 0;
        }
    }
}
