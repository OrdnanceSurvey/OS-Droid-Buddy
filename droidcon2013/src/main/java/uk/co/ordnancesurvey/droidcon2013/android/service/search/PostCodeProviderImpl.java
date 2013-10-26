package uk.co.ordnancesurvey.droidcon2013.android.service.search;

import android.content.Context;
import android.os.AsyncTask;

import java.util.EnumSet;

import uk.co.ordnancesurvey.android.maps.FailedToLoadException;
import uk.co.ordnancesurvey.android.maps.Geocoder;
import uk.co.ordnancesurvey.droidcon2013.android.search.PostcodeProvider;

public class PostCodeProviderImpl implements PostcodeProvider {

    private static final EnumSet<Geocoder.GeocodeType> POSTCODE
            = EnumSet.of(Geocoder.GeocodeType.OnlinePostcode);

    private Geocoder mGeocoder;
    private PostcodeProviderCallBack mCallBack;

    public PostCodeProviderImpl(Context context, String apiKey) {

        if (apiKey == null) {
            throw new IllegalArgumentException("Null API Key");
        }

        try {
            mGeocoder = new Geocoder(null, apiKey, context, true);
        } catch (FailedToLoadException e) {
            e.printStackTrace();
            // Ignore as this is for offline coders
        }
    }

    @Override
    public void requestPostcodes(String query, PostcodeProviderCallBack callBack) {

        mCallBack = callBack;

        new PostCodeQueryTask().execute(query);
    }

    @Override
    public void destroyProvider() {

        if (mGeocoder != null) {
            mGeocoder.close();
            mGeocoder = null;
        }
    }

    class PostCodeQueryTask extends AsyncTask<String, Void, Geocoder.Result> {

        @Override
        protected Geocoder.Result doInBackground(String... queries) {

            if (queries.length == 0) {
                return null;
            }

            String query = queries[0];

            if (query == null) {
                return null;
            }

            query = query.trim();

            if (query.length() == 0) {
                return null;
            }

            return mGeocoder.geocodeString(query, POSTCODE, null, 0, 15);
        }

        @Override
        protected void onPostExecute(Geocoder.Result results) {

            if (results == null) {
                return;
            }

            if(mCallBack != null) {
                mCallBack.onPostcodesProvided(results.getPlacemarks());
            }
        }
    }
}
