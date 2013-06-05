package net.senneco.twitley.request;

/**
 * Created by senneco on 21.05.13.
 */
public abstract class Urls {
    public static final String BASE_URL = "https://api.twitter.com/";

	public static final String OBTAIN_TOKEN_URL = BASE_URL + "oauth2/token";
    public static final String USER_TIMELINE_URL = BASE_URL + "1.1/statuses/user_timeline.json?screen_name=AndroidDev";
    public static final String USER_TIMELINE_WITH_MAX_ID_URL = USER_TIMELINE_URL + "&max_id=%d";
}
