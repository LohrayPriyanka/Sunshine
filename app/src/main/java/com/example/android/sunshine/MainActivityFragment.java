package com.example.android.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> WeekForecast = new ArrayList<String>();
        WeekForecast.add("Today(Saturday) - Sunny - 91/63");
        WeekForecast.add("Tomorrow - Sunny - 93/63");
        WeekForecast.add("Monday - Cloudy - 72/63");
        WeekForecast.add("Tuesday - Rainy - 74/63");
        WeekForecast.add("Wednesday - Rainy - 71/63");
        WeekForecast.add("Thursday - Cloudy - 88/63");
        WeekForecast.add("Friday - Sunny - 89/63");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                WeekForecast);

        ListView listView = (ListView)
                rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);
        return rootView;
    }
}
