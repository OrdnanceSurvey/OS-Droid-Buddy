package uk.co.ordnancesurvey.droidcon2013.android.app;

public interface MainPresenter {

    public void onViewSet(MainView view);
    public void onViewResumed();
    public void onViewPaused();
    public void onViewCloseRequested();

    public void onDisplaySearchRequested();
    public void onDisplayMapRequested();
    public void onDisplayTweetBoardRequested();
    public void onDisplayNavigationRequested();
    public void onDisplayInformationRequested();
    public void onDisplayAboutRequested();
    public void onDisplayEulaRequested();
}
