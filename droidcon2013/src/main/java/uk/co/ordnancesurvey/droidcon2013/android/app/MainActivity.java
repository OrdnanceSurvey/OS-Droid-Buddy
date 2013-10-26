package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import uk.co.ordnancesurvey.android.maps.Placemark;
import uk.co.ordnancesurvey.droidcon2013.android.BuildConfig;
import uk.co.ordnancesurvey.droidcon2013.android.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import uk.co.ordnancesurvey.droidcon2013.android.info.app.AboutFragment;
import uk.co.ordnancesurvey.droidcon2013.android.info.app.EulaFragment;
import uk.co.ordnancesurvey.droidcon2013.android.info.app.InfoFragment;
import uk.co.ordnancesurvey.droidcon2013.android.content.TwitterCredentialsStorageImpl;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;

public class MainActivity extends Activity
                          implements    MainView,
                                        NavigationFragment.NavigationFragmentCallBack,
                                        TwitterAuthorisationFragment
                                                .TwitterAuthorisationFragmentCallback,
                                        SearchFragment.SearchFragmentCallBack,
                                        SplashFragment.SplashFragmentCallBack {

    private static final String CLASS_TAG = MainActivity.class.getSimpleName();

    private MainPresenterImpl mPresenter;
    private Fragment mCurrentFragment;
    private Location mCurrentLocation;

    // ACTIVITY LIFECYCLE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenterImpl((LocationManager)
                getSystemService(Context.LOCATION_SERVICE));

        SearchView search = (SearchView) findViewById(R.id.activity_main_search);

        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onDisplaySearchRequested();
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (BuildConfig.DEBUG) {
                    Log.v(CLASS_TAG, "Submitting geocode query: " + query);
                }

                if (query == null || query.trim().length() == 0) {
                    return true;
                }

                performQuery(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (BuildConfig.DEBUG) {
                    Log.v(CLASS_TAG, "Geocode query text change: " + newText);
                }

                if (newText == null) {
                    return true;
                }

                if (newText.length() == 0) {

                    performQuery(null);
                }
                return true;
            }
        });

        findViewById(R.id.activity_main_img_map)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onDisplayMapRequested();
            }
        });

        findViewById(R.id.activity_main_img_twitter)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onDisplayTweetBoardRequested();
            }
        });

        findViewById(R.id.activity_main_img_overflow)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onDisplayNavigationRequested();
            }
        });

        displaySplashScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.onViewSet(this);
        mPresenter.onViewResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onViewPaused();
    }

    @Override
    public void onBackPressed() {

        if (mCurrentFragment != null) {

            if (mCurrentFragment instanceof TwitterAuthorisationFragment) {

                if (((TwitterAuthorisationFragment) mCurrentFragment).canWebViewGoBack()) {
                    return;
                }
            }

            if (mCurrentFragment instanceof SearchFragment) {

                SearchView searchView = (SearchView) findViewById(R.id.activity_main_search);

                if (searchView != null && !searchView.isIconified()) {
                   searchView.setIconified(true);
                    mPresenter.onDisplayMapRequested();
                    return;
                }
            }

            mPresenter.onDisplayMapRequested();
            return;
        }

        mPresenter.onViewCloseRequested();
    }

    @Override
    public void onLowMemory() {
        Log.v(CLASS_TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Log.v(CLASS_TAG, "onTrimMemory " + level);
        super.onTrimMemory(level);
    }

    @Override
    public void onAuthorisationSuccess() {
        displayTweetBoard();
    }

    @Override
    public void onSplashFragmentGone() {

        if(mCurrentFragment instanceof SplashFragment) {

            getFragmentManager().beginTransaction().setCustomAnimations(0, R.animator.fade_out)
            .remove(mCurrentFragment).commit();
        }

        displayMap();
    }

    @Override
    public void displayMap() {

        if (mCurrentFragment != null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.fade_in_left, R.animator.fade_out_right);
            transaction.remove(mCurrentFragment);

            transaction.commit();

            mCurrentFragment = null;
        }

        enableMap(true);
    }

    @Override
    public void displayLocation(Location location) {

        mCurrentLocation = location;

        if (mCurrentFragment instanceof TwitterFragment) {
            ((TwitterFragment) mCurrentFragment).updateLocation(location);
        }

        BuddyMapFragment mapFragment = (BuddyMapFragment) getFragmentManager()
                .findFragmentById(R.id.activity_main_frg_buddymap);

        if (mapFragment != null && mCurrentFragment == null) {
            mapFragment.displayUserLocation(mCurrentLocation);
        }
    }

    @Override
    public void displayLocationDisabled() {

        Toast.makeText(MainActivity.this, "Please enable the GPS or network location",
                        Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySearchBoard() {

        if (!(mCurrentFragment instanceof SearchFragment)) {

            swapFragment(new SearchFragment());

            enableMap(false);
        }
    }

    public void displaySplashScreen() {

        mCurrentFragment = new SplashFragment();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_container_content, mCurrentFragment)
                .commit();
        enableMap(false);
    }

    @Override
    public void displayTweetBoard() {

        TwitterCredentials credentials =
                new TwitterCredentialsStorageImpl(this).getCredentials();

        Fragment fragment;

        if (credentials != null && credentials.getId() > 0) {

            fragment = new TwitterFragment();

        } else {
            fragment = new TwitterAuthorisationFragment();
        }

        swapFragment(fragment);

        enableMap(false);
    }

    @Override
    public void displayNavigationBoard() {

        if (!(mCurrentFragment instanceof NavigationFragment)) {

            swapFragment(new NavigationFragment());

            enableMap(false);
        }
    }

    @Override
    public void displayInformationBoard() {

        if (!(mCurrentFragment instanceof InfoFragment)) {

            swapFragment(InfoFragment.newInstance(getString(R.string.about_information_url)));

            enableMap(false);
        }
    }

    @Override
    public void displayAboutBoard() {

        if (!(mCurrentFragment instanceof AboutFragment)) {

            swapFragment(new AboutFragment());

            enableMap(false);
        }
    }

    @Override
    public void displayEulaBoard() {

        if (!(mCurrentFragment instanceof EulaFragment)) {

            swapFragment(new EulaFragment());

            enableMap(false);
        }
    }

    @Override
    public void closeView() {
        this.finish();
    }

    @Override
    public void onInformationRequested() {
        mPresenter.onDisplayInformationRequested();
    }

    @Override
    public void onAboutRequested() {
        mPresenter.onDisplayAboutRequested();
    }

    @Override
    public void onEulaRequested() {
        mPresenter.onDisplayEulaRequested();
    }

    @Override
    public void onClearNavigationRequested() {
        mPresenter.onDisplayMapRequested();
    }

    @Override
    public void onPlaceMarkRequested(Placemark placemark) {
        displayMap();

        BuddyMapFragment mapFragment = (BuddyMapFragment) getFragmentManager()
                .findFragmentById(R.id.activity_main_frg_buddymap);

        if (mapFragment != null) {
            mapFragment.showPlacemark(placemark);
        }
    }

    private void swapFragment(Fragment fragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.animator.fade_in_left, R.animator.fade_out_right);
        transaction.replace(R.id.activity_main_container_content, fragment,
                fragment.getClass().getName());

        transaction.commit();

        mCurrentFragment = fragment;
    }

    private void enableMap(boolean enable) {

        BuddyMapFragment mapFragment = (BuddyMapFragment) getFragmentManager()
                .findFragmentById(R.id.activity_main_frg_buddymap);

        if (mapFragment != null) {
            mapFragment.enableMap(enable);
        }
    }

    private void performQuery(String query) {

        if (mCurrentFragment instanceof SearchFragment) {

            ((SearchFragment) mCurrentFragment).performSearch(query);

        } else {
            mPresenter.onDisplaySearchRequested();
        }
    }
}
