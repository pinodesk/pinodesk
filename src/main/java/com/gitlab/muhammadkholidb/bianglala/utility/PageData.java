package com.gitlab.muhammadkholidb.bianglala.utility;

import com.gitlab.muhammadkholidb.bianglala.constant.Page;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("unchecked")
public class PageData {

    private final Map<PageSet, Object> map = new HashMap<>();

    public static final PageData INSTANCE = new PageData();

    private PageData() {}

    public <T> void set(Page from, Page to, T data) {
        PageData.this.set(new PageSet(from, to), data);
    }

    public <T> void set(PageSet pageSet, T data) {
        map.clear(); // The map should only contains one value
        map.put(pageSet, data);
    }

    public <T> T get(Page from, Page to) {
        return PageData.this.get(new PageSet(from, to));
    }

    public <T> T get(PageSet pageSet) {
        return (T) map.get(pageSet);
    }

    @AllArgsConstructor
    @Data
    public static class PageSet {

        private Page from;
        private Page to;
    }

}
