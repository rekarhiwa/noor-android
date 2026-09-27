package com.dya.noor.activities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class customUtils {
    public static String getCurrentDate() {

        Calendar calendar = Calendar.getInstance(); // Device's timezone


        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault()); // Local format
        // SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.US); // UTC format

        String formattedDate = sdf.format(calendar.getTime());

        Log.d("CalendarDate", "Current date (MM-dd): " + formattedDate);
        return formattedDate;
    }

}