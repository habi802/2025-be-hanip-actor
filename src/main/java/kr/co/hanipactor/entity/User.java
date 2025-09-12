package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    @Comment("이름")
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    @Comment("로그인 아이디")
    private String loginId;

    @Column(nullable = false, length = 200)
    @Comment("로그인 비밀번호")
    private String loginPw;

    @Column(length = 13)
    @Comment("전화번호")
    private String phone;

    @Column(length = 50)
    @Comment("이메일")
    private String email;

    @Column(length = 200)
    @Comment("프로필 이미지")
    private String imagePath;

    @Column(nullable = false, length = 2, columnDefinition = "VARCHAR(2)")
    @Comment("회원 분류(01: 고객, 02: 사장, 03: 배달원, 04: 관리자)")
    private EnumUserRole role;

    @Column(nullable = false, length = 2, columnDefinition = "VARCHAR(2) DEFAULT '01'")
    @Comment("가입 유형(01: 일반, 02: 카카오, 03: 네이버)")
    private SignInProviderType providerType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAddress> addresses = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.providerType == null) {
            this.providerType = SignInProviderType.LOCAL;
        }
    }
}
