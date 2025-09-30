package kr.co.hanipactor.configuration.utils;

import kr.co.hanipactor.configuration.constants.ConstFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImgUploadManager {
    private final ConstFile constFile;
    private final MyFileUtils myFileUtils;

    // 유저 이미지 경로 만들기
    private String makeUserDirectoryPath(long userId) {
        return String.format("%s/%s/%d", constFile.getUploadDirectory(), constFile.getUserPic(), userId);
    }

    // 가게 이미지 경로 만들기
    private String makeStoreDirectoryPath(long storeId) {
        return String.format("%s/%s/%d", constFile.getUploadDirectory(), constFile.getStorePic(), storeId);
    }

    // 가게 배너 이미지 경로 만들기
    private String makeStoreBannerDirectoryPath(long storeId) {
        return String.format("%s/%s/%d", constFile.getUploadDirectory(), constFile.getStoreBannerPic(), storeId);
    }

    // 가게 메뉴 이미지 경로 만들기
    private String makeStoreMenuDirectoryPath(long storeId, long menuId) {
        return String.format("%s/%s/%d/%s/%d", constFile.getUploadDirectory(), constFile.getStorePic(), storeId, constFile.getStoreMenuPic(), menuId);
    }

    // 유저 이미지 저장
    public String saveUserPic(long userId, MultipartFile profilePicFile) {
        // 폴더 생성
        String directory = makeUserDirectoryPath(userId);
        myFileUtils.makeFolders(directory);

        String randomFileName = myFileUtils.makeRandomFileName(profilePicFile);
        String savePath = directory + "/" + randomFileName;

        try {
            myFileUtils.transferTo(profilePicFile, savePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 저장에 실패하였습니다.");
        }
        return randomFileName;
    }

    // 가게 이미지 저장
    public String saveStorePic(long storeId, MultipartFile profilePicFile) {
        // 폴더 생성
        String directory = makeStoreDirectoryPath(storeId);
        myFileUtils.makeFolders(directory);

        String randomFileName = myFileUtils.makeRandomFileName(profilePicFile);
        String savePath = directory + "/" + randomFileName;

        try {
            myFileUtils.transferTo(profilePicFile, savePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "가게 이미지 저장에 실패하였습니다.");
        }
        return randomFileName;
    }

    // 가게 배너 이미지 저장
    public String saveStoreBannerPic(long storeId, MultipartFile profilePicFile) {
        // 폴더 생성
        String directory = makeStoreBannerDirectoryPath(storeId);
        myFileUtils.makeFolders(directory);

        String randomFileName = myFileUtils.makeRandomFileName(profilePicFile);
        String savePath = directory + "/" + randomFileName;

        try {
            myFileUtils.transferTo(profilePicFile, savePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "가게 이미지 저장에 실패하였습니다.");
        }
        return randomFileName;
    }

    // 가게 메뉴 이미지 저장
    public String saveStoreMenuPic(long storeId, long menuId, MultipartFile profilePicFile) {
        // 폴더 생성
        String directory = makeStoreMenuDirectoryPath(storeId, menuId);
        myFileUtils.makeFolders(directory);

        String randomFileName = myFileUtils.makeRandomFileName(profilePicFile);
        String savePath = directory + "/" + randomFileName;

        try {
            myFileUtils.transferTo(profilePicFile, savePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "가게 메뉴 이미지 저장에 실패하였습니다.");
        }
        return randomFileName;
    }

    // 유저 이미지 폴더 삭제
    public void removeUserDirectory(long userId) {
        String directory = makeStoreDirectoryPath(userId);
        myFileUtils.deleteFolder(directory, true);
    }

    // 가게 이미지 폴더 삭제
    public void removeStoreDirectory(long storeId) {
        String directory = makeStoreDirectoryPath(storeId);
        myFileUtils.deleteFolder(directory, true);
    }

    // 가게 배너 이미지 폴더 삭제
    public void removeStoreBannerDirectory(long storeId) {
        String directory = makeStoreDirectoryPath(storeId);
        myFileUtils.deleteFolder(directory, true);
    }

    // 가게 메뉴 이미지 폴더 삭제
    public void removeMenuDirectory(long storeId, long menuId) {
        String directory = makeStoreMenuDirectoryPath(storeId, menuId);
        myFileUtils.deleteFolder(directory, true);
    }
}