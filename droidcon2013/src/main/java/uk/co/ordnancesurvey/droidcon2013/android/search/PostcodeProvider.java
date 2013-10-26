package uk.co.ordnancesurvey.droidcon2013.android.search;

import java.util.List;

import uk.co.ordnancesurvey.android.maps.Placemark;

public interface PostcodeProvider {

    public interface PostcodeProviderCallBack {
        public void onPostcodesProvided(List<? extends Placemark> postcodes);
    }

    public void requestPostcodes(String query, PostcodeProviderCallBack callBack);
    public void destroyProvider();
}
