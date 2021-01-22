package com.gitlab.muhammadkholidb.bianglala.utility;

import com.gitlab.muhammadkholidb.bianglala.constant.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@SuppressWarnings("unchecked")
public class PageData {

    @Getter
    private PageSet pageSet;

    private Object data;

    public static final PageData INSTANCE = new PageData();

    private PageData() {
    }

    public <T> void set(Page from, Page to, T data) {
        set(new PageSet(from, to), data);
    }

    public <T> void set(PageSet pageSet, T data) {
        this.pageSet = pageSet;
        this.data = data;
    }

    public <T> T get(Page from, Page to) {
        return get(new PageSet(from, to));
    }

    public <T> T get(PageSet pageSet) {
        return this.pageSet.equals(pageSet) ? (T) data : null;
    }

    public boolean hasData(Page from, Page to) {
        return hasData(new PageSet(from, to));
    }

    public boolean hasData(PageSet pageSet) {
        return this.pageSet.equals(pageSet) && data != null;
    }

    @AllArgsConstructor
    @Data
    public static class PageSet {

        private Page from;
        private Page to;

        public PageSet swap() {
            Page temp = from;
            from = to;
            to = temp;
            return this;
        }

        public PageSet swapCopy() {
            return new PageSet(to, from);
        }

    }

}
