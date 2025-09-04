package kr.co.hanipactor.application.user;

import jakarta.transaction.Transactional;
import kr.co.hanipactor.application.store.StoreMapper;
import kr.co.hanipactor.application.store.StoreRepository;
import kr.co.hanipactor.application.storecategory.StoreCategoryRepository;
import kr.co.hanipactor.application.user.model.*;
import kr.co.hanipactor.application.useraddress.UserAddressRepository;
import kr.co.hanipactor.application.useraddress.model.UserAddressPostReq;
import kr.co.hanipactor.configuration.utils.ImgUploadManager;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.configuration.security.model.SignInProviderType;
import kr.co.hanipactor.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디/비밀번호를 확인해 주세요.");
        }
        Integer storeId = storeMapper.findStoreIdByUserId(user.getId());

        EnumUserRole role = user.getRole();

        JwtUser jwtUser =  new JwtUser(user.getId(), user.getRole());

        UserLoginRes userLoginRes = UserLoginRes.builder()
                .id(user.getId())
                .storeId(user.getRole() == EnumUserRole.CUSTOMER ? 0 : storeId)
                .role(role)
                .loginPw(user.getLoginPw())
                .build();

        return UserLoginDto.builder()
                .jwtUser(jwtUser)
                .userLoginRes(userLoginRes)
                .build();
    }

    // 유저 전체 조회
    public Page<UserAllGetRes> allUser(UserAllGetReq req) {
        // 검색 조건 적용
        Specification<User> spec = UserSpecification.hasStartDate(req.getStartDate())
                                                    .and(UserSpecification.hasEndDate(req.getEndDate()))
                                                    .and(UserSpecification.hasLoginId(req.getLoginId()))
                                                    .and(UserSpecification.hasName(req.getName()))
                                                    .and(UserSpecification.hasAddress(req.getAddress()))
                                                    .and(UserSpecification.hasPhone(req.getPhone()))
                                                    .and(UserSpecification.hasEmail(req.getEmail()))
                                                    .and(UserSpecification.hasProviderType(req.getProviderType()))
                                                    .and(UserSpecification.hasRole(req.getRole()));

        // 페이징 및 페이지 사이즈 적용
        Pageable pageable = PageRequest.of(0, 10);

        // 검색 조건에 맞는 유저 데이터를 가져온 뒤,
        // 필요한 컬럼을 멤버 필드로 가진 객체 타입의 Page 변수를 선언하여 리턴함
        // Page 타입은 Spring Data JPA에서 제공하는 인터페이스라고 함
        // List 타입과 비슷하나 페이징 관련 정보가 포함되어 있음
        Page<User> page = userRepository.findAll(spec, pageable);
        Page<UserAllGetRes> result = page.map(u -> {
            // 이미 검색 조건을 리턴하는 데서 기본 주소인 것을 조건으로 조인을 했으나,
            // @OneToMany(fetch = FetchType.LAZY) 면, getAddresses()를 호출할 때 DB에서 다시 조회된다고 함
            // 그래서 기본 주소인 데이터 중 가장 첫 번째를 mainAddress 라는 변수에 담음
            UserAddress mainAddress = u.getAddresses().stream()
                                                      .filter(address -> address.getIsMain() == 1)
                                                      .findFirst()
                                                      .orElse(null);

            return UserAllGetRes.builder()
                    .userId(u.getId())
                    .name(u.getName())
                    .loginId(u.getLoginId())
                    .postcode(mainAddress != null ? mainAddress.getPostcode() : null)
                    .address(mainAddress != null ? mainAddress.getAddress() : null)
                    .addressDetail(mainAddress != null ? mainAddress.getAddressDetail() : null)
                    .phone(u.getPhone())
                    .email(u.getEmail())
                    .providerType(u.getProviderType().getCode())
                    .role(u.getRole().getCode())
                    .createdAt(u.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            }
        );

        return result;
    }
}
