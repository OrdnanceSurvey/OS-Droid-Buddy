package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity;

import twitter4j.TwitterException;

public class TwitterCredentials {

    private long mId;
    private String mToken;
    private String mSecret;
    private TwitterException mException;

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setException(TwitterException exception) {
        mException = exception;
    }

    public TwitterException getException() {
        return mException;
    }
}
