package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import twitter4j.TwitterException;
import twitter4j.conf.Configuration;

public interface TweetDispatcher {

    public interface TweetDispatchCallBack {
        public void onTweetDispatched(TwitterException exception);
    }

    public void dispatchTweet(Configuration configuration, String tweet, TweetDispatchCallBack callBack);
}
