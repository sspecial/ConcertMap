package de.berlin.special.concertmap.settings;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.service.DataFetchService;
import de.berlin.special.concertmap.util.Utility;

public class TimeFragment extends Fragment {

    private View rootView;
    private EditText fromDateEntry;
    private EditText toDateEntry;
    private ImageButton setDateBtn;
    private ProgressBar progressBar;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    public TimeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_time, container, false);

        fromDateEntry = (EditText) rootView.findViewById(R.id.from_date_edit_text);
        fromDateEntry.setInputType(InputType.TYPE_NULL);
        fromDateEntry.requestFocus();

        toDateEntry = (EditText) rootView.findViewById(R.id.to_date_edit_text);
        toDateEntry.setInputType(InputType.TYPE_NULL);

        if (Utility.MIN_DATE != null && Utility.MAX_DATE != null) {
            fromDateEntry.setText(Utility.simpleDate(Utility.MIN_DATE));
            toDateEntry.setText(Utility.simpleDate(Utility.MAX_DATE));
        } else {
            fromDateEntry.setText(Utility.MIN_DATE_DEFAULT());
            toDateEntry.setText(Utility.MAX_DATE_DEFAULT());
        }

        setDateBtn = (ImageButton) rootView.findViewById(R.id.set_date_button);
        progressBar = (ProgressBar) rootView.findViewById(R.id.parse_data_progress);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // from Date
        fromDateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                fromDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar today = Calendar.getInstance();
                        Calendar nextYear = Calendar.getInstance();
                        nextYear.add(Calendar.YEAR, 1);

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);

                        // If the picked day is between today till next year
                        if (newDate.compareTo(today) != -1 && newDate.compareTo(nextYear) == -1) {

                            fromDateEntry.setText(dateFormatter.format(newDate.getTime()));
                            Utility.MIN_DATE.setTime(newDate.getTime());

                            if (Utility.MAX_DATE.compareTo(Utility.MIN_DATE) != 1) {
                                newDate.add(Calendar.DAY_OF_YEAR, 1);
                                toDateEntry.setText(dateFormatter.format(newDate.getTime()));
                                Utility.MAX_DATE.setTime(newDate.getTime());
                            }
                        }
                        toDateEntry.requestFocus();
                    }
                }, Utility.MIN_DATE.get(Calendar.YEAR), Utility.MIN_DATE.get(Calendar.MONTH), Utility.MIN_DATE.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });

        // to Date
        toDateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar nextYear = Calendar.getInstance();
                        nextYear.add(Calendar.YEAR, 1);

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);

                        if(newDate.compareTo(Utility.MIN_DATE) == 1
                                && newDate.get(Calendar.DAY_OF_YEAR) != Utility.MIN_DATE.get(Calendar.DAY_OF_YEAR)
                                && newDate.compareTo(nextYear) == -1) {
                            toDateEntry.setText(dateFormatter.format(newDate.getTime()));
                            Utility.MAX_DATE.setTime(newDate.getTime());
                        }
                        toDateEntry.setFocusable(false);

                    }
                }, Utility.MAX_DATE.get(Calendar.YEAR), Utility.MAX_DATE.get(Calendar.MONTH), Utility.MAX_DATE.get(Calendar.DAY_OF_MONTH));
                toDatePickerDialog.show();
            }
        });

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDateBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                // Fetching data from Thrillcall API based on Geo information
                new DataFetchService(getActivity(), rootView, Utility.URL_GEO_EVENTS).execute();
            }
        });
    }
}