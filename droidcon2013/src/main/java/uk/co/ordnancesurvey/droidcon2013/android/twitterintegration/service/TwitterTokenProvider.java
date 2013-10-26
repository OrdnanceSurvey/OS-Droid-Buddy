package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;

public interface TwitterTokenProvider {

    public interface TokenCallBack{
        public abstract void onTokenRequestComplete(RequestToken token);
    }

    public void generateRequestToken(Configuration configuration, String referrer, TokenCallBack callback);
}
