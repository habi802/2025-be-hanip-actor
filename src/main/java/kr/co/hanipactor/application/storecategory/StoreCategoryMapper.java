package kr.co.hanipactor.application.storecategory;

import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StoreCategoryMapper {
    List<String> findByStoreId(Long storeId);
}
