package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.ordnancesurvey.droidcon2013.android.R;

public class TwitterFragment  extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_twitter, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_twitter_container_create, new CreateTweetFragment(),
                CreateTweetFragment.class.getSimpleName());

        transaction.add(R.id.fragment_twitter_container_view, new TweetListFragment(),
                TweetListFragment.class.getSimpleName());

        transaction.commit();
    }

    public void updateLocation(Location location) {

        CreateTweetFragment tweetFragment = (CreateTweetFragment) getFragmentManager()
                                    .findFragmentByTag(CreateTweetFragment.class.getSimpleName());

        if(tweetFragment != null) {
            tweetFragment.updateLocation(location);
        }
    }
}
