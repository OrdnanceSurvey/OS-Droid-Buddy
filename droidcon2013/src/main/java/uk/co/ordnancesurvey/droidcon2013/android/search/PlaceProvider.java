package uk.co.ordnancesurvey.droidcon2013.android.search;

import java.util.List;

import uk.co.ordnancesurvey.android.maps.Placemark;

public interface PlaceProvider {

    public interface PlaceProviderCallBack {
        public void onPlacesProvided(List<? extends Placemark> places);
    }

    public void requestPlaces(String query, PlaceProviderCallBack callBack);
    public void destroyProvider();
}
