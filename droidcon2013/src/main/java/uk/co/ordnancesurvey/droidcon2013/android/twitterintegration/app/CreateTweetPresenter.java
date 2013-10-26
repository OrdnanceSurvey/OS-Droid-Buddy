package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app;

import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TweetManager;

public interface  CreateTweetPresenter extends TweetManager.TweetManagerCallBack {

    public void onViewSet(CreateTweetView view);
    public void onViewResumed(String key, String secret);

    public void sendTweet(String tweet);
}
