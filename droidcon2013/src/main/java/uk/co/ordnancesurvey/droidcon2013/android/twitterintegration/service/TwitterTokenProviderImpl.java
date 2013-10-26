package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;

public class TwitterTokenProviderImpl implements TwitterTokenProvider{

    private static final String CLASS_TAG = TwitterTokenProviderImpl.class.getSimpleName();

    private TokenCallBack mCallBack;
    private String mReferrer;

    @Override
    public void generateRequestToken(Configuration configuration, String referrer, TokenCallBack callback){

        if(configuration == null){
            throw new IllegalArgumentException("Null Configuration");
        }

        if(referrer == null){
            throw new IllegalArgumentException("Null Referrer");
        }

        if(callback == null){
            throw new IllegalArgumentException("Null Call back");
        }

        mCallBack = callback;
        mReferrer = referrer;

        new RequestTokenTask().execute(configuration);
    }

    private class RequestTokenTask extends AsyncTask<Configuration, Void, RequestToken> {

        @Override
        protected RequestToken doInBackground(Configuration... args) {

            Configuration configuration = args[0];

            if(configuration != null){

                TwitterFactory factory = new TwitterFactory(configuration);
                Twitter twitter = factory.getInstance();

                try {
                    return twitter.getOAuthRequestToken(mReferrer);
                } catch (TwitterException e) {

                    Log.e(CLASS_TAG, "Unable to get Twitter OAuth token.");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(RequestToken result) {
            super.onPostExecute(result);

            mCallBack.onTokenRequestComplete(result);
        }
    }
}
