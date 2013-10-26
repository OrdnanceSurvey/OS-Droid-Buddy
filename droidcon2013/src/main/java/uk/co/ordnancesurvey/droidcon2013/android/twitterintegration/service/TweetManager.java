package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import twitter4j.Query;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterQueryResult;

public class TweetManager implements TweetDispatcher.TweetDispatchCallBack, TweetGatherer.TweetGathererCallBack, TweetStorage.TweetStorageCallBack {

    public interface TweetManagerCallBack {
        void onTweetRequestComplete(TwitterException exception);
    }

    private final TweetDispatcher mDispatcher;
    private final TweetGatherer mGatherer;
    private final TweetStorage mStorage;

    private TweetManagerCallBack mCallBack;

    public TweetManager(TweetDispatcher dispatcher, TweetGatherer gatherer, TweetStorage storage) {

        if (dispatcher == null) {
            throw new IllegalArgumentException("Null Dispatcher");
        }

        if (gatherer == null) {
            throw new IllegalArgumentException("Null Gatherer");
        }

        if (storage == null) {
            throw new IllegalArgumentException("Null Storage");
        }

        mDispatcher = dispatcher;
        mGatherer = gatherer;
        mStorage = storage;
    }

    public void setCallBack(TweetManagerCallBack callBack){
        mCallBack = callBack;
    }

    public Configuration generateConfiguration(String key, String secret, TwitterCredentials credentials) {

        if (key == null) {
            throw new IllegalArgumentException("Null Consumer Key");
        }

        if (secret == null) {
            throw new IllegalArgumentException("Null Consumer Secret");
        }

        if (credentials == null) {
            throw new IllegalArgumentException("Null Twitter Credentials");
        }

        ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setOAuthConsumerKey(key)
			  .setOAuthConsumerSecret(secret)
			  .setOAuthAccessToken(credentials.getToken())
			  .setOAuthAccessTokenSecret(credentials.getSecret());

        return cb.build();
    }

    public void sendTweet(Configuration configuration, String tweet) {

        if (configuration == null) {
            throw new IllegalArgumentException("Null configuration");
        }

        if (tweet == null) {
            throw new IllegalArgumentException("Null tweet content");
        }

        mDispatcher.dispatchTweet(configuration, tweet, this);
    }

    public void downloadLatestTweetsWithHashTag(Configuration configuration, String hashTag, long lastID){

        if (configuration == null) {
            throw new IllegalArgumentException("Null configuration");
        }

        Query query = new Query();

        if (hashTag != null) {
            query.setQuery(hashTag);
        }

        if(lastID > 0){
            query.setSinceId(lastID);
        }

        mGatherer.gatherTweets(configuration, query, this);
    }

    @Override
    public void onTweetDispatched(TwitterException exception) {

        if (mCallBack != null) {
            mCallBack.onTweetRequestComplete(exception);
        }
    }

    @Override
    public void onTweetsGathered(TwitterQueryResult result) {

        if(result.getException() != null){
            // Todo: Exception
            return;
        }

        mStorage.storeTweets(result.getTweets(), this);
    }

    @Override
    public void onTweetsStored() {

    }
}
