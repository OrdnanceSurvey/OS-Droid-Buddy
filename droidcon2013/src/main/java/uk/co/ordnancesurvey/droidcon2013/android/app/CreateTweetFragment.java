package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.TwitterException;
import uk.co.ordnancesurvey.droidcon2013.android.R;
import uk.co.ordnancesurvey.droidcon2013.android.content.TweetStorageImpl;
import uk.co.ordnancesurvey.droidcon2013.android.content.TwitterCredentialsStorageImpl;
import uk.co.ordnancesurvey.droidcon2013.android.service.twitterintegration.TweetDispatcherImpl;
import uk.co.ordnancesurvey.droidcon2013.android.service.twitterintegration.TweetGathererImpl;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.GeoTweet;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TweetManager;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app.CreateTweetPresenter;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app.CreateTweetPresenterImpl;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.app.CreateTweetView;

public class CreateTweetFragment extends Fragment implements CreateTweetView {

    private CreateTweetPresenter mPresenter;
    private Location mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new CreateTweetPresenterImpl(
                                new TweetManager(   new TweetDispatcherImpl(),
                                                    new TweetGathererImpl(),
                                                    new TweetStorageImpl(getActivity())),
                                new TwitterCredentialsStorageImpl(getActivity()));
        setDefaultLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_tweet, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.fragment_create_tweet_btn_send_tweet)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendTweet();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.onViewSet(this);

        mPresenter.onViewResumed(getString(R.string.twitter_public_key),
                                 getString(R.string.twitter_public_secret));
    }

    public void updateLocation(Location location){
        mLocation = location;
    }

    @Override
    public void showTweetResult(TwitterException exception) {

        if(exception != null){
            Toast.makeText(getActivity(), "Tweet Sending Failed", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getActivity(), "Tweet Sending Successful", Toast.LENGTH_LONG).show();

        EditText tweetEdit = (EditText) getView()
                .findViewById(R.id.fragment_create_tweet_edit_tweet);

        tweetEdit.setText("");
    }

    private void sendTweet(){

        if(getView() != null) {

            EditText tweetEdit = (EditText) getView()
                    .findViewById(R.id.fragment_create_tweet_edit_tweet);

            String rawTweet = tweetEdit.getText().toString();

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            if(rawTweet != null && rawTweet.length() > 0 && mLocation != null) {

                String tweet = GeoTweet.Util.getAsString(rawTweet,
                        mLocation.getLatitude(), mLocation.getLongitude());

                mPresenter.sendTweet(tweet);
            }else{
                showTweetResult(new TwitterException("Tweet Not in correct format"));
            }
        }
    }

    private void setDefaultLocation() {
        mLocation = new Location("default");
        mLocation.setLatitude(51.535525D);
        mLocation.setLongitude(-0.104887D);
    }
}
