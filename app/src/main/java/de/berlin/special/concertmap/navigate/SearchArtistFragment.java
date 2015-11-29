package de.berlin.special.concertmap.navigate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.service.DataFetchService;

public class SearchArtistFragment extends Fragment {

    private View rootView;
    private Button searchBtn;
    private EditText entryView;

    public SearchArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search_artist, container, false);
        searchBtn = (Button) rootView.findViewById(R.id.artist_search_button);
        entryView = (EditText) rootView.findViewById(R.id.enter_artist_edit_text);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String entry = entryView.getText().toString();
                entry = entry.replaceAll(" ", "+");

                if (entry.lastIndexOf("+") == (entry.length()-1))
                    entry = entry.substring(0, entry.length()-1);

                new DataFetchService(getContext(), rootView, entry, Utility.URL_ARTIST_SEARCH).execute();
            }
        });
    }

}