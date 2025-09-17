package kr.co.hanipactor.application.manager;

import kr.co.hanipactor.application.manager.model.*;
import kr.co.hanipactor.application.manager.specification.StoreSpecification;
import kr.co.hanipactor.application.manager.specification.UserSpecification;
import kr.co.hanipactor.application.store.StoreMapper;
import kr.co.hanipactor.application.store.StoreRepository;
import kr.co.hanipactor.application.storecategory.StoreCategoryRepository;
import kr.co.hanipactor.application.user.UserMapper;
import kr.co.hanipactor.application.user.UserRepository;
import kr.co.hanipactor.application.user.model.UserLoginDto;
import kr.co.hanipactor.application.user.model.UserLoginReq;
import kr.co.hanipactor.application.user.model.UserLoginRes;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.JwtUser;
import kr.co.hanipactor.entity.Store;
import kr.co.hanipactor.entity.StoreCategory;
import kr.co.hanipactor.entity.User;
import kr.co.hanipactor.entity.UserAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final StoreCategoryRepository storeCategoryRepository;

    // 관리자 로그인
    public UserLoginDto login(UserLoginReq req) {
        User user = userRepository.findByLoginId(req.getLoginId());

        // 비밀번호 일치 확인
        if (user == null || !BCrypt.checkpw(req.getLoginPw(), user.getLoginPw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일치하는 계정이 없습니다.");
        }

        // 관리자 권한 확인
        EnumUserRole role = user.getRole();
        if (role == null || !"MANAGER".equals(role.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일치하는 계정이 없습니다.");
        }

        JwtUser jwtUser = new JwtUser(user.getId(), user.getRole());

        UserLoginRes userLoginRes = UserLoginRes.builder()
                .id(user.getId())
                .role(role)
                .loginPw(user.getLoginPw())
                .build();

        return UserLoginDto.builder()
                .jwtUser(jwtUser)
                .userLoginRes(userLoginRes)
                .build();
    }

    // 유저 전체 조회
    public PageResponse<UserListRes> getUserList(UserListReq req) {
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
        Pageable pageable = req.getPageSize() == -1 ? Pageable.unpaged() : PageRequest.of(req.getPageNumber(), req.getPageSize());

        // 검색 조건에 맞는 유저 데이터를 가져온 뒤,
        // 필요한 컬럼을 멤버 필드로 가진 객체 타입의 Page 변수를 선언하여 리턴함
        // Page 타입은 Spring Data JPA에서 제공하는 인터페이스라고 함
        // List 타입과 비슷하나 페이징 관련 정보가 포함되어 있음
        Page<User> page = userRepository.findAll(spec, pageable);
        List<UserListRes> result = page.stream().map(user -> {
                // 이미 검색 조건을 리턴하는 데서 기본 주소인 것을 조건으로 조인을 했으나,
                // @OneToMany(fetch = FetchType.LAZY) 면, getAddresses()를 호출할 때 DB에서 다시 조회된다고 함
                // 그래서 기본 주소인 데이터 중 가장 첫 번째를 mainAddress 라는 변수에 담음
                UserAddress mainAddress = user.getAddresses().stream()
                                                             .filter(address -> address.getIsMain() == 1)
                                                             .findFirst()
                                                             .orElse(null);

                return UserListRes.builder()
                                  .userId(user.getId())
                                  .name(user.getName())
                                  .loginId(user.getLoginId())
                                  .address(mainAddress != null ? String.format("%s, %s, %s", mainAddress.getPostcode(), mainAddress.getAddress(), mainAddress.getAddressDetail()) : null)
                                  .phone(user.getPhone())
                                  .email(user.getEmail())
                                  .providerType(user.getProviderType().getCode())
                                  .role(user.getRole().getCode())
                                  .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                  .build();
            }
        ).toList();

        return new PageResponse<>(result, page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber() + 1);
    }

    // 유저 상세 조회(Action 호출용)
    public String getUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        return user.getName();
    }

    // 가게 전체 조회
    public PageResponse<StoreListRes> getStoreList(StoreListReq req) {
        // 검색 조건 적용
        Specification<Store> spec = StoreSpecification.hasStartDate(req.getStartDate())
                                                      .and(StoreSpecification.hasEndDate(req.getEndDate()))
                                                      .and(StoreSpecification.hasStartOpenDate(req.getStartOpenDate()))
                                                      .and(StoreSpecification.hasEndOpenDate(req.getEndOpenDate()))
                                                      .and(StoreSpecification.hasName(req.getName()))
                                                      .and(StoreSpecification.hasOwnerName(req.getOwnerName()))
                                                      .and(StoreSpecification.hasBusinessNumber(req.getBusinessNumber()))
                                                      .and(StoreSpecification.hasCategory(req.getCategory()))
                                                      .and(StoreSpecification.hasAddress(req.getAddress()))
                                                      .and(StoreSpecification.hasTel(req.getTel()))
                                                      .and(StoreSpecification.hasIsActive(req.getIsActive()));

        // 페이징 및 페이지 사이즈 적용
        Pageable pageable = req.getPageSize() == -1 ? Pageable.unpaged() : PageRequest.of(req.getPageNumber(), req.getPageSize());

        Page<Store> page = storeRepository.findAll(spec, pageable);
        List<StoreListRes> result = page.stream().map(store -> {
                List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(store.getId());
                List<String> categories = new ArrayList<>();
                for(StoreCategory storeCategory : storeCategories) {
                    categories.add(storeCategory.getStoreCategoryId().getCategory().getValue());
                }

                return StoreListRes.builder()
                                   .storeId(store.getId())
                                   .openDate(store.getOpenDate())
                                   .name(store.getName())
                                   .ownerName(store.getOwnerName())
                                   .businessNumber(store.getBusinessNumber())
                                   .categories(categories)
                                   .address(String.format("%s, %s, %s", store.getPostcode(), store.getAddress(), store.getAddressDetail()))
                                   .tel(store.getTel())
                                   .isActive(store.getIsActive())
                                   .createdAt(store.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                   .build();
            }
        ).toList();

        return new PageResponse<>(result, page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber() + 1);
    }

    // 가게 상세 조회
    public StoreInManagerRes getStore(Long storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);

        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(storeId);
        List<String> categories = new ArrayList<>();
        for(StoreCategory storeCategory : storeCategories) {
            categories.add(storeCategory.getStoreCategoryId().getCategory().getValue());
        }

        // 와우..
        return StoreInManagerRes.builder()
                                .storeId(storeId)
                                .name(store.getName())
                                .imagePath(store.getImagePath())
                                .categories(categories)
                                .businessNumber(store.getBusinessNumber())
                                .licensePath(store.getLicensePath())
                                .ownerName(store.getOwnerName())
                                .openDate(store.getOpenDate())
                                .address(String.format("%s, %s, %s", store.getPostcode(), store.getAddress(), store.getAddressDetail()))
                                .isActive(store.getIsActive())
                                .comment(store.getComment())
                                .eventComment(store.getEventComment())
                                .tel(store.getTel())
                                .openTime(store.getOpenTime())
                                .closeTime(store.getCloseTime())
                                .closedDay(store.getClosedDay() != null ? store.getClosedDay().getValue() : "-")
                                .isOpen(store.getIsOpen())
                                .isPickUp(store.getIsPickUp())
                                .minDeliveryFee(store.getMinDeliveryFee())
                                .maxDeliveryFee(store.getMaxDeliveryFee())
                                .minAmount(store.getMinAmount())
                                .rating(store.getRating())
                                .favorites(store.getFavorites())
                                .build();
    }

    // 가게 영업 승인 상태 변경
    @Transactional
    public void patchIsActiveInStore(List<Long> ids, int isActive) {
        List<Store> stores = storeRepository.findAllById(ids);

        for (Store store : stores) {
            store.setIsActive(isActive);
        }
    }

    // 3개의 기간을 리스트에 넣는 메소드
    public List<String> addPeriodList(String type, String date) {
        List<String> periods = new ArrayList<>(3);
        switch (type) {
            case "YEAR":
                int year = Integer.parseInt(date);
                for (int i = 2; i >= 0; i--) {
                    periods.add(String.valueOf(year - i));
                }
                break;
            case "MONTH":
                LocalDate month = LocalDate.parse(date + "-01");
                for (int i = 2; i >= 0; i--) {
                    periods.add(month.minusMonths(i).toString());
                }
                break;
            case "WEEK":
                LocalDate week = LocalDate.parse(date);
                for (int i = 2; i >= 0; i--) {
                    periods.add(week.minusWeeks(i).toString());
                }
                break;
            case "DAY":
                LocalDate day = LocalDate.parse(date);
                for (int i = 2; i >= 0; i--) {
                    periods.add(day.minusDays(i).toString());
                }
                break;
            default:
                throw new IllegalArgumentException("잘못된 날짜 타입을 입력하였습니다.");
        }

        return periods;
    }

    // 가입자 수 통계
    public List<UserStatsRes> getUserStats(UserStatsReq req) {
        String type = req.getType().toUpperCase();

        // 3개의 기간이 들어가는 리스트를 만들고, 그 리스트에 선택한 날짜의 이전 2개 항목, 마지막으로 선택한 날짜를 넣는 과정(예를 들어, ?type=year&date=2025 이면 2023, 2024, 2025)
        List<String> periods = addPeriodList(type, req.getDate());

        UserStatsDto dto = UserStatsDto.builder()
                                       .type(type)
                                       .periods(periods)
                                       .build();

        return userMapper.findStatsByDate(dto);
    }

    // 가게 등록 수 통계
    public List<StoreStatsRes> getStoreStats(StoreStatsReq req) {
        String type = req.getType().toUpperCase();

        List<String> periods = addPeriodList(type, req.getDate());

        StoreStatsDto dto = StoreStatsDto.builder()
                                         .type(type)
                                         .periods(periods)
                                         .build();

        return storeMapper.findStatsByDate(dto);
    }
}
