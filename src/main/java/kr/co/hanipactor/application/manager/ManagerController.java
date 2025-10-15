package kr.co.hanipactor.application.manager;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.application.manager.model.*;
import kr.co.hanipactor.application.user.model.UserLoginDto;
import kr.co.hanipactor.application.user.model.UserLoginReq;
import kr.co.hanipactor.application.user.model.UserLoginRes;
import kr.co.hanipactor.configuration.jwt.JwtTokenManager;
import kr.co.hanipactor.configuration.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hanip-manager/actor")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;
    private final JwtTokenManager jwtTokenManager;

    // 관리자 유저 로그인
    @PostMapping("/login")
    public ResponseEntity<ResultResponse<UserLoginRes>> login(@RequestBody UserLoginReq req, HttpServletResponse response) {
        UserLoginDto userLoginDto = managerService.login(req);
        jwtTokenManager.issue(response, userLoginDto.getJwtUser());

        if (userLoginDto == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResultResponse.fail(401, "아이디나 비밀번호가 올바르지 않습니다."));
        }

        return ResponseEntity.ok(ResultResponse.success(userLoginDto.getUserLoginRes()));
    }

    // 유저 전체 조회
    @PostMapping("/user")
    public ResponseEntity<ResultResponse<?>> getUserList(@RequestBody UserListReq req) {
        PageResponse<UserListRes> result = managerService.getUserList(req);
        return ResponseEntity.ok(ResultResponse.success(result));
    }

    // 유저 상세 조회(Action 호출용)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResultResponse<?>> getUser(@PathVariable Long userId) {
        String result = managerService.getUser(userId);
        return ResponseEntity.ok(ResultResponse.success(result));
    }

    // 가게 전체 조회
    @PostMapping("/store")
    public ResponseEntity<ResultResponse<?>> getStoreList(@RequestBody StoreListReq req) {
        PageResponse<StoreListRes> result = managerService.getStoreList(req);
        return ResponseEntity.ok(ResultResponse.success(result));
    }

    // 가게 상세 조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<ResultResponse<?>> getStore(@PathVariable Long storeId) {
        StoreInManagerRes result = managerService.getStore(storeId);
        return ResponseEntity.ok(ResultResponse.success(result));
    }

    // 가게 영업 승인 상태 변경
    @PatchMapping("/store")
    public ResponseEntity<ResultResponse<?>> patchIsActiveInStore(@RequestParam(name = "id") List<Long> ids,
                                                                  @RequestParam int isActive) {
        managerService.patchIsActiveInStore(ids, isActive);
        return ResponseEntity.ok(ResultResponse.success("가게 수정 완료"));
    }

    // 금일 가입자 수, 가게 등록 수 통계
    @GetMapping("/stats")
    public ResponseEntity<ResultResponse<?>> getTodayStats() {
        List<Integer> result = managerService.getTodayStats();
        return ResponseEntity.ok(ResultResponse.success(result));
    }

    // 대시보드 가게 조회
    @GetMapping("/store/dashboard")
    public ResponseEntity<ResultResponse<?>> getStoreInDashboard() {
        List<StoreListRes> result = managerService.getStoreInDashboard();
        return ResponseEntity.ok(ResultResponse.success(result));
    }

    // 가입자 수 통계
    @GetMapping("/user/stats")
    public ResponseEntity<ResultResponse<?>> getUserStats(@ModelAttribute UserStatsReq req) {
        List<UserStatsRes> result = managerService.getUserStats(req);
        return ResponseEntity.ok(ResultResponse.success(result));
    }

    // 가게 등록 수 통계
    @GetMapping("/store/stats")
    public ResponseEntity<ResultResponse<?>> getStoreStats(@ModelAttribute StoreStatsReq req) {
        List<StoreStatsRes> result = managerService.getStoreStats(req);
        return ResponseEntity.ok(ResultResponse.success(result));
    }
}
