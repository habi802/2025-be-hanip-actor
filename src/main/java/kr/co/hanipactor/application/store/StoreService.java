package kr.co.hanipactor.application.store;

import jakarta.transaction.Transactional;
import kr.co.hanipactor.application.store.model.*;
import kr.co.hanipactor.application.storecategory.StoreCategoryMapper;
import kr.co.hanipactor.application.storecategory.StoreCategoryRepository;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import kr.co.hanipactor.configuration.model.ResultResponse;
import kr.co.hanipactor.configuration.utils.ImgUploadManager;
import kr.co.hanipactor.entity.Store;
import kr.co.hanipactor.entity.StoreCategory;
import kr.co.hanipactor.entity.StoreCategoryIds;
import kr.co.hanipactor.openfegin.favorites.FavoritesClient;
import kr.co.hanipactor.openfegin.favorites.model.StoreFavoriteRes;
import kr.co.hanipactor.openfegin.review.ReviewClient;
import kr.co.hanipactor.openfegin.review.model.ReviewGetRatingRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
    private final StoreCategoryRepository storeCategoryRepository;
    private final ImgUploadManager imgUploadManager;

    // 전체 가게 조회 & 가게 카테고리 및 가게 검색
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
            log.info("가게 아이디: {}", storeGetListRes.getId());
            log.info("유저 아이디: {}", signedUserId);
            Long userIdParam = signedUserId != null ? signedUserId : -1L;
            boolean favorites = favoritesClient.getStoreFavorites(storeGetListRes.getId(), userIdParam);
            storeGetListRes.setFavorite(favorites);
        }
        return list;
    }

    // 가게 상세 조회(사장)
    public StoreGetRes findOwnerStore(Long signedUserId) {
        Store store = storeRepository.findByUserId(signedUserId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 관리하는 가게가 아닙니다."));
        return StoreGetRes.of(store);
    }

    // 가게 상세 조회(고객)
    public StoreGetRes findCustomerStore(long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 가게가 없습니다."));
        return StoreGetRes.of(store);
    }

    // 가게 정보 수정
    @Transactional
    public Integer modifyStore(Long signedUserId, StorePutReq req, MultipartFile pic) {
        Store store = storeRepository.findByUserId(signedUserId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 관리하는 가게가 아닙니다."));
        int result = 0;

        // 가게 활성화 전(승인 전)에만 수정 가능한 항목
        if (store.getIsActive() == 0) {
            // 가게 전화번호
            if (req.getTel() != null) {
                store.setTel(req.getTel());
                result++;
            }

            // 가게 카테고리
            if (req.getStoreCategory() != null) {
                List<StoreCategory> storeCategory = storeCategoryRepository.findByStoreId(req.getId());
                for (StoreCategory category : storeCategory) {
                    StoreCategoryIds storeCategoryIds = new StoreCategoryIds();
                    storeCategoryIds.setCategory(req.getStoreCategory());

                    category.setStoreCategoryId(storeCategoryIds);
                    category.setStore(store);
                }
                storeCategoryRepository.saveAll(storeCategory);
                result++;
            }
        }

        // 가게 활성화 여부 상관없이 수정 가능한 항목
        // 가게 이미지
        if (pic != null && !pic.isEmpty()) {
            String savedFileName = imgUploadManager.saveStorePic(signedUserId, pic);
            store.setImagePath(savedFileName);
            result += 1;
        }

        // 가게 코멘트
        if (req.getComment() != null) {
            store.setComment(req.getComment());
            result++;
        }

        // 영업 여부
        if (req.getIsOpen() != null) {
            store.setIsOpen(req.getIsOpen());
            result++;
        }

        // 영업 시간
        if (req.getOpenTime() != null) {
            store.setOpenTime(req.getOpenTime());
            result++;
        }
        if (req.getCloseTime() != null) {
            store.setCloseTime(req.getCloseTime());
            result++;
        }

        // 휴무일
        if (req.getDayOfWeek() != null) {
            store.setClosedDay(req.getDayOfWeek());
            result++;
        }

        // 최소 배달 요금
        if (req.getMinDeliveryFee() != null) {
            store.setMinDeliveryFee(req.getMinDeliveryFee());
            result++;
        }

        // 최대 배달 요금
        if (req.getMaxDeliveryFee() != null) {
            store.setMaxDeliveryFee(req.getMaxDeliveryFee());
            result++;
        }

        // 최소 주문 금액
        if (req.getMinAmount() != null) {
            store.setMinAmount(req.getMinAmount());
            result++;
        }

        // 포장 주문 여부
        if (req.getIsPickUp() != null) {
            store.setIsPickUp(req.getIsPickUp());
            result++;
        }

        // 이벤트 알림
        if (req.getEventComment() != null) {
            store.setEventComment(req.getEventComment());
            result++;
        }

        return result;
    }

    // 가게 평점 및 좋아요 갯수 수정 (서버 api)
    public Integer updateRatingAndFavorites(StorePatchReq req) {
        Store store = storeRepository.findById(req.getId()).orElse(null);
        if (store == null) {
            return 0;
        }

        if (req.getRating() != null) {
            store.setRating(req.getRating());
        }

        if (req.getFavorites() != null) {
            store.setFavorites(req.getFavorites());
        }

        storeRepository.save(store);
        return 1;
    }
}
