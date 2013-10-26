package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import java.util.List;

import twitter4j.Status;

public interface TweetStorage {

    public interface TweetStorageCallBack{
        public void onTweetsStored();
    }

    public void storeTweets(List<Status> tweets, TweetStorageCallBack callBack);
}
