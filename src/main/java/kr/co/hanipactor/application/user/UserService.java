package kr.co.hanipactor.application.user;

import jakarta.transaction.Transactional;
import kr.co.hanipactor.application.store.StoreMapper;
import kr.co.hanipactor.application.store.model.StoreRepository;
import kr.co.hanipactor.application.storecategory.StoreCategoryRepository;
import kr.co.hanipactor.application.user.model.*;
import kr.co.hanipactor.application.useraddress.UserAddressRepository;
import kr.co.hanipactor.application.useraddress.model.UserAddressPostReq;
import kr.co.hanipactor.configuration.common.util.ImgUploadManager;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ImgUploadManager imgUploadManager;

    @Transactional
    public Integer join(UserJoinReq req, MultipartFile pic) {
        int result = 0;

        // 1. 중복 체크
        UserPostReq checkIdReq = UserPostReq.builder()
                .loginId(req.getLoginId())
                .role(req.getRole())
                .build();

        Integer checkUserId = userMapper.findIdByLoginIdAndRole(checkIdReq);
        if (checkUserId != null && checkUserId > 0) {
            return null;
        }

        // 2. 비밀번호 암호화
        String hashedPw = BCrypt.hashpw(req.getLoginPw(), BCrypt.gensalt());

        // 3. 유저 엔터티 생성 및 저장
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
        if (savedUser != null) {
            result += 1;

            switch (req.getRole()) {
                // 사장일 경우 : 사장 가게 주소 저장 ( 단일 )
                case OWNER -> {
                    String savedFileName = null;
                    if(pic != null) {
                        savedFileName = imgUploadManager.saveProfilePic(user.getId(), pic);
                    }
                    Store store = Store.builder()
                            .user(savedUser)
                            .name(req.getStoreJoinReq().getName())
                            .comment(req.getStoreJoinReq().getComment())
                            .businessNumber(req.getStoreJoinReq().getBusinessNumber())
                            .licensePath(savedFileName)
                            .postcode(req.getStoreJoinReq().getPostcode())
                            .address(req.getStoreJoinReq().getAddress())
                            .addressDetail(req.getStoreJoinReq().getAddressDetail())
                            .tel(req.getStoreJoinReq().getTel())
                            .ownerName(req.getStoreJoinReq().getOwnerName())
                            .openDate(req.getStoreJoinReq().getOpenDate())
                            .closeTime("23:00:00")

                            .build();

                    storeRepository.save(store);

                    for (int i = 0; i < req.getStoreJoinReq().getEnumStoreCategory().size(); i++) {
                        StoreCategoryIds storeCategoryIds = new StoreCategoryIds();
                        storeCategoryIds.setStoreId(store.getId());
                        storeCategoryIds.setCategory(req.getStoreJoinReq().getEnumStoreCategory().get(i));

                        StoreCategory storeCategory = StoreCategory.builder()
                                .storeCategoryId(storeCategoryIds)
                                .store(store)
                                .build();
                        storeCategoryRepository.save(storeCategory);
                    }
                    result += 1;
                }

                // 고객일 경우 : 고객 주소 저장 ( 리스트 )
                case CUSTOMER -> {
                    for (UserAddressPostReq addReq : req.getUserAddressPostReq()) {
                        UserAddress userAddress = UserAddress.builder()
                                .user(savedUser)
                                .title(addReq.getTitle())
                                .postcode(addReq.getPostcode())
                                .address(addReq.getAddress())
                                .addressDetail(addReq.getAddressDetail())
                                .build();

                        userAddressRepository.save(userAddress);
                        result += 1;
                    }
                }

            }
        }
        return result;
    }

    public UserLoginDto login(UserLoginReq req) {
        User user = userRepository.findByLoginId(req.getLoginId());

        // 비밀번호 일치 확인
        if (user == null || !BCrypt.checkpw(req.getLoginPw(), user.getLoginPw())) {
            return null;
        }
        // Integer storeId = storeMapper.findStoreIdByUserId(res.getId());
        // (storeId == null ? 0 : storeId);

        EnumUserRole role = user.getRole();

        JwtUser jwtUser =  new JwtUser(user.getId(), user.getRole());

        UserLoginRes userLoginRes = UserLoginRes.builder()
                .id(user.getId())
                .storeId(0)
                .role(role)
                .loginPw(user.getLoginPw())
                .build();

        return UserLoginDto.builder()
                .jwtUser(jwtUser)
                .userLoginRes(userLoginRes)
                .build();
    }
}
