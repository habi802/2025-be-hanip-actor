package kr.co.hanipactor.application.storecategory;

import kr.co.hanipactor.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
}
