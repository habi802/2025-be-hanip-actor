package kr.co.hanipactor.openfegin.favorites;

import kr.co.hanipactor.configuration.FeignConfiguration;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.openfegin.favorites.model.StoreFavoriteRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "HANIP-ACTION", contextId = "favoriteClient", configuration = FeignConfiguration.class)
public interface FavoritesClient {
    @GetMapping("/api/favorite/count")
    boolean getStoreFavorites(@RequestParam("store_id") Long storeId,
                                     @RequestParam(value = "user_id", required = false) Long userId);
}
