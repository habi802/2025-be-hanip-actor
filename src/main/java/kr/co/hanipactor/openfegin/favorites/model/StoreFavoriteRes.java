package kr.co.hanipactor.openfegin.favorites.model;

import lombok.Getter;

@Getter
public class StoreFavoriteRes {
    private Long storeId;
    private int favoriteCount; // 좋아요 갯수
    private int isFavorite; // 좋아요 여부
}
