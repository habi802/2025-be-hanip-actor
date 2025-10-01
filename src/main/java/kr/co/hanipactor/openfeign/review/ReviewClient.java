package kr.co.hanipactor.openfeign.review;

import kr.co.hanipactor.configuration.FeignConfiguration;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.openfeign.review.model.ReviewGetRatingRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "${constants.open-feign.action.name:hanip-action}",
        contextId = "reviewClient",
        url = "${constants.open-feign.action.url:}",
        configuration = FeignConfiguration.class)
public interface ReviewClient {
    @GetMapping("/api/review/store-review/{storeId}")
    ResultResponse<List<ReviewGetRatingRes>> findByStoreIdAllReview(@PathVariable("storeId") Long storeId);
}
