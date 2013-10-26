package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.ordnancesurvey.android.maps.*;
import uk.co.ordnancesurvey.droidcon2013.android.R;
import uk.co.ordnancesurvey.droidcon2013.android.content.GeoTweetContract;
import uk.co.ordnancesurvey.droidcon2013.android.widgets.ScaleBarWidget;

public class BuddyMapFragment extends Fragment implements   OSMap.InfoWindowAdapter,
                                                            OSMap.OnCameraChangeListener,
                                                            LoaderManager.LoaderCallbacks<Cursor> {

    private static final float DEFAULT_ZOOM_LEVEL = 3;
    private static final String EXPLORER_TILE = "tq38e.ostiles";
    private static final String LANDRANGER_TILE = "tq38l.ostiles";

    private OSMap mMap;
    private ScaleBarWidget mScale;
    private float mCurrentZoom = DEFAULT_ZOOM_LEVEL;
    private MarkerOptions mDroidCon;
    private MarkerOptions mUser;
    private HashMap<String, MarkerOptions> mTweets;
    private BitmapDescriptor mTweetIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_buddymap, container, false);

        String[] products = new String[] { "50K-660DPI", "25K-660DPI","CS07","CS06","CS05","CS04","CS03","CS02","CS01","CS00" };

        OSMapOptions options = new OSMapOptions().products(products);

        MapFragment mapFragment = new MapFragment(options);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_buddymap_map_container, mapFragment, MapFragment.class.getSimpleName());

        transaction.commit();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScale = (ScaleBarWidget) getView().findViewById(R.id.fragment_buddymap_scale);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MapFragment.class.getSimpleName());

        if(mapFragment != null) {
            mMap = mapFragment.getMap();
        }

        mTweetIcon = BitmapDescriptorFactory.fromResource(R.drawable.twitter_bird_light_bgs);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMap == null) {

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MapFragment.class.getSimpleName());

            if(mapFragment != null) {
                mMap = mapFragment.getMap();
            }
        }

        if(mMap != null){

            // Set up tile sources
            new RenderAssetTask().execute();

            // Setup Map
            mMap.setInfoWindowAdapter(this);
            mMap.setOnCameraChangeListener(this);

            // Set map to Droidcon location with Marker (setting zoom level to 3mpp (metres per pixel))
            // long = 51.535525 lat = -0.104887. Easting and Northings 531538,183549
            GridPoint point = new GridPoint(531538,183549);

            mDroidCon = new MarkerOptions()
            .gridPoint(point)
            .title(getString(R.string.fragment_buddymap_droidcon_title))
            .snippet(getString(R.string.fragment_buddymap_droidcon_snippet));

            mMap.addMarker(mDroidCon);

            getLoaderManager().initLoader(0, null, this);

        }else{
            // TODO: Null map exception
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String projection[] = new String[]{
                GeoTweetContract.Tweets._ID,
                GeoTweetContract.Tweets.AUTHOR,
                GeoTweetContract.Tweets.TWEET,
                GeoTweetContract.Tweets.LATITUDE,
                GeoTweetContract.Tweets.LONGITUDE };

        String restriction = null;
        String[] restrictionArgs = null;
        String sortOrder = GeoTweetContract.Tweets.TWEET_ID + " ASC LIMIT 5";

        CursorLoader loader = new CursorLoader(getActivity(), GeoTweetContract.Tweets.CONTENT_URI,
                projection, restriction, restrictionArgs, sortOrder);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(getView() != null && mMap != null) {

            if(cursor != null && cursor.moveToFirst()) {

                mTweets = new HashMap<String, MarkerOptions>();

                while(!cursor.isAfterLast()) {

                    MarkerOptions marker = new MarkerOptions();

                    double lat = cursor.getDouble(cursor.getColumnIndex(GeoTweetContract.Tweets.LATITUDE));
                    double lng = cursor.getDouble(cursor.getColumnIndex(GeoTweetContract.Tweets.LONGITUDE));

                    marker.icon(mTweetIcon);
                    marker.gridPoint(MapProjection.getDefault().toGridPoint(lat, lng));
                    marker.title(cursor.getString(cursor.getColumnIndex(GeoTweetContract.Tweets.AUTHOR)));
                    final String snippet = cursor.getString(cursor.getColumnIndex(GeoTweetContract.Tweets.TWEET));
                    marker.snippet(snippet);

                    String key = lat + "," + lng;
                    mTweets.put(key, marker);

                    cursor.moveToNext();
                }
            }

            refreshMarkers();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (getView() != null && mMap != null) {

            mMap.clear();
        }
    }

    public void enableMap(boolean enable){

        if (getView() != null) {
            getView().findViewById(R.id.fragment_buddymap_view_overlay)
                    .setVisibility(enable ? View.INVISIBLE : View.VISIBLE);

            enableDisableViewGroup((ViewGroup) getView(), enable);
        }
    }

    public void displayUserLocation(Location location) {

        GridPoint gridPoint = MapProjection.getDefault().toGridPoint(location.getLatitude(),
                location.getLongitude());

        boolean animated = true;

        mUser = new MarkerOptions()
                .gridPoint(gridPoint)
                .title(getString(R.string.fragment_buddymap_user_title))
                .snippet("Lat:" +location.getLatitude() + ", Lng:" + location.getLongitude());

        refreshMarkers();

        mMap.moveCamera(new CameraPosition(gridPoint, mCurrentZoom), animated);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return generateMarkerView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return generateMarkerView(marker);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mCurrentZoom = cameraPosition.zoom;
        mScale.update(cameraPosition.zoom);
    }

    private void setTileSources() {

        if(mMap != null) {

            ArrayList<OSTileSource> sources = new ArrayList<OSTileSource>();

            sources.addAll(mMap.localTileSourcesInDirectory(getActivity(), Environment.getExternalStorageDirectory()));
            sources.add(mMap.webTileSource(getString(R.string.tile_source_key), true, null));

            mMap.setTileSources(sources);
        }
    }

    private void refreshMarkers() {

        mMap.clear();

        mMap.addMarker(mDroidCon);

        if(mUser != null) {
            mMap.addMarker(mUser);
        }

        if(mTweets != null){
            for(MarkerOptions marker : mTweets.values()) {
                mMap.addMarker(marker);
            }
        }
    }

    private void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {

        if(viewGroup != null) {

            int childCount = viewGroup.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View view = viewGroup.getChildAt(i);
                view.setEnabled(enabled);
                if (view instanceof ViewGroup) {
                    enableDisableViewGroup((ViewGroup) view, enabled);
                }
            }
        }
    }

    private View generateMarkerView(Marker marker) {

        GridPoint gp = marker.getGridPoint();

        if (((long)(gp.x/218.75)&1) == 0) {
            return null;
        }

        View view =  getActivity().getLayoutInflater().inflate(R.layout.marker_tweet, null);

        if(view != null) {

            ((TextView)view.findViewById(R.id.marker_title)).setText(marker.getTitle());
            ((TextView)view.findViewById(R.id.marker_snippet)).setText(marker.getSnippet());
        }

        return view;
    }

    public void showPlacemark(Placemark placemark) {

        if(mMap != null) {

            MarkerOptions marker = new MarkerOptions()
                    .gridPoint(placemark.getPosition())
                    .title(placemark.getName())
                    .snippet(placemark.getPosition().toString());

            mMap.addMarker(marker);
        }
    }

    private class RenderAssetTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {

            File explorer = new File(Environment.getExternalStorageDirectory() + "/" + EXPLORER_TILE);
            File landranger = new File(Environment.getExternalStorageDirectory() + "/" + LANDRANGER_TILE);

            if(explorer.exists() && landranger.exists()) {
                return null;
            }

            if (!explorer.exists()) {
                loadTileFromAsset(EXPLORER_TILE, explorer);
            }

            if (!landranger.exists()) {
                loadTileFromAsset(LANDRANGER_TILE, landranger);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            setTileSources();
        }

        private void loadTileFromAsset(String assetName, File outputFile) {

            AssetFileDescriptor descriptor = null;
            try {
                descriptor = getActivity().getAssets().openFd(assetName);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(BuddyMapFragment.class.getSimpleName(), "Unable to open asset: " + assetName);
            }

            if(descriptor == null){
                return;
            }

            InputStream is = null;
            try {
                is = descriptor.createInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(BuddyMapFragment.class.getSimpleName(), "Unable to create input stream: " + assetName);
            }

            try {

                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(buffer);
                fos.close();

            } catch (Exception e) {
                Log.e(BuddyMapFragment.class.getSimpleName(), "Unable to write to file: " + outputFile.getPath());
            }
        }
    }
}
