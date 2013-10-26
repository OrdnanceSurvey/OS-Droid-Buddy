package uk.co.ordnancesurvey.droidcon2013.android.search;

import uk.co.ordnancesurvey.android.maps.Placemark;

import java.util.List;

public class SearchManager implements   PlaceProvider.PlaceProviderCallBack,
                                        PostcodeProvider.PostcodeProviderCallBack {

    public interface SearchManagerCallBack {
        public void onSearchResultsChanged();
    }

    private PlaceProvider mPlaceProvider;
    private PostcodeProvider mPostcodeProvider;

    private List<? extends Placemark> mPlaces;
    private List<? extends Placemark> mPostcodes;

    private SearchManagerCallBack mCallBack;

    public SearchManager (PlaceProvider placeProvider, PostcodeProvider postcodeProvider) {

        mPlaceProvider = placeProvider;
        mPostcodeProvider = postcodeProvider;
    }

    public void setSearchManagerCallBack(SearchManagerCallBack callBack) {
        mCallBack = callBack;
    }

    public void performSearch(String query) {

        if(mPlaceProvider != null){

            mPlaceProvider.requestPlaces(query, this);
        }

        if(mPostcodeProvider != null){

            mPostcodeProvider.requestPostcodes(query, this);
        }
    }

    public List<? extends Placemark> getPlaces(){
        return mPlaces;
    }

    public List<? extends Placemark> getPostcodes(){
        return mPostcodes;
    }

    public void closeSearchManager(){

        if(mPlaceProvider != null){

            mPlaceProvider.destroyProvider();
        }

        if(mPostcodeProvider != null){

            mPostcodeProvider.destroyProvider();
        }
    }

    @Override
    public void onPlacesProvided(List<? extends Placemark> places) {

        mPlaces = places;

        if (mCallBack != null) {
            mCallBack.onSearchResultsChanged();
        }
    }

    @Override
    public void onPostcodesProvided(List<? extends Placemark> postcodes) {

        mPostcodes = postcodes;

        if (mCallBack != null) {
            mCallBack.onSearchResultsChanged();
        }
    }
}
