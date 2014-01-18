package com.example.aficionado.data;

import java.util.Date;

/**
 * Created by upopple on 1/18/14.
 */
public class Comment {
    private String mComment;
    private String mName;
    private Date mTime;
    private String mAccessionName;

    public Comment(String accessionName, String mComment, String mName, Date date) {
        this.mComment = mComment;
        this.mName = mName;
        this.mTime = date;
        this.mAccessionName = accessionName;
    }

    public String getComment() {
        return mComment;
    }

    public String getName() {
        return mName;
    }

    public String getAccessionName() {
        return mAccessionName;
    }
}
