package com.tappy.newsgateway;

import java.io.Serializable;

public class Items implements Serializable {

    String author;
    String title;
    String detail;
    String url;
    String urlToImage;
    String PubDate;

    public Items(String author,
                 String title,
                 String desc,
                 String urlArticle,
                 String urlImage,
                 String publish) {
        this.author = author;
        this.title = title;
        this.detail = desc;
        this.url = urlArticle;
        this.urlToImage = urlImage;
        this.PubDate = publish;

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return detail;
    }

    public void setDesc(String desc) {
        this.detail = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImg(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPubDate() {
        return PubDate;
    }

    public void setPublishDate(String publishDate) {
        this.PubDate = publishDate;
    }
}
