package kr.co.hanipactor.application.store;


import kr.co.hanipactor.application.store.model.StoreGetListReq;
import kr.co.hanipactor.application.store.model.StoreGetListRes;
import kr.co.hanipactor.application.store.model.StorePatchReq;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.configuration.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    // 가게 전체 조회
    @GetMapping
    public ResponseEntity<ResultResponse<List<StoreGetListRes>>> findAllStore(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                              @ModelAttribute StoreGetListReq req) {
        Long signedUserId = (userPrincipal != null) ? userPrincipal.getSignedUserId() : null;
        log.info("검색 요청: {}", req);
        log.info("검색자 확인: {}", signedUserId);
        List<StoreGetListRes> result = storeService.findAllStore(req, signedUserId);
        return ResponseEntity.ok(new ResultResponse<>(200, "가게 조회 성공", result));
    }

    @PatchMapping
    public ResponseEntity<ResultResponse<?>> patchStore(@RequestBody StorePatchReq req) {
        int result = storeService.updateRatingAndFavorites(req);
        return result == 0 ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                           .body(ResultResponse.fail(400, "존재 하지 않는 가게"))
                           : ResponseEntity.ok(ResultResponse.success("평균 별점, 좋아요 수 수정완료"));
    }

}
