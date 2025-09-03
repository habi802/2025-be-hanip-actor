package kr.co.hanipactor.application.store.model;

import kr.co.hanipactor.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
