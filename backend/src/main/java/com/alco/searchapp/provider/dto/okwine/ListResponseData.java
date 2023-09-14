package com.alco.searchapp.provider.dto.okwine;

import java.util.List;
import lombok.Data;

@Data
public class ListResponseData {
    private Integer maxPage;

    private List<ListResponseProduct> data;
}
