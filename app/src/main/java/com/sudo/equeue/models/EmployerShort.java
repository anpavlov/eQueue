package com.sudo.equeue.models;

import java.util.Map;

public class EmployerShort {
    private String name;
    private int id;
    private Map<String, String> logo_urls;

    public Map<String, String> getLogoUrls() {
        return logo_urls;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
