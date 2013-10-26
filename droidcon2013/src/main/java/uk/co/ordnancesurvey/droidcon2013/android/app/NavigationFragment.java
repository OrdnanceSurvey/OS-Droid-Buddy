package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import uk.co.ordnancesurvey.droidcon2013.android.R;

public class NavigationFragment extends Fragment implements AdapterView.OnItemClickListener {

    public interface NavigationFragmentCallBack{
        public void onInformationRequested();
        public void onAboutRequested();
        public void onEulaRequested();
        public void onClearNavigationRequested();
    }

    private static final int INFORMATION = 1;
    private static final int ABOUT = 2;
    private static final int EULA = 3;

    private NavigationFragmentCallBack mCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(!(activity instanceof NavigationFragmentCallBack)){
            throw new IllegalStateException("Activity must implement " + NavigationFragmentCallBack.class.getSimpleName());
        }

        mCallBack = (NavigationFragmentCallBack ) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NavigationItem[] items = new NavigationItem[]{
                new NavigationItem(INFORMATION, "OS Openspace for Android", R.drawable.ic_information),
                new NavigationItem(ABOUT, "About", R.drawable.ic_action_info),
                new NavigationItem(EULA, "End User Agreement", R.drawable.ic_information)
        };

        NavigationAdapter adapter = new NavigationAdapter(getActivity(),
                R.layout.listitem_navigation, items);

        ListView listView = (ListView) getView().findViewById(R.id.fragment_navigation_lst);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        int resId = (Integer) view.getTag();

        switch(resId){
            case INFORMATION:
                if (mCallBack != null) {
                    mCallBack.onInformationRequested();
                }
                break;
            case ABOUT:
                if (mCallBack != null) {
                    mCallBack.onAboutRequested();
                }
                break;
            case EULA:
                if (mCallBack != null) {
                    mCallBack.onEulaRequested();
                }
                break;
            default:
                if (mCallBack != null) {
                    mCallBack.onClearNavigationRequested();
                }
        }
    }

    private class NavigationAdapter extends ArrayAdapter<NavigationItem> {

        private NavigationItem[] mItems;

        public NavigationAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public NavigationAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public NavigationAdapter(Context context, int textViewResourceId, NavigationItem[] objects) {
            super(context, textViewResourceId, objects);
            mItems = objects;
        }

        public NavigationAdapter(Context context, int resource, int textViewResourceId, NavigationItem[] objects) {
            super(context, resource, textViewResourceId, objects);
            mItems = objects;
        }

        public NavigationAdapter(Context context, int textViewResourceId, List<NavigationItem> objects) {
            super(context, textViewResourceId, objects);
            mItems = objects.toArray(new NavigationItem[objects.size()]);
        }

        public NavigationAdapter(Context context, int resource, int textViewResourceId, List<NavigationItem> objects) {
            super(context, resource, textViewResourceId, objects);
            mItems = objects.toArray(new NavigationItem[objects.size()]);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = ((LayoutInflater)getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.listitem_navigation, null);
            }

            ((ImageView) convertView.findViewById(R.id.listitem_navigation_img_icon))
                    .setImageResource(mItems[position].getResourceId());

            ((TextView) convertView.findViewById(R.id.listitem_navigation_txt_name))
                    .setText(mItems[position].getName());

            convertView.setTag(mItems[position].getRequest());

            return convertView;
        }
    }

    private class NavigationItem {

        private int mRequest;
        private String mName;
        private int mResId;

        public NavigationItem(int request, String name, int resId){
            mRequest = request;
            mName = name;
            mResId = resId;
        }

        public int getRequest() {
            return mRequest;
        }

        public String getName(){
            return mName;
        }

        public int getResourceId(){
            return mResId;
        }
    }
}
