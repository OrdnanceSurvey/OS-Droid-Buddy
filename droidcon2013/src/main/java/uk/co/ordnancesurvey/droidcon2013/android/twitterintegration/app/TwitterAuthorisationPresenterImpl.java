package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app;

import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterAuthorisationManager;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterCredentialsStorage;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;

public class TwitterAuthorisationPresenterImpl implements TwitterAuthorisationPresenter {

    private TwitterAuthorisationView mView;
    private TwitterAuthorisationManager mManager;
    private TwitterCredentialsStorage mStorage;

    private Configuration mConfiguration;

    private boolean mViewOnPause = true;
    private boolean mHasError = false;

    public TwitterAuthorisationPresenterImpl(TwitterAuthorisationManager manager, TwitterCredentialsStorage storage) {

        if(manager == null){
            throw new IllegalArgumentException("Null Authorisation Manager");
        }

        if(storage == null){
            throw new IllegalArgumentException("Null Credentials Storage");
        }

        mManager = manager;
        mManager.setCallBack(this);

        mStorage = storage;
    }

    @Override
    public void onViewSet(TwitterAuthorisationView view) {

        if (view == null) {
            throw new IllegalArgumentException("View is null.");
        }

        mView = view;
    }

    @Override
    public void onViewResumed(String key, String secret, String referrer){

        if (mView == null) {
            throw new IllegalStateException("View has not been set. Call onViewSet() prior to this");
        }

        mViewOnPause  = false;

        if (mHasError) {

            mView.showProcessingFeedback(false);
            mView.showErrorFeedback(true);

            return;
        }

        if (mManager.getRequestToken() == null) {

            mView.showProcessingFeedback(true);
            mView.showErrorFeedback(false);

            if (!mManager.isRequestingToken()) {

                if (mConfiguration == null) {
                    mConfiguration = mManager.generateConfiguration(key, secret);
                }

                mManager.generateRequestToken(mConfiguration, referrer);

                return;
            }
        }

        tokenEmitted(mManager.getRequestToken());
    }

    @Override
    public void onViewPaused() {
        mViewOnPause  = true;
    }

//    @Override
//    public void onViewCloseRequested() {
//        mView.closeView();
//    }

    @Override
    public void onVerifyCredentials(String oauthVerifier) {

        if (oauthVerifier == null) {
            throw new IllegalStateException("Null OAuth Verifier");
        }

        if (mConfiguration == null) {
            throw new IllegalStateException("Null configuration");
        }

        if (mManager.getRequestToken() == null) {
            throw new IllegalStateException("Null Request Token");
        }

        mView.showProcessingFeedback(true);

        mManager.verifyCredentials(mConfiguration, mManager.getRequestToken(), oauthVerifier);
    }

    @Override
    public void tokenEmitted(RequestToken token) {

        if (token == null) {

            mHasError = true;

            if (!mViewOnPause) {

                mView.showProcessingFeedback(false);
                mView.showErrorFeedback(true);
            }

            return;
        }

        mHasError = false;

        if (!mViewOnPause) {

            mView.showProcessingFeedback(false);
            mView.showErrorFeedback(false);

            mView.showTwitterAuthorisation(token.getAuthorizationURL());
        }
    }

    @Override
    public void credentialsVerified(TwitterCredentials credentials) {

        if (credentials == null) {
            mView.showAuthorisationFailure();
            return;
        }

        if (mStorage.storeCredentials(credentials)){
            mView.showAuthorisationSuccess();
            return;
        }

        mView.showAuthorisationFailure();
    }
}
