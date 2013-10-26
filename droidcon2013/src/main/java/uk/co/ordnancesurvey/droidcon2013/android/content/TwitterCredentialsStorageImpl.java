package uk.co.ordnancesurvey.droidcon2013.android.content;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.service.TwitterCredentialsStorage;
import uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity.TwitterCredentials;

public class TwitterCredentialsStorageImpl implements TwitterCredentialsStorage {

    private static final String PREFERENCES_NAME = "uk.co.ordnancesurvey.droidcon2013.android.preferences";

    private static final String TWITTER_ID = "twitter_user_id";
    private static final String ACCESS_TOKEN = "twitter_access_token";
    private static final String ACCESS_SECRET = "twitter_access_secret";

    private final Context mContext;

    public TwitterCredentialsStorageImpl(Context context) {

        if (context == null) {
            throw new IllegalArgumentException("Null context");
        }

        mContext = context;
    }

    @Override
    public boolean storeCredentials(TwitterCredentials credentials) {

        SharedPreferences.Editor editor =
                mContext.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE).edit();

        editor.putLong(TWITTER_ID, credentials.getId());
        editor.putString(ACCESS_TOKEN, credentials.getToken());
        editor.putString(ACCESS_SECRET, credentials.getSecret());

        return editor.commit();
    }

    @Override
    public TwitterCredentials getCredentials() {

        SharedPreferences preferences =
                mContext.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

        TwitterCredentials credentials = new TwitterCredentials();

        credentials.setId(preferences.getLong(TWITTER_ID, 0));
        credentials.setToken(preferences.getString(ACCESS_TOKEN, ""));
        credentials.setSecret(preferences.getString(ACCESS_SECRET, ""));

        return credentials;
    }
}
