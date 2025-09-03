package kr.co.hanipactor.application.user;

import kr.co.hanipactor.application.store.StoreMapper;
import kr.co.hanipactor.application.user.model.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
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

        User user = User.builder()
                .providerType(SignInProviderType.LOCAL)
                .name(req.getName())
                .loginId(req.getLoginId())
                .loginPw(hashedPw)
                .phone(req.getPhone())
                .email(req.getEmail())
                .imagePath(req.getImagePath())
                .role(req.getRole())
                .build();

        //log.info("user joinReq:{}", joinReq);
        User savedUser = userRepository.save(user);
        result += (savedUser != null) ? 1 : 0;
        return result;
    }

    public UserLoginDto login(UserLoginReq req) {
        User user = userRepository.findByLoginId(req.getLoginId());
        UserLoginRes res = userMapper.findByLoginId(req);

        // 비밀번호 일치 확인
        if (user == null || !BCrypt.checkpw(req.getLoginPw(), user.getLoginPw())) {
            return null;
        }
        // Integer storeId = storeMapper.findStoreIdByUserId(res.getId());
        res.setStoreId(0); // (storeId == null ? 0 : storeId);

        EnumUserRole role = user.getRole();
        res.setRole(role);

        JwtUser jwtUser =  new JwtUser(res.getId(), res.getRole());

        return UserLoginDto.builder()
                .jwtUser(jwtUser)
                .userLoginRes(res)
                .build();
    }
}
