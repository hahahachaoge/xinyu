package com.example.web.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PagedResult<T> {

    private Long totalCount;

    private List<T> items;

    public static <T> PagedResult<T> getInstance(List<T> items, Long totalCount) {
        PagedResult<T> tPagedResult = new PagedResult<>();
        tPagedResult.setItems(items);
        tPagedResult.setTotalCount(totalCount);
        return tPagedResult;
    }

    public static <T> PagedResult<T> getEmptyInstance() {
        PagedResult<T> tPagedResult = new PagedResult<>();
        tPagedResult.setItems(new ArrayList<>());
        tPagedResult.setTotalCount(0L);
        return tPagedResult;
    }
}
