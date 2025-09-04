package kr.co.hanipactor.application.menu;

import jakarta.transaction.Transactional;
import kr.co.hanipactor.application.menu.model.MenuPostReq;
import kr.co.hanipactor.application.menuoption.MenuOptionRepository;
import kr.co.hanipactor.application.menuoption.model.MenuOptionPostReq;
import kr.co.hanipactor.configuration.utils.ImgUploadManager;
import kr.co.hanipactor.entity.Menu;
import kr.co.hanipactor.entity.MenuOption;
import kr.co.hanipactor.entity.Store;
import kr.co.hanipactor.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final ImgUploadManager imgUploadManager;

    @Transactional
    public void saveMenu(Long signedUserId, MenuPostReq req, MultipartFile pic) {
        User user = User.builder()
                .id(signedUserId)
                .build();

        Store store = Store.builder()
                .id(req.getStoreId())
                .build();

        String savedFileName = null;
        if(pic != null) {
            savedFileName = imgUploadManager.saveProfilePic(req.getStoreId(), pic);
        }

        // 메뉴 저장
        Menu menu = Menu.builder()
                .store(store)
                .user(user)
                .name(req.getName())
                .comment(req.getComment())
                .price(req.getPrice())
                .imagePath(savedFileName)
                .menuType(req.getMenuType())
                .build();
        menuRepository.save(menu);

        // 메뉴 옵션 저장
        if (req.getMenuOption() != null) {
            for (MenuOptionPostReq optionReq : req.getMenuOption()) {
                saveMenuOpt(menu, null, optionReq);
            }
        }
    }

    // 메뉴 옵션 메소드
    private void saveMenuOpt(Menu menu, MenuOption parentOption, MenuOptionPostReq optionReq) {
        MenuOption menuOption = MenuOption.builder()
                .menu(menu)
                .parentId(parentOption)  // 부모 옵션 연결 (없으면 null)
                .comment(optionReq.getComment())
                .price(optionReq.getPrice())
                .isRequired(optionReq.getIsRequired())
                .build();
        menuOptionRepository.save(menuOption);

        if (optionReq.getChildren() != null) {
            for (MenuOptionPostReq childReq : optionReq.getChildren()) {
                saveMenuOpt(menu, menuOption, childReq);
            }
        }
    }
}
