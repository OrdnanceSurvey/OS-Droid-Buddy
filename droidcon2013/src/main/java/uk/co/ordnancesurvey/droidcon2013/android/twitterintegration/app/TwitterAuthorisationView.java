package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app;

public interface TwitterAuthorisationView {

    public void showProcessingFeedback(boolean show);
    public void showErrorFeedback(boolean show);

    public void showTwitterAuthorisation(String authorisationURL);
    public void showAuthorisationSuccess();
    public void showAuthorisationFailure();

    //public void closeView();
}
