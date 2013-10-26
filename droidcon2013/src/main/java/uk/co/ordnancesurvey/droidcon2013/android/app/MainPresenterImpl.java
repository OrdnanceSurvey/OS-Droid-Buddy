package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.location.Location;
import android.location.LocationManager;

import uk.co.ordnancesurvey.droidcon2013.android.service.location.LocationPresenter;
import uk.co.ordnancesurvey.droidcon2013.android.service.location.LocationWorkerImpl;

    public class MainPresenterImpl implements MainPresenter, LocationPresenter {

    private MainView mView;

    LocationManager mManager;
    private LocationWorkerImpl mLocationWorker;

    private boolean mViewOnPause = true;

    public MainPresenterImpl(LocationManager manager){

        if(manager == null) {
            throw new IllegalArgumentException("Null Location Manager");
        }

        mManager = manager;
    }

    @Override
    public void onViewSet(MainView view) {

        if(view == null){
            throw new IllegalArgumentException("View is null.");
        }

        mView = view;
    }

    @Override
    public void onViewResumed() {

        mViewOnPause = false;

        if(mView == null){
            throw new IllegalStateException("View has not been set. Call onViewSet() prior to this");
        }

        mLocationWorker = new LocationWorkerImpl(mManager);
        mLocationWorker.setPresenter(this);

        mLocationWorker.findLocation();
    }

    @Override
    public void onViewPaused() {

        mViewOnPause = true;

        mLocationWorker.terminate();
        mLocationWorker = null;
    }

    @Override
    public void onViewCloseRequested() {
        mView.closeView();
    }

    @Override
    public void onDisplaySearchRequested() {

        if(!mViewOnPause) {
            mView.displaySearchBoard();
        }
    }

    @Override
    public void onDisplayMapRequested() {

        if(!mViewOnPause) {

            mView.displayMap();

            if (!mLocationWorker.isGpsAvailable() &&
                    !mLocationWorker.isNetworkAvailable()) {

                mView.displayLocationDisabled();
                return;
            }

            mLocationWorker.findLocation();
        }
    }

    @Override
    public void onDisplayTweetBoardRequested() {

        if(!mViewOnPause) {
            mView.displayTweetBoard();
        }
    }

    @Override
    public void onDisplayNavigationRequested() {

        if(!mViewOnPause) {
            mView.displayNavigationBoard();
        }
    }

    @Override
    public void onDisplayInformationRequested() {

        if(!mViewOnPause) {
            mView.displayInformationBoard();
        }
    }

    @Override
    public void onDisplayAboutRequested() {

        if(!mViewOnPause) {
            mView.displayAboutBoard();
        }
    }

    @Override
    public void onDisplayEulaRequested() {

        if(!mViewOnPause) {
            mView.displayEulaBoard();
        }
    }

    @Override
    public void receivedLocation(Location location) {

        if (location != null && !mViewOnPause) {
            mView.displayLocation(location);
        }
    }
}
