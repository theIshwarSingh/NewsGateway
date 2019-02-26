package com.tappy.newsgateway;

import java.io.Serializable;


public class Origin implements Serializable {

    String id;
    String origin1;
    String details;

    public Origin(String id, String name, String desc, String sourceUrl, String category) {
        this.id = id;
        this.origin1 = name;
        this.details = desc;
        this.url = sourceUrl;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return origin1;
    }

    public void setSource(String origin) {
        this.origin1 = origin;
    }

    public String getDescription() {
        return details;
    }

    public void setDescription(String details) {
        this.details = details;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    String url;
    String category;

}
