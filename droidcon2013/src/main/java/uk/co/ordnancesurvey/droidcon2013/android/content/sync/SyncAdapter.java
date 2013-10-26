package uk.co.ordnancesurvey.droidcon2013.android.content.sync;

import android.accounts.Account;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.R;
import uk.co.ordnancesurvey.droidcon2013.android.content.GeoTweetContract;
import uk.co.ordnancesurvey.droidcon2013.android.content.TweetStorageImpl;
import uk.co.ordnancesurvey.droidcon2013.android.content.TwitterCredentialsStorageImpl;
import uk.co.ordnancesurvey.droidcon2013.android.service.twitterintegration.TweetDispatcherImpl;
import uk.co.ordnancesurvey.droidcon2013.android.service.twitterintegration.TweetGathererImpl;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.GeoTweet;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TweetManager;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterCredentialsStorage;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        Log.d("SyncAdapter RUN", "SyncAdapter");
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        Log.d("SyncAdapter RUN", "SyncAdapter");
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        // Note: this runs on a background thread

        // Data transfer code goes here...
        Log.d("SyncAdapter RUN", "Performing sync...");

        TwitterCredentialsStorage storage = new TwitterCredentialsStorageImpl(getContext());

        TweetManager manager =  new TweetManager(
                                        new TweetDispatcherImpl(),
                                        new TweetGathererImpl(),
                                        new TweetStorageImpl(getContext()));

        Configuration configuration = manager.generateConfiguration(
                                            getContext().getString(R.string.twitter_public_key),
                                            getContext().getString(R.string.twitter_public_secret),
                                            storage.getCredentials());

        Cursor cursor = getContext().getContentResolver().query(
                                        GeoTweetContract.Tweets.CONTENT_URI,
                                        new String[]{ GeoTweetContract.TweetColumns.TWEET_ID },
                                        null,
                                        null,
                                        GeoTweetContract.TweetColumns.TWEET_ID + " DESC LIMIT 1");

        long lastId =0;

        if(cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getLong(cursor.getColumnIndex(GeoTweetContract.TweetColumns.TWEET_ID));
        }

        cursor.close();

        manager.downloadLatestTweetsWithHashTag(configuration,
                                                GeoTweet.Util.getHashtag(),
                                                lastId);
    }
}
