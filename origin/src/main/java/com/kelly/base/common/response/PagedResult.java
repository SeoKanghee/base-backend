package com.kelly.base.common.response;

import java.util.List;

/**
 * 페이지네이션 결과 공통 클래스
 *
 * @param <T>           응답 데이터 타입
 * @param content       응답 데이터 목록
 * @param totalElements 전체 아이템 개수
 * @param totalPages    페이지 사이즈에 따른 전체 페이지 개수
 * @param currentPage   현재 페이지 인덱스
 * @param pageSize      페이지별 아이템 개수
 * @param hasNext       다음 페이지 존재 여부
 * @param hasPrevious   이전 페이지 존재 여부
 * @author 서강희
 */
public record PagedResult<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {
}
