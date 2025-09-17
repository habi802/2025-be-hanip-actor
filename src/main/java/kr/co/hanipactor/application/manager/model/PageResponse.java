package kr.co.hanipactor.application.manager.model;

import java.util.List;

public record PageResponse<T> (List<T> content, // 데이터
                               long totalRow, // 총 데이터 개수
                               int totalPage, // 전체 페이지 수
                               int pageSize, // 페이지 사이즈
                               int pageNumber // 현재 페이지
                                ) {}
