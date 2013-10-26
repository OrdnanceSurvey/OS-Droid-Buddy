package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.co.ordnancesurvey.droidcon2013.android.R;

public class SplashFragment extends Fragment {

    public interface SplashFragmentCallBack {
        public void onSplashFragmentGone();
    }

    private static final int THREE_SECONDS_IN_MILLIS = 3000;
    private Handler mHandler;

    private SplashFragmentCallBack mCallBack;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        if(!(activity instanceof SplashFragmentCallBack)){
            throw new IllegalStateException("Activity must implement " + SplashFragmentCallBack.class.getSimpleName());
        }

        mCallBack = (SplashFragmentCallBack) activity;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mHandler == null){

            mHandler = new Handler();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mCallBack != null) {
                        mCallBack.onSplashFragmentGone();
                    }
                }
            }, THREE_SECONDS_IN_MILLIS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }
}
