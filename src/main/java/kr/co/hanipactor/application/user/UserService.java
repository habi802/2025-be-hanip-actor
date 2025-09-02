package kr.co.hanipactor.application.user;

import kr.co.hanipactor.application.store.StoreMapper;
import kr.co.hanipactor.application.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final StoreMapper storeMapper;

    public Integer join(UserJoinReq req) {
        int result = 0;

        UserPostReq checkIdReq = UserPostReq.builder()
                .loginId(req.getLoginId())
                .role(req.getRole())
                .build();

        if (userMapper.findIdByLoginIdAndRole(checkIdReq) != null && userMapper.findIdByLoginIdAndRole(checkIdReq) > 0) {
            return null;
        }

        String hashedPw = BCrypt.hashpw(req.getLoginPw(), BCrypt.gensalt());

        UserJoinReq joinReq = UserJoinReq.builder()
                .name(req.getName())
                .loginId(req.getLoginId())
                .loginPw(hashedPw)
                .phone(req.getPhone())
                .email(req.getEmail())
                .imagePath(req.getImagePath())
                .role(req.getRole())
                .build();

        //log.info("user joinReq:{}", joinReq);
        result += userMapper.save(joinReq);

        return result;
    }

    public UserLoginDto login(UserLoginReq req) {
        UserLoginRes res = userMapper.findByLoginId(req);

        if (res == null || !BCrypt.checkpw(req.getLoginPw(), res.getLoginPw())) {
            return null;
        }
        Integer storeId = storeMapper.findStoreIdByUserId(res.getId());
        res.setStoreId(storeId == null ? 0 : storeId);
        String role = userMapper.findRoleByUserId(res.getId());
        res.setRole(role);

        return UserLoginDto.builder()
                .userLoginRes(res)
                .build();
    }
}
