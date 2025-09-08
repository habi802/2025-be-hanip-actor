package kr.co.hanipactor.application.menu;

import kr.co.hanipactor.application.menu.model.MenuPostReq;
import kr.co.hanipactor.application.menu.model.MenuGetItemReq;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.configuration.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<ResultResponse<?>> saveMenu(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                      @RequestPart MenuPostReq req,
                                                      @RequestPart MultipartFile pic) {
        try {
            log.info("signedUserId: {}", userPrincipal.getSignedUserId());

            // 권한 체크
            if (userPrincipal.getJwtUser().getRole() != EnumUserRole.OWNER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ResultResponse.fail(401, "권한이 없습니다."));
            }

            menuService.saveMenu(userPrincipal.getSignedUserId(), req, pic);

            return ResponseEntity.ok(ResultResponse.success("메뉴가 성공적으로 저장되었습니다."));
        } catch (IllegalArgumentException e) {
            // 예: 요청 파라미터가 잘못됐을 때
            return ResponseEntity.badRequest()
                    .body(ResultResponse.fail(400, "잘못된 요청입니다: " + e.getMessage()));
        } catch (Exception e) {
            // 기타 서버 오류
            log.error("메뉴 저장 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultResponse.fail(500, "서버 오류가 발생했습니다."));
        }
    }

    // 주문 내역 메뉴 조회
    @GetMapping
    public ResponseEntity<ResultResponse<?>> getOrderMenu(@ModelAttribute MenuGetItemReq req) {
        return null;
    }
}
