package kr.co.hanipactor.openfegin.review.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewGetRatingRes {
    private Long id;        // 리뷰 ID
    private Long storeId;   // 가게 ID
    private int rating;     // 별점
    private String content; // 리뷰 내용
    private Long userId;    // 작성자 ID
}
