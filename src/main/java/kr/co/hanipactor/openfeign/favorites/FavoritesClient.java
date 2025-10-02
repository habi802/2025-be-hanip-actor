package kr.co.hanipactor.openfeign.favorites;

import kr.co.hanipactor.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "hanip-action",
        contextId = "favoriteClient",
        url = "${constants.open-feign.action.url}",
        configuration = FeignConfiguration.class)
public interface FavoritesClient {
    @GetMapping("/api/favorite/count")
    boolean getStoreFavorites(@RequestParam("store_id") Long storeId,
                                     @RequestParam(value = "user_id", required = false) Long userId);
}
