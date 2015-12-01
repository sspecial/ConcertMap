package de.berlin.special.concertmap.navigate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.city.CityActivity;

public class SettingFragment extends Fragment
        implements Preference.OnPreferenceChangeListener {

    private SharedPreferences settings;
    private View rootView;
    private LayoutInflater layoutInflater;
    private final int CASE_LOCATION_CHANGE = 0;
    private final int CASE_EVENT_NUMBER = 1;
    ListView settingsList;
    String[] items = new String[] { "Change your location", "Set number of events"};

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
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_expandable_list_item_1, android.R.id.text1, items);

        settingsList.setAdapter(adapter);

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
                                                int number = settings.getInt(Utility.SETTING_EVENT_NUMBER, 0);
                                                Toast.makeText(getContext(),"value is :" + number, Toast.LENGTH_SHORT).show();
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
                    case CASE_LOCATION_CHANGE: {

                        Intent intent = new Intent(getActivity(), CityActivity.class);
                        getActivity().startActivity(intent);
                        break;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}