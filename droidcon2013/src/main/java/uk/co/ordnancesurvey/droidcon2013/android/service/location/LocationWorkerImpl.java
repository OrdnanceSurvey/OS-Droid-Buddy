package uk.co.ordnancesurvey.droidcon2013.android.service.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class LocationWorkerImpl implements LocationWorker {

    private static final String TAG = "LocationWorkerImpl";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int ONE_MINUTE = 1000 * 60;
    private static final int NO_MINIMUM_DISTANCE = 0;

   // private final Context mContext;
    private final LocationManager mLocationManager;
    private final LocationCallback mLocationResult = new LocationCallback();
    private final LocationListenerGps mLocationListenerGps = new LocationListenerGps();
    private final LocationListenerNetwork mLocationListenerNetwork = new LocationListenerNetwork();

    private LocationPresenter mPresenter = null;

    public LocationWorkerImpl(LocationManager manager) {

        if (manager == null) {
            throw new IllegalArgumentException("Null location manager");
        }

        mLocationManager = manager;
    }

    @Override
    public void findLocation() {
        Log.d(TAG, "2. findLocation()");
        getLocation();
    }

    public boolean isGpsAvailable() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isNetworkAvailable() {
        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void setPresenter(LocationPresenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Termination lifecycle methods must terminate this object to ensure outstanding requests are
     * cancelled.
     */
    @Override
    public void terminate() {
        mLocationManager.removeUpdates(mLocationListenerGps);
        mLocationManager.removeUpdates(mLocationListenerNetwork);
    }

    private boolean getLocation() {
        List<String> providers = mLocationManager.getProviders(true/*enabled providers*/);

        // I think fire off location requests
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    ONE_MINUTE, NO_MINIMUM_DISTANCE, mLocationListenerGps);
        }

        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    ONE_MINUTE, NO_MINIMUM_DISTANCE, mLocationListenerNetwork);
        }
        return true;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix.
     * Note: needs simplification.
     * Source: http://developer.android.com/guide/topics/location/obtaining-user-location.html
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The best existing fix, which you want to compare with the new fix
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (location == null) {
            // A null location is always worse than a location
            return false;
        }
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        final int accuracyThreshold = 200;
        boolean isSignificantlyLessAccurate = accuracyDelta > accuracyThreshold;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private class LocationListenerGps implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mLocationManager.removeUpdates(this);
            mLocationResult.gotLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    private class LocationListenerNetwork implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mLocationManager.removeUpdates(this);
            mLocationResult.gotLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    private class LocationCallback {

        private Location mBestLocation = null;

        public void gotLocation(Location location) {
            if (isBetterLocation(location, mBestLocation)) {
                mBestLocation = location;
                mPresenter.receivedLocation(location);
            }
        }
    }
}
