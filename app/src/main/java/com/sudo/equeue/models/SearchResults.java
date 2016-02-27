package com.sudo.equeue.models;

import java.io.Serializable;
import java.util.List;

public class SearchResults implements Serializable {
    private int per_page;
    private int page;
    private int pages;
    private List<VacancyShort> items;

    public int getPerPage() {
        return per_page;
    }

    public int getPage() {
        return page;
    }

    public int getPages() {
        return pages;
    }

    public List<VacancyShort> getItems() {
        return items;
    }
}
