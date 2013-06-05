package net.senneco.twitley.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by senneco on 22.05.13.
 */
public class Twit implements Serializable {
    private long mId;
    private String mText;
    private User mUser;
    private Date mCreatedAt;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }
}
