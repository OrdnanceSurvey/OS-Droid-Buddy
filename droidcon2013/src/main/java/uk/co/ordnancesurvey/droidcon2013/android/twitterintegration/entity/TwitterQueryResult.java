package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import uk.co.ordnancesurvey.droidcon2013.android.content.GeoTweetContract;

public class TwitterQueryResult {

    private List<Status> mTweets;
    private TwitterException mException;

    public void setTweets(List<Status> tweets) {
        mTweets = tweets;
    }

    public List<Status> getTweets() {
        return mTweets;
    }

    public void setException(TwitterException exception) {
        mException = exception;
    }

    public TwitterException getException() {
        return mException;
    }
}
