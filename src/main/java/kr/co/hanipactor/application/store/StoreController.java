package kr.co.hanipactor.application.store;


import jakarta.servlet.annotation.MultipartConfig;
import kr.co.hanipactor.application.store.model.*;
import kr.co.hanipactor.configuration.enumcode.model.EnumUserRole;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.configuration.model.UserPrincipal;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    // 가게 전체 조회 (유저 전용)
    @GetMapping
    public ResponseEntity<ResultResponse<List<StoreGetListRes>>> findAllStore(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                              @ModelAttribute StoreGetListReq req) {
        Long signedUserId = (userPrincipal != null) ? userPrincipal.getSignedUserId() : null;
        log.info("검색 요청: {}", req);
        log.info("검색자 확인: {}", signedUserId);
        List<StoreGetListRes> result = storeService.findAllStore(req, signedUserId);
        return ResponseEntity.ok(new ResultResponse<>(200, "가게 조회 성공", result));
    }

    // 가게 상세 조회 (사장)
    @GetMapping("/owner")
    public ResponseEntity<ResultResponse<StoreGetRes>> findStore(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long signedUserId = userPrincipal.getSignedUserId();
        StoreGetRes result = storeService.findOwnerStore(signedUserId);
        return ResponseEntity.ok(new ResultResponse<>(200, "사장 가게 조회 성공", result));
    }

    // 가게 상세 조회 (고객)
    @GetMapping("/{storeId}")
    public ResponseEntity<ResultResponse<StoreGetRes>> findStore(@PathVariable long storeId) {
        StoreGetRes result = storeService.findCustomerStore(storeId);
        return ResponseEntity.ok(new ResultResponse<>(200, "가게 조회 성공", result));
    }

    // 가게 수정 (사장)
    @PutMapping("/update")
    public ResponseEntity<ResultResponse<Integer>> modifyStore(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                               @RequestPart StorePutReq req,
                                                               @RequestPart(required = false) MultipartFile pic,
                                                               @RequestPart(required = false) MultipartFile bannerPic) {
        Long signedUserId = userPrincipal.getSignedUserId();
        Integer result = storeService.modifyStore(signedUserId, req, pic, bannerPic);
        return ResponseEntity.ok(new ResultResponse<>(200, "가게 수정 성공", result));
    }

    // 가게 영업 활성화
    @PatchMapping("/{storeId}")
    public ResponseEntity<ResultResponse<?>> modifyOpenStore(@PathVariable Long storeId) {
        Integer result = storeService.updateIsOpenByStoreIdAndUserId(storeId);
        return ResponseEntity.ok(new ResultResponse<>(200, "가게 영업 변경 완료", result));
    }

    // 좋아요 수 및 별점 평균 가게 수정 (서버 전용 API)
    @PatchMapping
    public ResponseEntity<ResultResponse<?>> patchStore(@RequestBody StorePatchReq req) {
        int result = storeService.updateRatingAndFavorites(req);
        return result == 0 ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                           .body(ResultResponse.fail(400, "존재 하지 않는 가게"))
                           : ResponseEntity.ok(ResultResponse.success("평균 별점, 좋아요 수 수정완료"));
    }


    // 배달원용 - 배달 음식 픽업할 가게의 주소 가져옴
    @GetMapping("/rider/{storeId}")
    public ResponseEntity<ResultResponse<?>> getStoreInRider(@PathVariable long storeId) {
        String result = storeService.getStoreInRider(storeId);

        return ResponseEntity.ok(ResultResponse.success(result));
    }
}
