package com.sudo.equeue.models;

import java.io.Serializable;
import java.util.Map;

public class Employer implements Serializable {

    private String name;
//    private String type;
    private long id;
    private String site_url;
    private String description;
//    private String branded_description;
//    private String vacancies_url;
//    private boolean trusted;
//    private String alternate_url;
    private Map<String, String> logo_urls;
}
