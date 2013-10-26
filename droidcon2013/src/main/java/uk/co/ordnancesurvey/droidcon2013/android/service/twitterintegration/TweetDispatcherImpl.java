package uk.co.ordnancesurvey.droidcon2013.android.service.twitterintegration;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TweetDispatcher;

public class TweetDispatcherImpl implements TweetDispatcher{

    private TweetDispatchCallBack mCallBack;

    @Override
    public void dispatchTweet(Configuration configuration, String tweet, TweetDispatchCallBack callBack) {

        if (configuration == null) {
            throw new IllegalArgumentException("Null Configuration");
        }

        if (tweet == null) {
            throw new IllegalArgumentException("Null Tweet Content");
        }

        mCallBack = callBack;

        new TweetDispatchTask(configuration).execute(tweet);
    }

    public class TweetDispatchTask extends AsyncTask<String, Void, TwitterException> {

        private final Configuration mConfiguration;

        public TweetDispatchTask(Configuration configuration){

            if (configuration == null) {
                throw new IllegalArgumentException("Null Configuration");
            }

            mConfiguration = configuration;
        }

        @Override
        protected void onPostExecute(TwitterException exception) {
            super.onPostExecute(exception);

            if ( mCallBack != null) {
                mCallBack.onTweetDispatched(exception);
            }
        }

        @Override
        protected TwitterException doInBackground(String... strings) {

            final String tweet = strings[0];

            TwitterFactory tf = new TwitterFactory(mConfiguration);

            Twitter twitter = tf.getInstance();

            try {

                twitter.updateStatus(tweet);

            } catch (TwitterException e) {
                Log.e(TweetDispatcherImpl.class.getSimpleName(), "Unable to determine Tweet Status");
                return e;
            }

            return null;
        }
    }
}
