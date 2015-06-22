package com.example.android.sunshine;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by priyanka on 6/21/2015.
 */
public class WeatherDataJsonParser {

    /**
     +     * Given a string of the form returned by the api call:
     +     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     +     * retrieve the maximum temperature for the day indicated by dayIndex
     +     * (Note: 0-indexed, so 0 would refer to the first day).
     +     */

                private final static String LOG_TAG = WeatherDataJsonParser.class.getSimpleName();
       private final static String weatherJsonArrayListTag = "list";
       private final static String weatherJsonObjectTemp = "temp";
       private final static String weatherJsonSearchKeyAttribute = "max";
       private static Double maxTemperatureArray = 0.00;
       private final static String weatherJsonCityTag = "city";
       private final static String weatherJsonCityName = "name";
       private final static String weatherJsonCountryName = "country";

    public static String[] getGeoDetails(String weatherJsonStr)
                throws JSONException{
                //define the storage for return values
                       String [] geoDetails = new String[2];

                        JSONObject weatherObject = new JSONObject(weatherJsonStr);
               //Get the JSON Array List attribute "city"
                        JSONObject weatherCityObject = weatherObject.getJSONObject(weatherJsonCityTag);
                if(weatherCityObject != null && weatherCityObject.length() > 0){
                        geoDetails[0] = weatherCityObject.getString(weatherJsonCityName);
                        geoDetails[1] = weatherCityObject.getString(weatherJsonCountryName);
                   }


                                return geoDetails;

                    }
                public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException{

                        //Log.v(LOG_TAG, " weatherJsonStr - " + weatherJsonStr);
                                //Get the JSON payload
                                       JSONObject weatherObject = new JSONObject(weatherJsonStr);
               //Get the JSON Array List attribute "list"
                       JSONArray weatherDataList = weatherObject.getJSONArray(weatherJsonArrayListTag);
               //Log.v(LOG_TAG, " weatherJsonStr List - " + weatherDataList.toString());
                        //Iterate the JSON Array List
                                if(weatherDataList != null && weatherDataList.length() > 0){
                        for(int i = 0; i < weatherDataList.length(); i++){
                               //Get the JSON Object for each day, identified by index 0..6
                                       JSONObject weatherDayObject = weatherDataList.getJSONObject(i);
                                //Log.v(LOG_TAG, weatherDataList.getString(i).toString());
                                      if(i == dayIndex){
                                      //Get the max temperature attribute
                                               JSONObject weatherTempAttribute = weatherDayObject.getJSONObject(weatherJsonObjectTemp);
                                  maxTemperatureArray = weatherTempAttribute.getDouble(weatherJsonSearchKeyAttribute);
                    //Log.v(LOG_TAG, "Maximum Temperature : " + maxTemperatureArray);
                                               break;
                                   }
                            }//end of for loop
                    }//check for null

                        //Alternate Solution:
                                /*
        JSONObject weatherObject = new JSONObject(weatherJsonStr);
        JSONArray weatherDataList = weatherObject.getJSONArray(weatherJsonArrayList);
        JSONObject weatherDayObject = weatherDataList.getJSONObject(dayIndex);
        JSONObject weatherTempAttribute = weatherDayObject.getJSONObject(weatherJsonObjectTemp);
       return weatherTempAttribute.getDouble("max");
        */


                                                       return maxTemperatureArray;
            }
}
