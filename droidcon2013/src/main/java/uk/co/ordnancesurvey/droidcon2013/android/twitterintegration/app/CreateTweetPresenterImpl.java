package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app;

import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TweetManager;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterCredentialsStorage;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;

public class CreateTweetPresenterImpl implements CreateTweetPresenter{

    private TweetManager mTweetManager;
    private TwitterCredentialsStorage mStorage;

    private Configuration mConfiguration;

    private CreateTweetView mView;

    public CreateTweetPresenterImpl(TweetManager manager, TwitterCredentialsStorage storage) {

        if(manager == null){
            throw new IllegalArgumentException("Null TweetManager");
        }

        if(storage == null){
            throw new IllegalArgumentException("Null Credentials Storage");
        }

        mTweetManager = manager;
        mTweetManager.setCallBack(this);

        mStorage = storage;
    }

    @Override
    public void onViewSet(CreateTweetView view) {

        if(view == null){
            throw new IllegalArgumentException("View is null.");
        }

        mView = view;
    }

    @Override
    public void onViewResumed(String key, String secret) {

        if(mView == null){
            throw new IllegalStateException("View has not been set. Call onViewSet() prior to this");
        }

        TwitterCredentials credentials = mStorage.getCredentials();

        mConfiguration = mTweetManager.generateConfiguration(key, secret, credentials);
    }


    @Override
    public void sendTweet(String tweet) {

        if(mConfiguration == null){
            throw new IllegalStateException("Twitter Configuration error - " +
                    "call onViewResumed() prior to this");
        }

        mTweetManager.sendTweet(mConfiguration, tweet);
    }

    @Override
    public void onTweetRequestComplete(TwitterException exception) {

        mView.showTweetResult(exception);
    }
}
