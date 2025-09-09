package kr.co.hanipactor.openfegin.review;

import kr.co.hanipactor.configuration.FeignConfiguration;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.openfegin.review.model.ReviewGetRatingRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "${constants.open-feign.review}", configuration = FeignConfiguration.class)
public interface ReviewClient {
    @GetMapping("/store-review/{storeId}")
    ResultResponse<List<ReviewGetRatingRes>> findByStoreIdAllReview(
            @PathVariable("storeId") Long storeId
    );
}
