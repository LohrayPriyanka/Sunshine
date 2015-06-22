package com.example.android.sunshine;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.net.Uri;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

   public ArrayAdapter<String> mForecastAdapter;
    private final static String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static String[] weatherForecast = {};
    private static ListView listView;
   // private static String location = "";
   // private static String temp_units = "";
    //private static String days = "";
   // private static String[] weatherParams = new String[3];
    private static String[] geoParams = new String[2];

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Add this line in order for this fragment to handle menu events.*/
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] data= {};

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));


        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);


       new FetchWeatherTask().execute("95054");
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);



        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */

         @Override
        protected String[] doInBackground(String... params) {

            /* If there's no zip code, there's nothing to loop up. Verify size of params.*/

            if (params.length == 0) {
                return null;
            }
            /**
             * These two need to be declared outside the try/catch
             * so that they can be closed in the finally block.
             */
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

        /*Will contain the raw JSON response as a string.*/
            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int weekDays = 7;
            try{
           /* Construct the URL for the OpenWeatherMap query
            //// Possible parameters are available at OWM's forecast API page, at
            //// http://openweathermap.org/API#forecast*/
                //final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                //final String APPID = "APPID";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(weekDays))
                        .build();

                String weatherURLBuilder = builder.toString();

                Log.v(LOG_TAG, "Built URI " + weatherURLBuilder);

                /* Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast*/
                URL url = new URL(weatherURLBuilder);
            /* Create the request to OpenWeatherMap, and open the connection*/
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

            /*Read the input stream into a String */
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                /* Nothing to do.*/
                    forecastJsonStr = null;
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
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                //  Log.v(LOG_TAG, "JSON String " + forecastJsonStr);
                WeatherDataJsonParser weatherDataJsonParser = new WeatherDataJsonParser();
                try {
                    Double maxTemperature = weatherDataJsonParser.getMaxTemperatureForDay(forecastJsonStr, 4);
                    geoParams = weatherDataJsonParser.getGeoDetails(forecastJsonStr);
                    Log.v(LOG_TAG, "Maximum Temperature for day " + maxTemperature);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                  try
                  {
                      weatherForecast = getWeatherDataFromJson(forecastJsonStr, weekDays);
                      Log.v(LOG_TAG, "Weather Forecast " + weatherForecast);
                  }
                  catch (JSONException e) {
                      e.printStackTrace();
                  }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;

            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                        Log.v(LOG_TAG, "closing stream");
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return weatherForecast;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.forecastfragment, menu);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Log.d("Action Refresh", ">>>>>>>>>> Action Refresh");
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute("95054");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
        * so for convenience we're breaking it out into its own method now.
        */
    private String getReadableDateString(Date cal) {
            /* Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.*/
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd", Locale.US);
        return shortenedDateFormat.format(cal);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }


    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

            /* These are the names of the JSON objects that need to be extracted.*/
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            /*OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.*/

        // get the supported ids for GMT-05:00 (Eatern Standard Time)
        String[] ids = TimeZone.getAvailableIDs(-5 * 60 * 60 * 1000);
        // if no ids were returned, something is wrong. exit.
        if (ids.length == 0)
            System.exit(0);
        // create a Eastern Standard Time time zone
        SimpleTimeZone est = new SimpleTimeZone(-5 * 60 * 60 * 1000, ids[0]);
        // set up rules for daylight savings time
        est.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        est.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
        // create a GregorianCalendar with the Pacific Daylight time zone
        // and the current date and time
        Calendar calendar = new GregorianCalendar(est);
        Date startDate = calendar.getTime();
        calendar.setTime(startDate);

        Log.v(LOG_TAG, " Start Date : " + Calendar.DAY_OF_MONTH);

        String[] resultStrs = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {
                /* For now, using the format "Day, description, hi/low"*/
            String day;
            String description;
            String highAndLow;

                /* Get the JSON object representing the day*/
            JSONObject dayForecast = weatherArray.getJSONObject(i);

              /*  Log.v(LOG_TAG, i + ".) Date : " + Calendar.DAY_OF_MONTH);

                if(i > 0){*/
            calendar.add(Calendar.DAY_OF_MONTH, 1);


            String dateFormatted = getReadableDateString(calendar.getTime());

                /* description is in a child array called "weather", which is 1 element long.*/
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

                /* Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.*/
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = dateFormatted + " - " + description + " - " + highAndLow;
        }

        for (String s : resultStrs) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }
        return resultStrs;


    }

}