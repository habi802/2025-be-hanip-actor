package kr.co.hanipactor.application.user;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.hanipactor.application.user.model.UserJoinReq;
import kr.co.hanipactor.application.user.model.UserLoginDto;
import kr.co.hanipactor.application.user.model.UserLoginReq;
import kr.co.hanipactor.application.user.model.UserLoginRes;
import kr.co.hanipactor.configuration.common.model.ResultResponse;
import kr.co.hanipactor.configuration.jwt.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;

    @PostMapping("/join")
    public ResponseEntity<ResultResponse<Integer>> join(@RequestBody UserJoinReq req) {
        log.info("req: {}", req);
        Integer result = userService.join(req);

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

}
