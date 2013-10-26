package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;

public class TwitterVerificationProviderImpl implements TwitterVerificationProvider {

    private static final String CLASS_TAG = TwitterVerificationProviderImpl.class.getSimpleName();

    private TwitterVerificationCallBack mCallBack;

    @Override
    public void validateCredentials(Configuration configuration, RequestToken token, String oauthVerifier, TwitterVerificationCallBack callBack) {

        if(configuration == null){
            throw new IllegalArgumentException("Null Configuration");
        }

        if(token == null){
            throw new IllegalArgumentException("Null RequestToken");
        }

        if(oauthVerifier == null){
            throw new IllegalArgumentException("Null oAuthVerifier");
        }

        if(callBack == null){
            throw new IllegalArgumentException("Null Call back");
        }

        mCallBack = callBack;

        new VerifyCredentialsTask(configuration, token).execute(oauthVerifier);
    }

    private class VerifyCredentialsTask extends AsyncTask<String, Void, TwitterCredentials> {

        private final Configuration mConfiguration;

        private RequestToken mToken;

        public VerifyCredentialsTask(Configuration configuration, RequestToken token){

            if(configuration == null){
                throw new IllegalArgumentException("Null Configuration");
            }

            mConfiguration = configuration;

            mToken = token;
        }

        @Override
        protected TwitterCredentials doInBackground(String... args) {

            final String oauthVerifier = args[0];

            TwitterCredentials credentials = new TwitterCredentials();

            AccessToken accessToken = null;

            TwitterFactory factory = new TwitterFactory(mConfiguration);
            Twitter twitter = factory.getInstance();

            while (accessToken == null) {

                Log.e(CLASS_TAG, "Attempting OAuth Twitter Access Token Authorisation");

                try{
                    accessToken = twitter.getOAuthAccessToken(mToken, oauthVerifier);

                    twitter.setOAuthAccessToken(accessToken);

                } catch (TwitterException e) {

                    Log.e(CLASS_TAG, "Unable to get Twitter access token.");

                    credentials.setException(e);

                    return credentials;
                }
            }

            User user;

            try {
                user = twitter.verifyCredentials();
            } catch (TwitterException e) {

                Log.e(CLASS_TAG, "Unable to verify Twitter credentials.");

                credentials.setException(e);

                return credentials;
            }

            if(user != null){

                credentials.setId(user.getId());

                credentials.setToken(accessToken.getToken());
                credentials.setSecret(accessToken.getTokenSecret());
            }

            return credentials;
        }

        @Override
        protected void onPostExecute(TwitterCredentials credentials) {
            super.onPostExecute(credentials);

            if(mCallBack != null){
                mCallBack.onVerificationComplete(credentials);
            }
        }
    }
}
