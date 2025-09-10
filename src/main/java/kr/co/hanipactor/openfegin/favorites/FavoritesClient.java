package kr.co.hanipactor.openfegin.favorites;

import kr.co.hanipactor.configuration.FeignConfiguration;
import kr.co.hanipactor.openfegin.favorites.model.StoreFavoriteRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "HANIP-ACTION", contextId = "favoriteClient", configuration = FeignConfiguration.class)
public interface FavoritesClient {
    @GetMapping("/api/feed/count")
    StoreFavoriteRes getStoreFavorites(@RequestParam("storeId") Long storeId,
                                       @RequestParam("userId") Long userId);
}
