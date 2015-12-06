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
import java.util.Date;

import de.berlin.special.concertmap.R;

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

        setDateBtn = (ImageButton) rootView.findViewById(R.id.set_date_button);
        progressBar = (ProgressBar) rootView.findViewById(R.id.parse_data_progress);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        fromDateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar fromCalendar = Calendar.getInstance();
                fromDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar newDate = Calendar.getInstance();
                        view.setMinDate(new Date().getTime());
                        newDate.set(year, monthOfYear, dayOfMonth);
                        fromDateEntry.setText(dateFormatter.format(newDate.getTime()));
                        toDateEntry.requestFocus();
                    }
                }, fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });

        toDateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year;
                int month;
                int day;
                Calendar toCalendar = Calendar.getInstance();
                String fromDate = fromDateEntry.getText().toString();


                if(!fromDate.equals("")) {
                    String[] fromDateArr = fromDateEntry.getText().toString().split("-");
                    year = Integer.valueOf(fromDateArr[0]);
                    month = Integer.valueOf(fromDateArr[1]);
                    day = Integer.valueOf(fromDateArr[2]);
                }else {
                    year = toCalendar.get(Calendar.YEAR);
                    month = toCalendar.get(Calendar.MONTH);
                    day = toCalendar.get(Calendar.DAY_OF_MONTH);
                }

                toDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        toDateEntry.setText(dateFormatter.format(newDate.getTime()));
                        toDateEntry.setFocusable(false);
                    }
                }, year, month-1, day);
                toDatePickerDialog.show();
            }
        });

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }
}