package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import twitter4j.Query;
import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterQueryResult;

public interface TweetGatherer {

    public interface TweetGathererCallBack {
        public void onTweetsGathered(TwitterQueryResult result);
    }

    public void gatherTweets(Configuration configuration, Query query, TweetGathererCallBack callBack);
}
