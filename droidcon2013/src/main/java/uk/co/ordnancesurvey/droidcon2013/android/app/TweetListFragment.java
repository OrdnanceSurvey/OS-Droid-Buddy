package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import uk.co.ordnancesurvey.droidcon2013.android.R;
import uk.co.ordnancesurvey.droidcon2013.android.content.GeoTweetContract;
import uk.co.ordnancesurvey.droidcon2013.android.content.sync.SyncUtils;

public class TweetListFragment extends ListFragment {

    private SimpleCursorAdapter mAdapter;

    private static final int LOADER_TWEETS = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String[] from = new String[]{ GeoTweetContract.Tweets.AUTHOR,
                                            GeoTweetContract.Tweets.TWEET };

        final int[] to = new int[]{ R.id.list_tweets_txt_screen_name,
                                    R.id.list_tweets_txt_content };

        final Cursor cursor = null;

        mAdapter = new TweetAdapter(getActivity(),
                R.layout.listitem_tweets, cursor, from, to,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        this.setListAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_TWEETS, null, new LoaderManager.LoaderCallbacks<Cursor>(){
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                String projection[] = new String[]{
                        GeoTweetContract.Tweets._ID,
                        GeoTweetContract.Tweets.AUTHOR,
                        GeoTweetContract.Tweets.TWEET };

                String restriction = null;
                String[] restrictionArgs = null;
                String sortOrder = GeoTweetContract.Tweets._ID + " DESC";

                CursorLoader loader = new CursorLoader(getActivity(), GeoTweetContract.Tweets.CONTENT_URI,
                        projection, restriction, restrictionArgs, sortOrder);
                return loader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                mAdapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        });

        SyncUtils.createSyncAccount(getActivity());
    }

    @Override
    public void onResume() {
        SyncUtils.triggerRefresh();
        super.onResume();
    }

    private class TweetAdapter extends SimpleCursorAdapter {

        public TweetAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView name = (TextView)view.findViewById(R.id.list_tweets_txt_screen_name);
            name.setText(cursor.getString(cursor.getColumnIndex
                    (GeoTweetContract.TweetColumns.AUTHOR)));

            TextView content = (TextView)view.findViewById(R.id.list_tweets_txt_content);
            content.setText(cursor.getString(cursor.getColumnIndex
                    (GeoTweetContract.TweetColumns.TWEET)));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View v = inflater.inflate(R.layout.listitem_tweets, parent, false);
            bindView(v, context, cursor);
            return v;
        }
    }
}
