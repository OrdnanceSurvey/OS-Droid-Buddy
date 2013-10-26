package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.ordnancesurvey.droidcon2013.android.R;
import uk.co.ordnancesurvey.droidcon2013.android.content.TwitterCredentialsStorageImpl;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app.TwitterAuthorisationPresenter;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app.TwitterAuthorisationPresenterImpl;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app.TwitterAuthorisationView;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterAuthorisationManager;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterTokenProviderImpl;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterVerificationProviderImpl;

public class TwitterAuthorisationFragment extends Fragment implements TwitterAuthorisationView {

    public interface TwitterAuthorisationFragmentCallback{
		public abstract void onAuthorisationSuccess();
	}

    private static final String OAUTH_VERIFIER = "oauth_verifier";

    private TwitterAuthorisationFragmentCallback mCallBack;
    private String mReferrer;

    private TwitterAuthorisationPresenter mPresenter;

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
        	mCallBack = (TwitterAuthorisationFragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TwitterAuthorisationFragmentCallback");
        }
    }
//
	@Override
    public void onSaveInstanceState(Bundle outState) {
		outState.putString( TwitterAuthorisationFragment.class.getSimpleName(),
                            TwitterAuthorisationFragment.class.getSimpleName());

		super.onSaveInstanceState(outState);
    }
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_twitterauthorisation, container, false);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

        // Set up presenter
        TwitterAuthorisationManager manager = new TwitterAuthorisationManager( new TwitterTokenProviderImpl(),
                new TwitterVerificationProviderImpl());

        mPresenter = new TwitterAuthorisationPresenterImpl(manager, new TwitterCredentialsStorageImpl(getActivity()));
	}

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.onViewSet(this);

        mReferrer = getString(R.string.twitter_public_referrer);

        mPresenter.onViewResumed(   getString(R.string.twitter_public_key),
                                    getString(R.string.twitter_public_secret),
                                    mReferrer );
    }

    @Override
    public void onPause() {
        super.onPause();

        mPresenter.onViewPaused();
    }

    // TWITTER AUTHORISATION VIEW INTERFACE METHODS
    @Override
    public void showProcessingFeedback(boolean show) {

        if(getView() != null){

            displayWebView(false);

            setFeedback(getString(R.string.twitter_processing_access));

            showFeedback(true, true);
        }
    }

    @Override
    public void showErrorFeedback(boolean show) {

        if(getView() != null) {

            displayWebView(false);

            setFeedback(getString(R.string.twitter_authorisation_failure));

            showFeedback(true, false);
        }
    }

    @Override
    public void showTwitterAuthorisation(String authorisationURL) {

        if(getView() != null){

            displayWebView(true);

            showFeedback(false, false);

            loadAuthorisationView(authorisationURL);
        }
    }

    @Override
    public void showAuthorisationSuccess() {

        if(mCallBack != null){
            mCallBack.onAuthorisationSuccess();
        }
    }

    @Override
    public void showAuthorisationFailure() {

        if(getView() != null) {

            displayWebView(false);

            setFeedback(getString(R.string.twitter_authorisation_failure));

            showFeedback(true, false);
        }
    }
	
	public boolean canWebViewGoBack(){

		WebView webView = (WebView)getView().findViewById(R.id.fragment_twitter_authorisation_webview);

		return webView.canGoBack();
	}

    private void displayWebView(boolean display) {

        if(getView() != null) {

            WebView webView = (WebView)getView().findViewById(R.id.fragment_twitter_authorisation_webview);

            if(webView != null) {
                webView.setVisibility(display ? View.VISIBLE : View.INVISIBLE);
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
    }

    private void setFeedback(String feedback){

        if(getView() != null) {

            TextView feedbackView = (TextView) getView()
                                            .findViewById(R.id.fragment_twitter_authorisation_txt_feedback);

            if(feedbackView != null) {
                feedbackView.setText(feedback);
            }
        }
    }

    private void showFeedback(boolean display, boolean isProgress) {

        if(getView() != null) {

            View progressLayout = getView().findViewById(R.id.fragment_twitter_authorisation_layout_progress);

            if(progressLayout != null){
                progressLayout.setVisibility(display ? View.VISIBLE : View.INVISIBLE);
            }

            View progress = getView().findViewById(R.id.fragment_twitter_authorisation_progress);

            if(progress != null) {
                progress.setVisibility(isProgress ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    private void loadAuthorisationView(String authorisationURL){

        if(getView() != null) {

            WebView webView = (WebView)getView().findViewById(R.id.fragment_twitter_authorisation_webview);

            if(webView != null){

                webView.setVisibility(View.VISIBLE);

                webView.setWebViewClient( new WebViewClient(){
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                        if(url.contains(mReferrer)) {

                            view.stopLoading();

                            Uri uri = Uri.parse( url );

                            String oauthVerifier = uri.getQueryParameter(OAUTH_VERIFIER);

                            mPresenter.onVerifyCredentials(oauthVerifier);

                        }else{
                            super.onPageStarted(view, url, favicon);
                        }
                    }
                });

                webView.loadUrl(authorisationURL);
            }
        }
    }
}
