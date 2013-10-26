package uk.co.ordnancesurvey.droidcon2013.android.info.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.ordnancesurvey.droidcon2013.android.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if( getView() != null) {

            TextView version = (TextView) getView().findViewById(R.id.fragment_about_version);

            try {
                PackageInfo pi = getActivity().getPackageManager()
                        .getPackageInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);

                version.setText(pi.versionName);

            } catch (PackageManager.NameNotFoundException e) {

                Log.w(AboutFragment.class.getSimpleName(), "Failed to get version info.", e);
                version.setText("");
            }
        }
    }
}
