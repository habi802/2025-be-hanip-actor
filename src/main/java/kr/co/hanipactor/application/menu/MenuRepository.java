package kr.co.hanipactor.application.menu;

import kr.co.hanipactor.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
