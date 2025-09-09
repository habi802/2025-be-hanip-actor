package kr.co.hanipactor.application.store;

import kr.co.hanipactor.application.store.model.StoreGetListReq;
import kr.co.hanipactor.application.store.model.StoreGetListRes;
import kr.co.hanipactor.application.storecategory.StoreCategoryMapper;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreMapper storeMapper;
    private final StoreCategoryMapper storeCategoryMapper;

    public List<StoreGetListRes> findAllStore(StoreGetListReq req, Long signedUserId) {
        String categoryCode = req.getCategory() != null ? req.getCategory().getCode() : null;
        List<StoreGetListRes> list = storeMapper.findAllStore(req, categoryCode);

        for (StoreGetListRes storeGetListRes : list) {
            List<String> categoryCodes = storeCategoryMapper.findByStoreId(storeGetListRes.getId());

            List<EnumStoreCategory> categories = categoryCodes.stream()
                    .map(code -> EnumStoreCategory.valueOfCode(code))
                    .toList();

            storeGetListRes.setCategory(categories);
        }

        return list;
    }
}
