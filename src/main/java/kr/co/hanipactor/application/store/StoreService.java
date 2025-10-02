package kr.co.hanipactor.application.store;

import jakarta.transaction.Transactional;
import kr.co.hanipactor.application.store.model.*;
import kr.co.hanipactor.application.storecategory.StoreCategoryMapper;
import kr.co.hanipactor.application.storecategory.StoreCategoryRepository;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import kr.co.hanipactor.configuration.utils.ImgUploadManager;
import kr.co.hanipactor.entity.Store;
import kr.co.hanipactor.entity.StoreCategory;
import kr.co.hanipactor.entity.StoreCategoryIds;
import kr.co.hanipactor.openfeign.favorites.FavoritesClient;
import kr.co.hanipactor.openfeign.review.ReviewClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        }
        return list;
    }

    // 가게 상세 조회(사장)
    public StoreGetRes findOwnerStore(Long signedUserId) {
        Store store = storeRepository.findByUserId(signedUserId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 관리하는 가게가 아닙니다."));

        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(store.getId());
        List<EnumStoreCategory> categories = new ArrayList<>();
        for(StoreCategory storeCategory : storeCategories) {
            categories.add(storeCategory.getStoreCategoryId().getCategory());
        }

        return StoreGetRes.of(store, categories);
    }

    // 가게 상세 조회(고객)
    public StoreGetRes findCustomerStore(long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 가게가 없습니다."));

        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(store.getId());
        List<EnumStoreCategory> categories = new ArrayList<>();
        for(StoreCategory storeCategory : storeCategories) {
            categories.add(storeCategory.getStoreCategoryId().getCategory());
        }

        return StoreGetRes.of(store, categories);
    }

    // 가게 정보 수정
    @Transactional
    public Integer modifyStore(Long signedUserId, StorePutReq req, MultipartFile pic, MultipartFile bannerPic) {
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
                // 기존 카테고리 삭제
                storeCategoryRepository.deleteByStoreId(req.getId());

                // 새로운 카테고리 저장
                List<StoreCategory> storeCategory = req.getStoreCategory().stream()
                        .map(cat -> {
                            StoreCategoryIds ids = new StoreCategoryIds();
                            ids.setCategory(cat);

                            return StoreCategory.builder()
                                    .storeCategoryId(ids)
                                    .store(store)
                                    .build();
                        })
                        .toList();

                storeCategoryRepository.saveAll(storeCategory);
                result++;
            }
        }

        // 가게 활성화 여부 상관없이 수정 가능한 항목
        // 가게 이미지
        if (pic != null && !pic.isEmpty() || bannerPic != null && !bannerPic.isEmpty()) {
            String savedFileName = imgUploadManager.saveStorePic(signedUserId, pic);
            store.setImagePath(savedFileName);
            result += 1;

            String savedFileName2 = imgUploadManager.saveStorePic(signedUserId, bannerPic);
            store.setBannerPath(savedFileName2);
            result += 1;
        }

        // 가게 코멘트
        result += applyIfNotNull(req.getComment(), store::setComment, store::getComment);

        // 영업 여부
        result += applyIfNotNull(req.getIsOpen(), store::setIsOpen, store::getIsOpen);

        // 영업 시간
        result += applyIfNotNull(req.getOpenTime(), store::setOpenTime, store::getOpenTime);
        result += applyIfNotNull(req.getCloseTime(), store::setCloseTime, store::getCloseTime);

        // 휴무일
        result += applyIfNotNull(req.getDayOfWeek(), store::setClosedDay, store::getClosedDay);

        // 최소 배달 요금
        result += applyIfNotNull(req.getMinDeliveryFee(), store::setMinDeliveryFee, store::getMinDeliveryFee);

        // 최대 배달 요금
        result += applyIfNotNull(req.getMaxDeliveryFee(), store::setMaxDeliveryFee, store::getMaxDeliveryFee);

        // 최소 주문 금액
        result += applyIfNotNull(req.getMinAmount(), store::setMinAmount, store::getMinAmount);

        // 포장 주문 여부
        result += applyIfNotNull(req.getIsPickUp(), store::setIsPickUp, store::getIsPickUp);

        // 이벤트 알림
        result += applyIfNotNull(req.getEventComment(), store::setEventComment, store::getEventComment);

        return result;
    }

    // 가게 수정 헬퍼 메소드
    public <T> int applyIfNotNull(T newValue, Consumer<T> setter, Supplier<T> getter) {
        if (newValue != null && !Objects.equals(newValue, getter.get())) {
            setter.accept(newValue);
            return 1;
        }
        return 0;
    }

    // 가게 영업 활성화
    @Transactional
    public Integer updateIsOpenByStoreIdAndUserId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 가게가 없습니다."));

        log.info("store.getIsOpen(): {}", store.getIsOpen());
        store.setIsOpen(store.getIsOpen() == 0 ? 1 : 0);
        storeRepository.save(store);
        return 1;
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

    // 배달원용 - 배달 음식 픽업할 가게의 주소 가져옴
    public String getStoreInRider(long storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);

        return store.getPostcode() + ", " + store.getAddress() + (store.getAddressDetail() != null ? ", " + store.getAddressDetail() : "");
    }
}
