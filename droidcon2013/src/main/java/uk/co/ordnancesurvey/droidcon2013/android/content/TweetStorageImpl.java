package uk.co.ordnancesurvey.droidcon2013.android.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import twitter4j.Status;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.GeoTweet;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TweetStorage;

public class TweetStorageImpl implements TweetStorage {

    private final Context mContext;

    public TweetStorageImpl(Context context) {

        if(context == null) {
            throw new IllegalArgumentException("Null Context");
        }

        mContext = context;
    }

    @Override
    public void storeTweets(List<Status> tweets, TweetStorageCallBack callBack) {

        if(tweets != null) {

            for(Status tweet : tweets) {

                long id = tweet.getId();

                Cursor row = mContext.getContentResolver()
                        .query( GeoTweetContract.Tweets.CONTENT_URI,
                                new String[]{GeoTweetContract.Tweets.TWEET_ID},
                                GeoTweetContract.Tweets.TWEET_ID + " = " + id, null, null);

                int count = 0;

                if(row != null){
                    count = row.getCount();
                    row.close();
                }

                if(count == 0){

                    GeoTweet geoTweet = GeoTweet.Util.getAsGeoTweet(tweet.getText());

                    // If tweet matching hashtag is not a geotweet then continue
                    if (geoTweet == null) {
                        continue;
                    }

                    ContentValues values = new ContentValues();

                    values.put(GeoTweetContract.TweetColumns.TWEET_ID, tweet.getId());
                    values.put(GeoTweetContract.TweetColumns.AUTHOR, tweet.getUser().getScreenName());
                    values.put(GeoTweetContract.TweetColumns.SOURCE, tweet.getSource());
                    values.put(GeoTweetContract.TweetColumns.TIME, tweet.getCreatedAt().getTime());
                    values.put(GeoTweetContract.TweetColumns.TWEET, geoTweet.getTweet());
                    values.put( GeoTweetContract.TweetColumns.LATITUDE,
                            geoTweet.getLatitude());
                    values.put( GeoTweetContract.TweetColumns.LONGITUDE,
                            geoTweet.getLongitude());

                    mContext.getContentResolver().insert(GeoTweetContract.Tweets.CONTENT_URI, values);
                }
            }
        }

        callBack.onTweetsStored();
    }
}
