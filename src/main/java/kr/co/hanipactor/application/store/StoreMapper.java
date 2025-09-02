package kr.co.hanipactor.application.store;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreMapper {
    // int save(StorePostReq req); // 가게등록
    Integer findStoreIdByUserId(int userId);
}
