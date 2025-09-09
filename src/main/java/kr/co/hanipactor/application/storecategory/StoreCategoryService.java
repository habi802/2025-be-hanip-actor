package kr.co.hanipactor.application.storecategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCategoryService {
    private final StoreCategoryMapper storeCategoryMapper;
}
