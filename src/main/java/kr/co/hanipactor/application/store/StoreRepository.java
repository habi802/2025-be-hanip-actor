package kr.co.hanipactor.application.store;

import kr.co.hanipactor.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {
    Optional<Store> findByUserId(Long userId);
    List<Store> findTop3ByOrderByIsActiveAscCreatedAtDesc();
}
