package kr.co.hanipactor.application.user;

import kr.co.hanipactor.application.user.model.UserJoinReq;
import kr.co.hanipactor.application.user.model.UserLoginReq;
import kr.co.hanipactor.application.user.model.UserLoginRes;
import kr.co.hanipactor.application.user.model.UserPostReq;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int save(UserJoinReq req);
    Integer findIdByLoginIdAndRole(UserPostReq req);
    UserLoginRes findByLoginId(UserLoginReq req);
}
