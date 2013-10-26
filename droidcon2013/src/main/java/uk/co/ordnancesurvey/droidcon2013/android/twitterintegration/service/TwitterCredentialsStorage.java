package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;

public interface TwitterCredentialsStorage {

    public boolean storeCredentials(TwitterCredentials credentials);
    public TwitterCredentials getCredentials();
}
