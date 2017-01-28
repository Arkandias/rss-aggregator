package com.abd_lillah.ilyes.rssaggregator;

/**
 * Created by ilyes on 28/01/2017.
 */
public class RssFeedModel {

    public String title;
    public String link;
    public String description;

    public RssFeedModel(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }
}