package kr.co.hanipactor.application.menu;

import jakarta.transaction.Transactional;
import kr.co.hanipactor.application.menu.model.*;
import kr.co.hanipactor.application.menuoption.MenuOptionRepository;
import kr.co.hanipactor.application.menuoption.model.MenuOptionPostReq;
import kr.co.hanipactor.application.store.StoreRepository;
import kr.co.hanipactor.application.user.UserRepository;
import kr.co.hanipactor.configuration.enumcode.model.EnumMenuType;
import kr.co.hanipactor.configuration.utils.ImgUploadManager;
import kr.co.hanipactor.entity.Menu;
import kr.co.hanipactor.entity.MenuOption;
import kr.co.hanipactor.entity.Store;
import kr.co.hanipactor.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final StoreRepository storeRepository;
    private final ImgUploadManager imgUploadManager;
    private final UserRepository userRepository;

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
            savedFileName = imgUploadManager.saveStorePic(req.getStoreId(), pic);
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
                .parent(parentOption)  // 부모 옵션 연결 (없으면 null)
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

    // 가게 메뉴 조회
    public List<MenuListGetRes> getMenuList(Long storeId) {
        // 가게 id를 가진 메뉴 조회 후 메뉴 종류 별로 그룹핑
        List<Menu> menus = menuRepository.findByStore_Id(storeId);
        Map<EnumMenuType, List<Menu>> sortedMenus = menus.stream()
                                                         .collect(Collectors.groupingBy(Menu::getMenuType));

        // 결과값으로 보낼 객체 타입에 맞게 데이터를 넣어준 뒤, 리스트에 담는 과정
        List<MenuListGetRes> result = new ArrayList<>(sortedMenus.size());
        for (EnumMenuType menuType : sortedMenus.keySet()) {
            MenuListGetRes res = MenuListGetRes.builder()
                                       .menuType(menuType.getValue())
                                       .menus(sortedMenus.get(menuType).stream()
                                                                       .map(menu -> MenuListGetRes.Menu.builder()
                                                                                                       .menuId(menu.getId())
                                                                                                       .name(menu.getName())
                                                                                                       .price(menu.getPrice())
                                                                                                       .comment(menu.getComment())
                                                                                                       .imagePath(menu.getImagePath())
                                                                                                       .build())
                                                                       .toList())
                                       .build();

            result.add(res);
        }

        return result;
    }
    // 가게 사장 메뉴 조회
    public List<MenuListGetRes> getOwnerMenuList(Long signedUserId) {
        Store store = storeRepository.findByUserId(signedUserId).orElse(null);
        List<Menu> menus = menuRepository.findByStore_Id(store.getId());
        Map<EnumMenuType, List<Menu>> sortedMenus = menus.stream()
                .collect(Collectors.groupingBy(Menu::getMenuType));

        List<MenuListGetRes> result = new ArrayList<>(sortedMenus.size());
        for (EnumMenuType menuType : sortedMenus.keySet()) {
            MenuListGetRes res = MenuListGetRes.builder()
                    .menuType(menuType.getValue())
                    .menus(sortedMenus.get(menuType).stream()
                            .map(menu -> MenuListGetRes.Menu.builder()
                                    .menuId(menu.getId())
                                    .name(menu.getName())
                                    .price(menu.getPrice())
                                    .comment(menu.getComment())
                                    .imagePath(menu.getImagePath())
                                    .build())
                            .toList())
                    .build();

            result.add(res);
        }
        return result;
    }

    // 가게 메뉴 상세 조회
    public MenuGetRes getMenu(Long menuId) {
        // 입력한 id를 가진 메뉴가 없을 경우 null 리턴
        Menu menu = menuRepository.findById(menuId).orElse(null);
        if (menu == null) {
            return null;
        }

        // 메뉴의 옵션 조회
        List<MenuOption> menuOptions = menuOptionRepository.findByMenu_Id(menuId);

        // 결과값으로 보낼 객체의 멤버 필드 중 옵션에 하위 옵션까지 담는 과정
        List<MenuGetRes.Option> options = menuOptions.stream()
                                                     .filter(option -> option.getParent() == null)
                                                     .map(option -> MenuGetRes.Option.builder()
                                                                                     .optionId(option.getId())
                                                                                     .comment(option.getComment())
                                                                                     .price(option.getPrice())
                                                                                     .children(option.getChildren().stream()
                                                                                                                   .map(child -> MenuGetRes.Option.builder()
                                                                                                                                                  .optionId(child.getId())
                                                                                                                                                  .comment(child.getComment())
                                                                                                                                                  .price(child.getPrice())
                                                                                                                                                  .build())
                                                                                                                   .toList())
                                                                                     .build())
                                                     .toList();

        return MenuGetRes.builder()
                         .menuId(menuId)
                         .name(menu.getName())
                         .price(menu.getPrice())
                         .comment(menu.getComment())
                         .imagePath(menu.getImagePath())
                         .options(options)
                         .build();
    }

    // 메뉴 수정
    @Transactional
    public int updateMenu(MenuPutReq req, MultipartFile menuImage) {
        // 1. 입력한 id를 가진 메뉴가 없을 경우 null 리턴
        Menu menu = menuRepository.findById(req.getMenuId()).orElse(null);
        if (menu == null) {
            return 0;
        }

        // 2. 기본 정보 수정
        menu.setName(req.getName());
        menu.setPrice(req.getPrice());
        menu.setComment(req.getComment());
        EnumMenuType menuType = EnumMenuType.fromCode(req.getMenuType());
        menu.setMenuType(menuType);

        // 3. 메뉴 이미지 수정
        if (menuImage != null) {

        }

        // 4. 옵션 수정
        // 기존에 있었던 옵션을 가져옴
        List<MenuOption> menuOptions = menuOptionRepository.findByMenu_Id(menu.getId());
        Map<Long, MenuOption> existingOptions = menuOptions.stream()
                                                           .collect(Collectors.toMap(MenuOption::getId, option -> option));
        List<MenuOption> updateOptions = new ArrayList<>();
        for (MenuPutReq.Option optionReq : req.getOptions()) {
            // 4-1. 상위 옵션 수정
            MenuOption option;

            if (optionReq.getOptionId() != null && existingOptions.containsKey(optionReq.getOptionId())) {
                // 기존 상위 옵션 수정
                option = existingOptions.get(optionReq.getOptionId());

                option.setComment(optionReq.getComment());
                option.setIsRequired(optionReq.getIsRequired());
            } else {
                // 새 상위 옵션 추가
                option = MenuOption.builder()
                                   .menu(menu)
                                   .comment(optionReq.getComment())
                                   .isRequired(optionReq.getIsRequired())
                                   .children(new ArrayList<>())
                                   .build();
            }

            // 4-2. 하위 옵션 수정
            // 기존에 있었던 상위 옵션일 경우, 그 상위 옵션의 기존 하위 옵션 리스트를 가져오고,
            // 새 상위 옵션일 경우, 초기화된 리스트를 가져온다.
            List<MenuOption> menuSubOptions = Optional.ofNullable(option.getChildren()).orElse(new ArrayList<>());
            Map<Long, MenuOption> existingSubOptions = menuSubOptions.stream()
                                                                     .collect(Collectors.toMap(MenuOption::getId, child -> child));
            List<MenuOption> updateSubOptions = new ArrayList<>();
            if (optionReq.getSubOptions() != null) {
                for (MenuPutReq.Option.SubOption subOptionReq : optionReq.getSubOptions()) {
                    MenuOption subOption;
                    if (subOptionReq.getOptionId() != null && existingSubOptions.containsKey(subOptionReq.getOptionId())) {
                        // 기존 하위 옵션 수정
                        subOption = existingSubOptions.get(subOptionReq.getOptionId());

                        subOption.setComment(subOptionReq.getComment());
                        subOption.setPrice(subOptionReq.getPrice());
                    } else {
                        // 새 하위 옵션 추가
                        subOption = MenuOption.builder()
                                              .menu(menu)
                                              .parent(option)
                                              .comment(subOptionReq.getComment())
                                              .price(subOptionReq.getPrice())
                                              .build();
                    }
                    updateSubOptions.add(subOption);
                }
            }

            // 상위 옵션의 기존 하위 옵션들을 다 삭제한 후에, 최종 수정된 하위 옵션을 다시 추가함
            option.getChildren().clear();
            option.getChildren().addAll(updateSubOptions);

            updateOptions.add(option);
        }

        // 기존에 있었으나 req에 없었던 옵션은 삭제
        Set<Long> updateOptionIds = updateOptions.stream()
                                                 .map(MenuOption::getId)
                                                 .filter(Objects::nonNull)
                                                 .collect(Collectors.toSet());

        List<MenuOption> deleteOptions = menuOptions.stream()
                                                    .filter(option -> !updateOptionIds.contains(option.getId()))
                                                    .collect(Collectors.toList());

        menuOptionRepository.deleteAll(deleteOptions);

        // 새로 추가된 옵션은 추가
        List<MenuOption> newOptions = updateOptions.stream()
                                                   .filter(option -> option.getId() == null)
                                                   .toList();
        menuOptionRepository.saveAll(newOptions);

        return 1;
    }

    // 메뉴 품절 여부, 숨김 여부 변경
    @Transactional
    public int patchMenu(MenuPatchReq req) {
        Menu menu = menuRepository.findById(req.getMenuId()).orElse(null);
        if (menu == null) {
            return 0;
        }

        if (req.getIsHide() != null) {
            menu.setIsHide(req.getIsHide());
        }
        if (req.getIsSoldOut() != null) {
            menu.setIsSoldOut(req.getIsSoldOut());
        }
        return 1;
    }

    // 메뉴 삭제
    @Transactional
    public int deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElse(null);
        if (menu == null) {
            return 0;
        }

        menuRepository.deleteById(menuId);
        return 1;
    }

    // 주문 내역 메뉴 조회
    public List<OrderMenuGetRes> getOrderMenu(OrderMenuGetReq req) {
        // Action 쪽에서 보낸 menuId, optionId를 가진 메뉴, 메뉴 옵션 조회
        List<Menu> menus = menuRepository.findAllById(req.getMenuIds());
        List<MenuOption> menuOptions = menuOptionRepository.findAllById(req.getOptionIds());

        // 메뉴 옵션을 상위 옵션->하위 옵션으로 보여질 수 있게 정렬
        Map<Long, MenuOption> menuOptionMap = menuOptions.stream()
                                                         .collect(Collectors.toMap(MenuOption::getId, option -> option));
        List<MenuOption> sortedMenuOptions = new ArrayList<>(menuOptions.size());
        for (MenuOption option : menuOptions) {
            if (option.getParent() == null) {
                sortedMenuOptions.add(option);
            } else {
                MenuOption parentOption = menuOptionMap.get(option.getParent().getId());
                if (parentOption != null) {
                    parentOption.getChildren().add(option);
                }
            }
        }

        // 결과값으로 보낼 객체 리스트에 데이터를 담는 과정
        List<OrderMenuGetRes> result = new ArrayList<>(menus.size());
        for (Menu menu : menus) {
            // 정렬된 메뉴 옵션 리스트에서 menu의 id를 가진 옵션(즉, 해당 메뉴의 옵션) 리스트를 만듦
            List<MenuOption> optionsInMenu = sortedMenuOptions.stream()
                                                              .filter(option -> option.getMenu().getId() == menu.getId())
                                                              .toList();

            // 위에서 옵션 리스트에서 상위 옵션에 하위 옵션을 담는 과정
            // Sorry..
            List<OrderMenuGetRes.Option> options = optionsInMenu.stream()
                                                                .filter(option -> option.getParent() == null)
                                                                // 상위 옵션 담는 거
                                                                .map(option -> OrderMenuGetRes.Option.builder()
                                                                                                     .optionId(option.getId())
                                                                                                     .comment(option.getComment())
                                                                                                     .price(option.getPrice())
                                                                                                     .children(option.getChildren().stream()
                                                                                                                                   // 하위 옵션 담는 거
                                                                                                                                   .map(child -> OrderMenuGetRes.Option.builder()
                                                                                                                                                                       .optionId(child.getId())
                                                                                                                                                                       .comment(child.getComment())
                                                                                                                                                                       .price(child.getPrice())
                                                                                                                                                                       .build())
                                                                                                                                   .toList())
                                                                                                     .build())
                                                                .toList();

            // 하위 옵션까지 담긴 메뉴를 데이터에 담은 뒤 그 데이터를 리스트에 add
            OrderMenuGetRes orderMenu = OrderMenuGetRes.builder()
                                                       .menuId(menu.getId())
                                                       .storeId(menu.getStore().getId())
                                                       .name(menu.getName())
                                                       .price(menu.getPrice())
                                                       .imagePath(menu.getImagePath())
                                                       .options(options)
                                                       .build();

            result.add(orderMenu);
        }

        return result;
    }
}
