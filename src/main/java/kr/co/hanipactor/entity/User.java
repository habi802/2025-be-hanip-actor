package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false, length = 200)
    private String loginPw;

    @Column(length = 13)
    private String phone;

    @Column(length = 50)
    private String email;

    @Column(length = 200)
    private String imagePath;

    @Column(nullable = false, length = 2, columnDefinition = "VARCHAR(2)")
    private EnumUserRole role;

    @Column(nullable = false, length = 2, columnDefinition = "VARCHAR(2) DEFAULT '01'")
    private SignInProviderType providerType;
}
