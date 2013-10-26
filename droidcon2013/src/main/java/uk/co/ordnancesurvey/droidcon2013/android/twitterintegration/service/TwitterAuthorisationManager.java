package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service;

import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;


public  class       TwitterAuthorisationManager
        implements  TwitterTokenProviderImpl.TokenCallBack,
                    TwitterVerificationProvider.TwitterVerificationCallBack {

    public interface TwitterAuthorisationManagerCallBack {
        public void tokenEmitted(RequestToken token);
        public void credentialsVerified(TwitterCredentials credentials);
    }

    private TwitterTokenProvider mTokenProvider;
    private TwitterVerificationProvider mVerificationProvider;

    private TwitterAuthorisationManagerCallBack mCallBack;

    private RequestToken mRequestToken;
    private boolean mIsRequesting = false;

    public TwitterAuthorisationManager(TwitterTokenProvider tokenProvider,
                                       TwitterVerificationProvider verificationProvider) {

        if(tokenProvider == null){
            throw new IllegalArgumentException("Null Token Provider");
        }

        if(verificationProvider == null){
            throw new IllegalArgumentException("Null Verification Provider");
        }

        mTokenProvider = tokenProvider;
        mVerificationProvider = verificationProvider;
    }

    public void setCallBack(TwitterAuthorisationManagerCallBack callBack){
        mCallBack = callBack;
    }

    public Configuration generateConfiguration(String key, String secret) {

        if(key == null){
            throw new IllegalArgumentException("Null Public Key");
        }

        if(secret == null){
            throw new IllegalArgumentException("Null Public Secret");
        }

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setOAuthConsumerKey(key);
        builder.setOAuthConsumerSecret(secret);

        return builder.build();
    }

    public void generateRequestToken(Configuration configuration, String referrer){

        mIsRequesting = true;

        mTokenProvider.generateRequestToken(configuration, referrer, this);
    }

    public boolean isRequestingToken(){
        return mIsRequesting;
    }

    public RequestToken getRequestToken(){
        return mRequestToken;
    }

    public void verifyCredentials(Configuration configuration,  RequestToken token, String oauthVerifier){

        mVerificationProvider.validateCredentials(configuration, token, oauthVerifier, this);
    }

    @Override
    public void onTokenRequestComplete(RequestToken token) {

        mIsRequesting = false;

        mRequestToken = token;

        if(mCallBack != null){
            mCallBack.tokenEmitted(mRequestToken);
        }
    }

    @Override
    public void onVerificationComplete(TwitterCredentials credentials) {

        if(credentials == null){
            throw new IllegalArgumentException("Null credentials");
        }

        if(mCallBack != null){
            mCallBack.credentialsVerified(credentials);
        }
    }
}
