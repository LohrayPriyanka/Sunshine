package com.example.android.sunshine;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            /**
             * These two need to be declared outside the try/catch
             * so that they can be closed in the finally block.
             */
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

        /*Will contain the raw JSON response as a string.*/
            String forecastJsonStr =  null;

            try

            {
           /* Construct the URL for the OpenWeatherMap query
            //// Possible parameters are available at OWM's forecast API page, at
            //// http://openweathermap.org/API#forecast*/
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

            /* Create the request to OpenWeatherMap, and open the connection*/
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

            /*Read the input stream into a String */
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                /* Nothing to do.*/
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                /* Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.*/
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                /* Stream was empty.  No point in parsing.*/
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (
                    IOException e
                    )

            {
                Log.e(LOG_TAG, "Error ", e);
            /* If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.*/
                return null;
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }
}