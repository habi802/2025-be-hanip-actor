package kr.co.hanipactor.application.user;

import jakarta.transaction.Transactional;
import kr.co.hanipactor.application.store.StoreMapper;
import kr.co.hanipactor.application.store.StoreRepository;
import kr.co.hanipactor.application.storecategory.StoreCategoryRepository;
import kr.co.hanipactor.application.user.model.*;
import kr.co.hanipactor.application.useraddress.UserAddressRepository;
import kr.co.hanipactor.configuration.utils.ImgUploadManager;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final StoreMapper storeMapper;
    private final ImgUploadManager imgUploadManager;

    // 유저 회원가입
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
                // 사장일 경우 : 사장 가게 주소 및 정보 저장 ( 단일 )
                case OWNER -> {
                    String savedFileName = null;
                    if(pic != null) {
                        savedFileName = imgUploadManager.saveStorePic(user.getId(), pic);
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
                                .isMain(1)
                                .build();

                        userAddressRepository.save(userAddress);
                        result += 1;
                    }
                }

            }
        }
        return result;
    }

    // 유저 로그인
    public UserLoginDto login(UserLoginReq req) {
        User user = userRepository.findByLoginId(req.getLoginId());

        // 비밀번호 일치 확인
        if (user == null || !BCrypt.checkpw(req.getLoginPw(), user.getLoginPw())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디/비밀번호를 확인해 주세요.");
        }

        // 유저 롤 확인
        if (!user.getRole().equals(req.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role을 확인해주세요.");
        }

        Integer storeId = 0;
        if (user.getRole() == EnumUserRole.OWNER) {
            storeId = storeMapper.findStoreIdByUserId(user.getId());
        }

        JwtUser jwtUser =  new JwtUser(user.getId(), user.getRole());

        UserLoginRes userLoginRes = UserLoginRes.builder()
                .id(user.getId())
                .storeId(storeId)
                .role(user.getRole())
                .build();

        return UserLoginDto.builder()
                .jwtUser(jwtUser)
                .userLoginRes(userLoginRes)
                .build();
    }

    // 유조 주소 등록
    public Integer saveUserAdds(Long signedUserId, UserAddressPostReq req) {
        User user = userRepository.findById(signedUserId).orElseThrow(
                () -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        userAddressRepository.save(UserAddress.builder()
                .user(user)
                .title(req.getTitle())
                .isMain(0)
                .postcode(req.getPostcode())
                .address(req.getAddress())
                .addressDetail(req.getAddressDetail())
                .build());
        return 1;
    }

    // 유저 비밀번호 체크
    public Integer checkPassword(Long signedUserId, String password) {
        User user = userRepository.findById(signedUserId).orElseThrow(
                () -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        if (!BCrypt.checkpw(password, user.getLoginPw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디/비밀번호를 확인해 주세요.");
        }
        return 1;
    }

    // 유저 리스트 조회
    public Map<Long, UserGetItem> getUserList(List<Long> userIdList) {
        List<User> userList = userRepository.findAllById(userIdList);

        return userList.stream().collect(
                Collectors.toMap( item -> item.getId(),
                        item -> UserGetItem.builder()
                                .id(item.getId())
                                .userNickName(item.getName())
                                .userPic(item.getImagePath())
                                .userTel(item.getPhone())
                                .build()
                )
        );
    }

    // 유저 정보 조회
    public UserGetRes getUser(Long signedUserId) {
        User user = userRepository.findById(signedUserId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        return UserGetRes.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .imagePath(user.getImagePath())
                .role(user.getRole())
                .created(user.getCreatedAt())
                .build();
    }

    // 유저 주소 조회
    public List<UserAddressGetRes> getUserAddress(Long signedUserId) {
        List<UserAddress> userAddress = userAddressRepository.findAllByUserId(signedUserId);

        return userAddress.stream()
                .map(UserAddressGetRes::from)
                .toList();
    }

    // 유저 정보 수정
    @Transactional
    public Integer updateUser(Long signedUserId, UserPutReq req, MultipartFile pic) {
        User user = userRepository.findById(signedUserId).orElseThrow(
                () -> new RuntimeException("정보를 수정할 권한이 없습니다."));

        // 1) 비밀번호 재확인
        if (req.getLoginPw() == null || req.getLoginPw().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 필요합니다.");
        }
        if (!BCrypt.checkpw(req.getLoginPw(), user.getLoginPw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디/비밀번호를 확인해 주세요.");
        }

        // 2) 필드 부분 업데이트
        if (req.getName() != null && !req.getName().isBlank()) {
            user.setName(req.getName());
        }
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            user.setPhone(req.getPhone());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            user.setEmail(req.getEmail());
        }

        // 3) 비밀번호 변경 (새 비번이 있을 때만)
        if (req.getNewLoginPw() != null && !req.getNewLoginPw().isBlank()) {
            String hashedPw = BCrypt.hashpw(req.getNewLoginPw(), BCrypt.gensalt());
            user.setLoginPw(hashedPw);
        }

        // 4) 프로필 이미지 (비번 검증 후에 저장)
        if (pic != null && !pic.isEmpty()) {
            String savedFileName = imgUploadManager.saveUserProfilePic(user.getId(), pic);
            user.setImagePath(savedFileName);
        }

        userRepository.save(user);
        return 1;
    }

    // 유저 주소 수정
    @Transactional
    public Integer updateUserAdds(UserAddressPutReq req) {
        UserAddress userAddress = userAddressRepository.findById(req.getId()).orElseThrow();

        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            userAddress.setTitle(req.getTitle());
        }
        if (req.getPostcode() != null && !req.getPostcode().isBlank()) {
            userAddress.setPostcode(req.getPostcode());
        }
        if (req.getAddress() != null && !req.getAddress().isBlank()) {
            userAddress.setAddress(req.getAddress());
        }
        if (req.getAddressDetail() != null && !req.getAddressDetail().isBlank()) {
            userAddress.setAddressDetail(req.getAddressDetail());
        }
        userAddressRepository.save(userAddress);
        return 1;
    }

    // 유저 메인 주소 변경
    public Integer patchUserAddress(Long signedUserId, Long addressId) {
        List<UserAddress> addresses = userAddressRepository.findAllByUserId(signedUserId);

        if (addresses.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "등록된 주소가 없습니다.");
        }

        boolean found = false;
        for (UserAddress address : addresses) {
            if (address.getId() == addressId) {
                address.setIsMain(1);  // 이놈만 기본주소
                found = true;
            } else {
                address.setIsMain(0);  // 나머지는 전부 기본 아님
            }
        }

        if (!found) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 유저의 주소가 아닙니다.");
        }
        return 1;
    }

    // 유저 주소 삭제
    public Integer deleteUserAddress(Long addressId) {
        UserAddress userAddress = userAddressRepository.findById(addressId).orElseThrow();
        userAddressRepository.delete(userAddress);
        return 1;
    }
}
