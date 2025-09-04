package kr.co.hanipactor.application.menuoption;

import kr.co.hanipactor.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
}
