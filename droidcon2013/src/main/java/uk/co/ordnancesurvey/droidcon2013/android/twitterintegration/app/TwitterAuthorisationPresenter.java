package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app;

import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterAuthorisationManager;

public  interface   TwitterAuthorisationPresenter
        extends     TwitterAuthorisationManager.TwitterAuthorisationManagerCallBack {

    public void onViewSet(TwitterAuthorisationView view);
    public void onViewResumed(String key, String secret, String referrer);
    public void onViewPaused();

    public void onVerifyCredentials(String oauthVerifier);

}
