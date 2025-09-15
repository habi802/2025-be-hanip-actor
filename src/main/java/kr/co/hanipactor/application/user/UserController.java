package kr.co.hanipactor.application.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.application.user.model.*;
import kr.co.hanipactor.configuration.jwt.JwtTokenManager;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.configuration.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;

    // 유저 회원가입
    @PostMapping("/join")
    public ResponseEntity<ResultResponse<Integer>> join(@RequestPart UserJoinReq req,
                                                        @RequestPart(required = false) MultipartFile pic) {
        log.info("req: {}", req);
        Integer result = userService.join(req, pic);

        if (result == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResultResponse.fail(400, "이미 등록된 아이디입니다."));
        }

        return result == 0
                ? ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.fail(400, "등록 실패"))
                : ResponseEntity.ok(ResultResponse.success(result));
    }

    // 유저 로그인
    @PostMapping("/login")
    public ResponseEntity<ResultResponse<UserLoginRes>> login(@RequestBody UserLoginReq req, HttpServletResponse response) {
        UserLoginDto userLoginDto = userService.login(req);
        jwtTokenManager.issue(response, userLoginDto.getJwtUser());
        log.info("userLoginDto: {}", userLoginDto.getJwtUser());
        if (userLoginDto == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResultResponse.fail(401, "아이디나 비밀번호가 올바르지 않습니다."));
        }
        return ResponseEntity.ok(ResultResponse.success(userLoginDto.getUserLoginRes()));
    }

    // 유저 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResultResponse<?>> logout(HttpServletResponse response) {
        jwtTokenManager.signOut(response);
        return ResponseEntity.ok(ResultResponse.success("로그아웃 성공"));
    }

    // 토큰 재발행
    @PostMapping("/reissue")
    public ResponseEntity<ResultResponse<?>> reissue(HttpServletResponse response, HttpServletRequest request) {
        jwtTokenManager.reissue(request, response);
        return ResponseEntity.ok(ResultResponse.success("토큰 재발급 성공"));
    }

    // 유저 비밀번호 체크
    @PostMapping("/check-password")
    public ResponseEntity<ResultResponse<Integer>> checkPassword(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                 @RequestBody String password) {
        Long signedUserId = userPrincipal.getSignedUserId();
        Integer result = userService.checkPassword(signedUserId, password);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "비밀번호 일치 확인", result)
        );
    }

    // 유저 주소 등록
    @PostMapping("/adds")
    public ResponseEntity<ResultResponse<?>> saveUserAddress(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @RequestPart UserAddressPostReq req) {
        Long signedUserId = userPrincipal.getSignedUserId();
        Integer result = userService.saveUserAdds(signedUserId, req);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "유저 주소 등록 성공", result)
        );

    }

    // 유저 리스트 조회 (서버 api)
    @GetMapping("/search")
    public ResponseEntity<?> getUserList(@RequestParam(name = "user_id")List<Long> userIdList) {
        log.info("userId: {}", userIdList);
        Map<Long, UserGetItem> result = userService.getUserList(userIdList);
        return ResponseEntity.ok(
                new ResultResponse<>(200, String.format("rows: %d", result.size()), result)
        );
    }

    //  유저 정보 조회
    @GetMapping
    public ResponseEntity<ResultResponse<UserGetRes>> getUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long signedUserId = userPrincipal.getSignedUserId();
        UserGetRes result = userService.getUser(signedUserId);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "유저 정보 조회 성공", result)
        );
    }

    // 유저 주소 조회
    @GetMapping("/adds")
    public ResponseEntity<ResultResponse<List<UserAddressGetRes>>> getUserAddress(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long signedUserId = userPrincipal.getSignedUserId();
        List<UserAddressGetRes> result = userService.getUserAddress(signedUserId);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "유저 주소 조회 성공", result)
        );
    }

    // 유저 정보 수정
    @PutMapping
    public ResponseEntity<ResultResponse<Integer>> updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @RequestPart UserPutReq req,
                                                        @RequestPart(required = false) MultipartFile pic) {
        Long signedUserId = userPrincipal.getSignedUserId();
        Integer result = userService.updateUser(signedUserId, req, pic);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "유저 정보 수정 성공", result)
        );
    }

    // 유저 주소 수정
    @PutMapping("/address")
    public ResponseEntity<ResultResponse<Integer>> updateUserAddress(@RequestBody UserAddressPutReq req) {
        Integer result = userService.updateUserAdds(req);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "유저 주소 수정 성공", result)
        );
    }

    // 유저 메인 주소 변경
    @PatchMapping("/adds/main/{address_id}")
    public ResponseEntity<ResultResponse<Integer>> patchUserAddress(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                    @PathVariable(value = "address_id") Long addressId) {
        Long signedUserId = userPrincipal.getSignedUserId();
        Integer result = userService.patchUserAddress(addressId, signedUserId);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "유저 주소 메인 변경 성공", result)
        );
    }

    // 유저 주소 삭제
    @DeleteMapping("/adds/{address_id}")
    public ResponseEntity<ResultResponse<Integer>> deleteUserAddress(@PathVariable(value = "address_id") Long addressId) {
        Integer result = userService.deleteUserAddress(addressId);
        return ResponseEntity.ok(
                new ResultResponse<>(200, "유저 주소 삭제 성공", result)
        );
    }
}
