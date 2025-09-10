package kr.co.hanipactor.application.menu;

import kr.co.hanipactor.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStore_Id(long storeId);
}
