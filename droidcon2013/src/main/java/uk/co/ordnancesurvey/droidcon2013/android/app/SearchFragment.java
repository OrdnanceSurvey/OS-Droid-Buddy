package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import uk.co.ordnancesurvey.android.maps.Geocoder;
import uk.co.ordnancesurvey.android.maps.Placemark;
import uk.co.ordnancesurvey.droidcon2013.android.R;
import uk.co.ordnancesurvey.droidcon2013.android.search.SearchManager;
import uk.co.ordnancesurvey.droidcon2013.android.service.search.PlaceProviderImpl;
import uk.co.ordnancesurvey.droidcon2013.android.service.search.PostCodeProviderImpl;

public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener, SearchManager.SearchManagerCallBack {

    public interface SearchFragmentCallBack {
        public void onPlaceMarkRequested(Placemark placemark);
    }

    private Geocoder.GeocodeType mCurrentType = Geocoder.GeocodeType.OnlineGazetteer;

    private ListView mResults;
    private PlacemarkAdapter mAdapter;

    private SearchManager mManager;
    private SearchFragmentCallBack mCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof SearchFragmentCallBack)){
            throw new IllegalArgumentException("Activity must implement " + SearchFragment.class.getSimpleName());
        }

        mCallBack = (SearchFragmentCallBack) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String api = getString(R.string.tile_source_key);

        mManager = new SearchManager(new PlaceProviderImpl(getActivity(), api),
                new PostCodeProviderImpl(getActivity(), api));

        mManager.setSearchManagerCallBack(this);

        if( getView() != null) {

            getView().findViewById(R.id.fragment_search_filter_place).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSearchType(Geocoder.GeocodeType.OnlineGazetteer);
                    refreshSearch();
                }
            });

            getView().findViewById(R.id.fragment_search_filter_postcode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSearchType(Geocoder.GeocodeType.OnlinePostcode);
                    refreshSearch();
                }
            });

            getView().findViewById(R.id.fragment_search_filter_place).setSelected(true);

            mResults = (ListView) getView().findViewById(R.id.fragment_search_lst_results);
            mResults.setEmptyView(getView().findViewById(R.id.fragment_search_no_results_view));

            mAdapter = new PlacemarkAdapter(getActivity(), android.R.layout.simple_list_item_1);

            mResults.setAdapter(mAdapter);
            mResults.setOnItemClickListener(this);
        }
    }

    @Override
    public void onDestroy() {

        mManager.closeSearchManager();
        mManager = null;

        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mCallBack.onPlaceMarkRequested((Placemark) mResults.getItemAtPosition(i));
    }

    public void performSearch(String query) {

        if(query == null) {
            mAdapter.clear();
            return;
        }

        mManager.performSearch(query);
    }

    private void setSearchType(Geocoder.GeocodeType type) {

        mCurrentType = type;

        View place = getView().findViewById(R.id.fragment_search_filter_place);
        View postcode = getView().findViewById(R.id.fragment_search_filter_postcode);

        switch(mCurrentType) {

            case OnlineGazetteer:

                place.setSelected(true);
                postcode.setSelected(false);

                break;

            case OnlinePostcode:

                place.setSelected(false);
                postcode.setSelected(true);

                break;
        }
    }

    private void refreshSearch() {

        mAdapter.clear();

        switch(mCurrentType) {

            case OnlineGazetteer:

                List<? extends Placemark> places = mManager.getPlaces();

                if(places != null) {
                    mAdapter.addAll(places);
                }
                break;

            case OnlinePostcode:

                List<? extends Placemark> postcodes = mManager.getPostcodes();

                if(postcodes != null) {
                    mAdapter.addAll(postcodes);
                }
                mManager.getPostcodes();
                break;
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchResultsChanged() {
        refreshSearch();
    }

    private class PlacemarkAdapter extends ArrayAdapter<Placemark> {

        class ResultHolder {
            public TextView text;
        }

        public PlacemarkAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            if (rowView == null) {
                LayoutInflater inflater = SearchFragment.this.getActivity().getLayoutInflater();

                rowView = inflater.inflate(android.R.layout.simple_list_item_1, null);
                rowView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                ResultHolder viewHolder = new ResultHolder();
                viewHolder.text = (TextView) rowView.findViewById(android.R.id.text1);
                rowView.setTag(viewHolder);
            }

            ResultHolder holder = (ResultHolder) rowView.getTag();

            Placemark mark = getItem(position);

            if (mark.getCounty() == null) {
                holder.text.setText(mark.getName());
            } else {
                holder.text.setText(mark.getName() + ", " + mark.getCounty());
            }

            return rowView;
        }
    }
}
