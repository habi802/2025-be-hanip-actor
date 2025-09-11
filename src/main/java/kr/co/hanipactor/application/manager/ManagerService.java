package kr.co.hanipactor.application.manager;

import kr.co.hanipactor.application.manager.specification.UserSpecification;
import kr.co.hanipactor.application.manager.model.UserAllGetReq;
import kr.co.hanipactor.application.manager.model.UserAllGetRes;
import kr.co.hanipactor.application.user.UserRepository;
import kr.co.hanipactor.entity.User;
import kr.co.hanipactor.entity.UserAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService {
    private final UserRepository userRepository;

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
        Pageable pageable = PageRequest.of(req.getPageNumber(), req.getPageSize());

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
