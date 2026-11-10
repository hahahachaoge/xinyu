package com.example.web.common;


import lombok.Data;

@Data
public class PagedInput {

    private Long limit;

    private Long page;

    private SortItemDto sortItem;

    private String keyWord;

    public PagedInput() {
        if (limit == null || limit == 0) {
            limit = Long.valueOf(1000);
        }
        if (page == null|| page == 0) {
            page = Long.valueOf(1);
        }
    }
}
