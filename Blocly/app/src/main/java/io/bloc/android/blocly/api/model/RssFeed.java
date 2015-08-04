package io.bloc.android.blocly.api.model;

public class RssFeed {

    // Member variables

    private String title;
    private String description;
    private String siteUrl;
    private String feedUrl;

    // Constructor

    public RssFeed(String title, String description, String siteUrl, String feedUrl) {
        this.title = title;
        this.description = description;
        this.siteUrl = siteUrl;
        this.feedUrl = feedUrl;
    }

    // Getters

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }


}