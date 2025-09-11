package kr.co.hanipactor.application.manager;

import kr.co.hanipactor.application.manager.model.UserAllGetReq;
import kr.co.hanipactor.application.manager.model.UserAllGetRes;
import kr.co.hanipactor.configuration.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/hanip-manager/actor")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    // 유저 전체 조회
    @GetMapping
    public ResponseEntity<ResultResponse<?>> allUser(@RequestBody UserAllGetReq req) {
        log.info("allUser req: {}", req);
        Page<UserAllGetRes> result = managerService.allUser(req);
        return ResponseEntity.ok(ResultResponse.success(result));
    }
}
