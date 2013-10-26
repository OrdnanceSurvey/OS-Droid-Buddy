package uk.co.ordnancesurvey.droidcon2013.android.info.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import uk.co.ordnancesurvey.droidcon2013.android.R;

import java.util.Locale;

public class InfoFragment extends Fragment {

    private static final String ARG_URL = "arg_info_url";
    private String mUrl;

    public static InfoFragment newInstance(String infoUrl) {

        Bundle args = new Bundle();

        args.putString(infoUrl, ARG_URL);

        InfoFragment fragment = new InfoFragment();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(this.ARG_URL)) {
            mUrl = getArguments().getString(this.ARG_URL);
        }

        WebView webView = (WebView) getView().findViewById(R.id.help_webview);

        if(webView != null) {
            String language = Locale.getDefault().getLanguage();

            if (language == null || language.equals("")) {
                language = "en";
            }

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());

            if(mUrl != null) {
                webView.loadUrl(mUrl);
            }
        }
    }
}
