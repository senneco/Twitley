package net.senneco.twitley.data;

import java.io.Serializable;

/**
 * Created by senneco on 22.05.13.
 */
public class User implements Serializable {
    private String mName;
    private String mProfileImageUrl;
    private String mScreenName;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mProfileImageUrl = profileImageUrl;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public void setScreenName(String screenName) {
        mScreenName = screenName;
    }
}
