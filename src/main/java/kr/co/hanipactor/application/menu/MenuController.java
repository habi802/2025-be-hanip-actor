package kr.co.hanipactor.application.menu;

import kr.co.hanipactor.application.menu.model.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.configuration.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    // 가게 메뉴 조회
    @GetMapping
    public ResponseEntity<ResultResponse<?>> getMenuList(@RequestParam Long storeId) {
        List<MenuListGetRes> result = menuService.getMenuList(storeId);
        return result == null
                ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResultResponse.fail(400, "등록된 메뉴가 없습니다."))
                : ResponseEntity.ok(ResultResponse.success(result));
    }

    // 가게 메뉴 상세 조회
    @GetMapping("/{menuId}")
    public ResponseEntity<ResultResponse<?>> getMenu(@PathVariable Long menuId) {
        MenuGetRes result = menuService.getMenu(menuId);
        return result == null
                ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResultResponse.fail(400, "등록되지 않은 메뉴입니다."))
                : ResponseEntity.ok(ResultResponse.success(result));
    }

    // 메뉴 삭제
    @DeleteMapping("/{menuId}")
    public ResponseEntity<ResultResponse<?>> deleteMenu(@PathVariable Long menuId) {
        int result = menuService.deleteMenu(menuId);
        return result == 0
                ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResultResponse.fail(400, "등록되지 않은 메뉴입니다."))
                : ResponseEntity.ok(ResultResponse.success("메뉴가 삭제되었습니다."));
    }

    // 주문 내역 메뉴 조회
    @PostMapping("/order")
    public ResponseEntity<ResultResponse<?>> getOrderMenu(@RequestBody OrderMenuGetReq req) {
        List<OrderMenuGetRes> result = menuService.getOrderMenu(req);
        return result == null
                ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResultResponse.fail(400, "주문한 메뉴가 없습니다."))
                : ResponseEntity.ok(ResultResponse.success(result));
    }
}
