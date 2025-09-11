package kr.co.hanipactor.application.storecategory;

import kr.co.hanipactor.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
    List<StoreCategory> findByStoreId(Long storeId);
}
