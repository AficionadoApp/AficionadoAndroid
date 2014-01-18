package com.example.aficionado.data;

/**
 * Created by upopple on 1/18/14.
 */
public class MuseumItem {
    private Comment[] mComments;
    private String mTitle;
    private String mImageUrl;

    public MuseumItem(String title, String imageUrl, Comment[] comments) {
        this.mTitle = title;
        mComments = comments;
        this.mImageUrl = imageUrl;
    }

    public Comment[] getComments() {
        return mComments;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }
}
