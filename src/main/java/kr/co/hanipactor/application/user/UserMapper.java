package kr.co.hanipactor.application.user;

import kr.co.hanipactor.application.manager.model.UserStatsDto;
import kr.co.hanipactor.application.manager.model.UserStatsRes;
import kr.co.hanipactor.application.user.model.UserJoinReq;
import kr.co.hanipactor.application.user.model.UserLoginReq;
import kr.co.hanipactor.application.user.model.UserLoginRes;
import kr.co.hanipactor.application.user.model.UserPostReq;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    int save(UserJoinReq req);
    Integer findIdByLoginIdAndRole(UserPostReq req);
    Integer findIdByLoginId(String loginId);
    UserLoginRes findByLoginId(UserLoginReq req);
    List<UserStatsRes> findStatsByDate(UserStatsDto dto); // 가입자 수 통계
}
