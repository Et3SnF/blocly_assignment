package io.bloc.android.blocly.api.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RssFeed extends Model {

    // Member variables

    private String title;
    private String description;
    private String siteUrl;
    private String feedUrl;

    // Constructor

    public RssFeed(long rowId, String title, String description, String siteUrl, String feedUrl) {
        super(rowId);
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

    // ----- Parcelable Stuff ---- //

        // Store the information here

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(siteUrl);
        dest.writeString(feedUrl);
    }

        // Call back the information using this constructor

    public RssFeed(Parcel source) {
        super(source);
        title = source.readString();
        description = source.readString();
        siteUrl = source.readString();
        feedUrl = source.readString();
    }

    public static final Parcelable.Creator<RssFeed> CREATOR = new Parcelable.Creator<RssFeed>() {

        @Override
        public RssFeed createFromParcel(Parcel source) {
            return new RssFeed(source);
        }

        @Override
        public RssFeed[] newArray(int size) {
            return new RssFeed[size];
        }
    };


}