package kr.co.hanipactor.application.store;

import kr.co.hanipactor.application.store.model.StoreGetListReq;
import kr.co.hanipactor.application.store.model.StoreGetListRes;
import kr.co.hanipactor.application.store.model.StorePatchReq;
import kr.co.hanipactor.application.storecategory.StoreCategoryMapper;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.entity.Store;
import kr.co.hanipactor.openfegin.favorites.FavoritesClient;
import kr.co.hanipactor.openfegin.review.ReviewClient;
import kr.co.hanipactor.openfegin.review.model.ReviewGetRatingRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreMapper storeMapper;
    private final StoreCategoryMapper storeCategoryMapper;
    private final FavoritesClient favoritesClient;
    private final ReviewClient reviewClient;
    private final StoreRepository storeRepository;

    public List<StoreGetListRes> findAllStore(StoreGetListReq req, Long signedUserId) {
        String categoryCode = req.getCategory() != null ? req.getCategory().getCode() : null; // Enum -> String 변환
        List<StoreGetListRes> list = storeMapper.findAllStore(req, categoryCode);

        List<Long> storeIdList = new ArrayList<>(list.size()); // storeId 수집용

        for (StoreGetListRes storeGetListRes : list) {
            storeIdList.add(storeGetListRes.getId()); // storeId 수집
            List<String> categoryCodes = storeCategoryMapper.findByStoreId(storeGetListRes.getId());

            List<EnumStoreCategory> categories = categoryCodes.stream()
                    .map(code -> EnumStoreCategory.valueOfCode(code))
                    .toList();

            storeGetListRes.setCategory(categories);

            ResultResponse<List<ReviewGetRatingRes>> ratingRes = reviewClient.findByStoreIdAllReview(storeGetListRes.getId());
            if (ratingRes != null && ratingRes.getResultData() != null) {
                double avgRating = ratingRes.getResultData().stream()
                        .mapToInt(ReviewGetRatingRes::getRating)
                        .average()
                        .orElse(0.0);

                storeGetListRes.setRating((int)Math.round(avgRating));
            } else {
                storeGetListRes.setRating(0);
            }
        }
        return list;
    }

    public Integer updateRatingAndFavorites(StorePatchReq req) {
        Store store = storeRepository.findById(req.getId()).orElse(null);
        if (store == null) {
            return 0;
        }

        store.setRating(req.getRating());
        store.setFavorites(req.getFavorites());

        storeRepository.save(store);
        return 1;
    }
}
