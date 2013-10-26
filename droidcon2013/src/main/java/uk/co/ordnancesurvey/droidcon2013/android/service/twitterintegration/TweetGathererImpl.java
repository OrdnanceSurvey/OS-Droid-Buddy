package uk.co.ordnancesurvey.droidcon2013.android.service.twitterintegration;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TweetGatherer;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterQueryResult;

public class TweetGathererImpl implements TweetGatherer{

    private TweetGathererCallBack mCallBack;

    @Override
    public void gatherTweets(Configuration configuration, Query query, TweetGathererCallBack callBack) {

        if (configuration == null) {
            throw new IllegalArgumentException("Null Configuration");
        }

        if (query == null) {
            throw new IllegalArgumentException("Null Twitter Query");
        }

        mCallBack = callBack;

        new TweetGatherTask(configuration).execute(query);
    }

    private class TweetGatherTask extends AsyncTask<Query, Void, TwitterQueryResult> {

        private final Configuration mConfiguration;

        public TweetGatherTask(Configuration configuration){

            if (configuration == null) {
                throw new IllegalArgumentException("Null Configuration");
            }

            mConfiguration = configuration;
        }

        @Override
        protected void onPostExecute(TwitterQueryResult results) {
            super.onPostExecute(results);

            if ( mCallBack != null) {
                mCallBack.onTweetsGathered(results);
            }
        }

        @Override
        protected TwitterQueryResult doInBackground(Query... args) {

             final Query query = args[0];

            TwitterFactory tf = new TwitterFactory(mConfiguration);

            Twitter twitter = tf.getInstance();

            TwitterQueryResult results = new TwitterQueryResult();

            try {

                QueryResult queryResult = twitter.search(query);

                results.setTweets(queryResult.getTweets());

            } catch (TwitterException e) {
                Log.e(TweetGathererImpl.class.getSimpleName(), "Unable to search for requested tweets");
                results.setException(e);
            }

            return results;
        }
    }
}
