package uk.co.ordnancesurvey.droidcon2013.android.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JReeves on 14/10/13.
 */
public class GeoTweetContract {

    public static final String AUTHORITY = "uk.co.ordnancesurvey.droidcon2013";

    public static interface TweetColumns {
        public static final String AUTHOR = "author";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String SOURCE = "source";
        public static final String TIME = "time";
        public static final String TWEET = "tweet";
        public static final String TWEET_ID = "tweet_id";
    }

    public static class Tweets implements BaseColumns, TweetColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tweets");

        public static final String CONTENT_TYPE
                = "vnd.android.cursor.dir/vnd.ordnancesurveydroidcon.tweet";

        public static final String CONTENT_ITEMTYPE
                = "vnd.android.cursor.item/vnd.ordnancesurveydroidcon.tweet";

        public static class Table {
            public static final String NAME = "tweets";

            public interface Sql {
                public static final String CREATE = "CREATE TABLE " + NAME + " (" + Tweets._ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + Tweets.AUTHOR + " STRING, "
                        + Tweets.LATITUDE + " DOUBLE, "
                        + Tweets.LONGITUDE + " DOUBLE, "
                        + Tweets.SOURCE + " STRING,"
                        + Tweets.TIME + " LONG,"
                        + Tweets.TWEET + " STRING,"
                        + Tweets.TWEET_ID + " LONG);";
            }
        }
    }
}
