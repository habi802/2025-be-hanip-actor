package kr.co.hanipactor.application.store;

import kr.co.hanipactor.application.store.model.StoreGetListReq;
import kr.co.hanipactor.application.store.model.StoreGetListRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreMapper {
    // int save(StorePostReq req); // 가게등록
    Integer findStoreIdByUserId(long userId);
    List<StoreGetListRes> findAllStore(@Param("req") StoreGetListReq req,
                                       @Param("categoryCode") String categoryCode); // 가게전체조회 & 검색
    Integer updateStoreByUserId();
}
