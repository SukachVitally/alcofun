package com.alco.searchapp.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ListResponseDto<T> {
    private List<T> results;

    private Integer currentPage;

    private Integer pageCount;
}
