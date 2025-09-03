package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MenuOption extends UpdatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "menu_id")
    @Comment("메뉴 아이디(menus)")
    private Menu menu;

    // 자기 자신을 참조(self reference)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Comment("상위 옵션 아이디")
    private MenuOption parentId;
    @OneToMany(mappedBy = "parentId")
    private List<MenuOption> children = new ArrayList<>();

    @Column(nullable = false, length = 50)
    @Comment("옵션 설명")
    private String comment;

    @Column
    @Comment("옵션 가격")
    private int price;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT '0'")
    @Comment("필수 선택 여부(0: 선택, 1: 필수 선택)")
    private Integer isRequried;
}
