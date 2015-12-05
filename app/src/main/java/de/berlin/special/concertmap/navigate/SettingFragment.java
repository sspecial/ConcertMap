package de.berlin.special.concertmap.navigate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.city.CityActivity;

public class SettingFragment extends Fragment {

    private final int CASE_CHANGE_LOCATION = 0;
    private final int CASE_CHANGE_DATE = 1;
    private final int CASE_EVENT_NUMBER = 2;

    private SharedPreferences settings;
    private View rootView;
    private LayoutInflater layoutInflater;
    private ListView settingsList;
    private SettingAdapter myAdapter;
    private String[] items = new String[] { "Change your location", "Set time duration", "Set number of events"};

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getContext().getSharedPreferences(Utility.PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        rootView = layoutInflater.inflate(R.layout.fragment_setting, container, false);

        settingsList = (ListView) rootView.findViewById(R.id.list_view_settings);

        myAdapter = new SettingAdapter(getContext(), items);

        settingsList.setAdapter(myAdapter);

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case CASE_EVENT_NUMBER: {

                        final NumberPicker np = (NumberPicker) layoutInflater.inflate(R.layout.dialog_number_picker, null);
                        np.setMaxValue(40);
                        np.setMinValue(10);
                        int defValue = settings.getInt(Utility.SETTING_EVENT_NUMBER, 0);
                        np.setValue(defValue);
                        np.setWrapSelectorWheel(false);

                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog));
                        builder.setTitle("Set number of events")
                                .setView(np)
                                .setPositiveButton("Set",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                settings.edit().putInt(Utility.SETTING_EVENT_NUMBER, np.getValue()).commit();
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    }

                    case CASE_CHANGE_LOCATION: {

                        Intent intent = new Intent(getActivity(), CityActivity.class);
                        getActivity().startActivity(intent);
                        break;
                    }

                    case CASE_CHANGE_DATE: {

                    }
                }
            }
        });

        return rootView;
    }
}

class SettingAdapter extends BaseAdapter {

    private Context context;
    private String[] items;
    private int[] images = {R.drawable.geo_fence, R.drawable.planner, R.drawable.list};

    public SettingAdapter(Context context, String[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.custom_settings_row, viewGroup, false);

        TextView titleTextView = (TextView) row.findViewById(R.id.row_textView);
        ImageView titleImageView = (ImageView) row.findViewById(R.id.row_imageView);
        titleTextView.setText(items[i]);
        titleImageView.setImageResource(images[i]);
        return row;
    }
}