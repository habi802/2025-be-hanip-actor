package kr.co.hanipactor.configuration.model;

import kr.co.hanipactor.application.user.UserGetItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ResultResponse<T> {
    private int resultStatus;
    private String resultMessage;
    private T resultData;

    public ResultResponse(String success, Map<Long, UserGetItem> result) {
    }

    public static <T> ResultResponse<T> success(T data) {
        return new ResultResponse<>(200, "성공", data);
    }

    public static <T> ResultResponse<T> fail(int status, String message) {
        return new ResultResponse<>(status, message, null);
    }
}
