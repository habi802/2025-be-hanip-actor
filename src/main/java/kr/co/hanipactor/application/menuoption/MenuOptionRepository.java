package kr.co.hanipactor.application.menuoption;

import kr.co.hanipactor.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
    List<MenuOption> findByMenu_Id(Long menuId);
}
