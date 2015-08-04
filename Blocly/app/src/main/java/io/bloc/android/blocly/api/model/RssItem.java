package io.bloc.android.blocly.api.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RssItem extends Model {

    // Member variables

    private String guid;
    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private long rssFeedId;
    private long datePublished;
    private boolean read;
    private boolean favorite;
    private boolean archived;

    // Constructor

    public RssItem(long rowId, String guid, String title, String description, String url, String imageUrl, long rssFeedId, long datePublished, boolean favorite, boolean archived) {
        super(rowId);
        this.guid = guid;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.rssFeedId = rssFeedId;
        this.datePublished = datePublished;
        this.favorite = favorite;
        this.archived = archived;
    }

    // Getters

    public String getGuid() {
        return guid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getRssFeedId() {
        return rssFeedId;
    }

    public long getDatePublished() {
        return datePublished;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isArchived() {
        return archived;
    }

    // ---- Parcelable Area ---- //

        // Store the variables into this method

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(guid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeLong(rssFeedId);
        dest.writeLong(datePublished);
    }

    // Retrieve the variables from this constructor

    public RssItem(Parcel source) {
        super(source);
        guid = source.readString();
        title = source.readString();
        description = source.readString();
        url = source.readString();
        imageUrl = source.readString();
        rssFeedId = source.readLong();
        datePublished = source.readLong();
    }

    public static final Parcelable.Creator<RssItem> CREATOR = new Parcelable.Creator<RssItem>() {

        @Override
        public RssItem createFromParcel(Parcel source) {
            return new RssItem(source);
        }

        @Override
        public RssItem[] newArray(int size) {
            return new RssItem[size];
        }
    };

}
