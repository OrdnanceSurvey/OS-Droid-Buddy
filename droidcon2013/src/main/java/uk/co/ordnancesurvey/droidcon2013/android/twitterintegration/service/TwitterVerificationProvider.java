package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;

public interface TwitterVerificationProvider {

    public interface TwitterVerificationCallBack{
        public abstract void onVerificationComplete(TwitterCredentials credentials);
    }

    public void validateCredentials(Configuration configuration,  RequestToken token, String oauthVerifier, TwitterVerificationCallBack callBack);
}
