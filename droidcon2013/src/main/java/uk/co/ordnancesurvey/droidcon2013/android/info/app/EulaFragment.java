package uk.co.ordnancesurvey.droidcon2013.android.info.app;

import uk.co.ordnancesurvey.droidcon2013.android.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EulaFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eula, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if( getView() != null) {

            TextView eula = (TextView) getView().findViewById(R.id.fragment_eula_content);

            if(eula != null) {

                eula.setMovementMethod(LinkMovementMethod.getInstance());

                SpannableString message = new SpannableString(getEulaText());
                Linkify.addLinks(message, Linkify.WEB_URLS);

                eula.setText(message);
            }
        }
    }

    /**
     * Gets the EULA text.
     */
    private String getEulaText() {
        String tos = getString(R.string.eula_date) + "\n\n" + getString(R.string.eula_body)
                + "\n\n" + getString(R.string.eula_footer) + "\n\n"
                + getString(R.string.eula_copyright_year);
        return tos;
    }




}
