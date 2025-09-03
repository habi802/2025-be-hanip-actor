package kr.co.hanipactor.application.useraddress;

import kr.co.hanipactor.application.useraddress.model.UserAddressPostReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAddressMapper {
    Integer save(UserAddressPostReq req);
}
