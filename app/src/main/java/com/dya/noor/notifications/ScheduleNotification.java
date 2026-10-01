package com.dya.noor.notifications;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import static com.dya.noor.activities.MainActivity.getLastThirdStart;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.dya.noor.database.MydbClass;
import com.dya.noor.activities.Khatm;
import com.dya.noor.activities.customUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleNotification {

    public static String PreferencesName = "NotificationPrefs";
    private static final int NOTIFICATION_DELAY_AFTER = 15 * 60 * 1000; // 15 minutes after adhan
    private static final int NOTIFICATION_DELAY_BEFORE = -15 * 60 * 1000; // 15 minutes before adhan

    /**
     * Schedules a generic notification.
     * Fix includes correct permission check for Android 12+ and a fallback to inexact alarms.
     */
    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleNotification(Context context, int requestCode, int hour, int minute, String title, String message) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the alarm time is in the past, set it for the next day.
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        // Check for permission starting from Android 12 (API 31)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                // Permission is granted, schedule the exact alarm
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                // Permission is not granted. Use an inexact alarm as a fallback to prevent crashing.
                Log.w("ScheduleNotification", "SCHEDULE_EXACT_ALARM permission not granted. Using inexact alarm as fallback.");
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            // For Android 11 and below, no special permission is needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    /**
     * Reads user preferences and schedules Khatm notifications accordingly.
     */
    public static void scheduleKhatmNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Khatm.KHATM_PREFS, MODE_PRIVATE);

        boolean afterPrayer = sharedPreferences.getBoolean("switchAfterPrayer", false);
        boolean beforePrayer = sharedPreferences.getBoolean("switchBeforePrayer", false);
        boolean afterIsha = sharedPreferences.getBoolean("switchAfterIsha", false);

        cancelKhatmNotifications(context); // Clear old notifications before setting new ones

        if (afterPrayer || beforePrayer || afterIsha) {
            SharedPreferences sharedPreferencesCity = context.getSharedPreferences("key", MODE_PRIVATE);
            String city = sharedPreferencesCity.getString("City", "Kalar");

            MydbClass mydb = new MydbClass(context);
            String currentDate = customUtils.getCurrentDate();
            Cursor cursor = mydb.readAllDate(city, currentDate);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String[] prayerNames = {"bayani", "niwaro", "asr", "eywara", "esha"};
                    for (String prayerName : prayerNames) {
                        try {
                            String prayerTime = cursor.getString(cursor.getColumnIndexOrThrow(prayerName));
                            if (prayerTime != null) {
                                // Before prayer notification
                                if (beforePrayer) {
                                    scheduleSingleKhatmNotification(context, prayerName, prayerTime, NOTIFICATION_DELAY_BEFORE, "before");
                                }
                                // After prayer notification
                                if (afterPrayer) {
                                    scheduleSingleKhatmNotification(context, prayerName, prayerTime, NOTIFICATION_DELAY_AFTER, "after");
                                }
                                // After Isha notification
                                if (afterIsha && "esha".equals(prayerName)) {
                                    scheduleSingleKhatmNotification(context, prayerName, prayerTime, NOTIFICATION_DELAY_AFTER, "afterIsha");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e("ScheduleKhatm", "Column for prayer " + prayerName + " not found.");
                        }
                    }
                }
                cursor.close();
            }
        }
    }

    /**
     * Schedules a single time-sensitive Khatm notification.
     * Fix includes correct permission check for Android 12+.
     */
    private static void scheduleSingleKhatmNotification(Context context, String prayerName, String prayerTime, int notificationDelay, String type) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(prayerTime);
            if (date == null) return;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // Set the alarm for today
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.MILLISECOND, notificationDelay);

            // If the calculated time is in the past, schedule it for the next day
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            int requestCode = generateRequestCode(prayerName + "_" + type);

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("title", "کاتی خوێندنی قورئانە");
            intent.putExtra("message", "بەردەوامی بدە بە خەتمەکەت");
            intent.putExtra("notificationId", requestCode);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    Log.w("ScheduleNotification", "SCHEDULE_EXACT_ALARM permission not granted for Khatm. Using inexact alarm.");
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

        } catch (ParseException e) {
            Log.e("PrayerNotification", "Error parsing time: " + e.getMessage());
        }
    }

    /**
     * Improved method to reliably cancel all potential Khatm notifications.
     */
    private static void cancelKhatmNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String[] prayerNames = {"bayani", "niwaro", "asr", "eywara", "esha"};
        String[] types = {"before", "after", "afterIsha"};

        for (String prayerName : prayerNames) {
            for (String type : types) {
                // Recreate the exact same request code used for scheduling
                int requestCode = generateRequestCode(prayerName + "_" + type);
                Intent intent = new Intent(context, NotificationReceiver.class);

                // Look for an existing PendingIntent, but don't create a new one
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                        PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);

                // If the PendingIntent exists, cancel it
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }
        }
    }



    public static void scheduleThirdNightNotification(Context context, boolean isSwitched) {
        if (!isSwitched) return; // ئەگەر switch فڵێت نەبوو، هیچ کاری ناکرێ

        // 🔥 گەڕانەوەی بەرواری پێشینەی بانگ
        ArrayList<Long> prayer = MydbClass.getTodayPrayers(true);
        if (prayer == null || prayer.size() < 5) return;

        Calendar fajrCal = Calendar.getInstance();
        fajrCal.setTimeInMillis(prayer.get(0));

        Calendar maghribCal = Calendar.getInstance();
        maghribCal.setTimeInMillis(prayer.get(4));

        // 🔥 حسابکردنی start لە Last Third
        String timeStr = getLastThirdStart(maghribCal.get(Calendar.HOUR_OF_DAY),
                maghribCal.get(Calendar.MINUTE),
                fajrCal.get(Calendar.HOUR_OF_DAY),
                fajrCal.get(Calendar.MINUTE));

        // 🔥 convert timeStr to hour & minute
        String[] parts = timeStr.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        // 🔥 schedule notification
        scheduleNotification(context, 6, hour, minute, "ئێستا سێیەکی شەوە", "باشترین کات بۆ دوعاکردن");
    }






    /**
     * Generates a deterministic, unique integer request code from a string.
     * This is crucial for being able to update or cancel specific alarms.
     */
    private static int generateRequestCode(String uniqueString) {
        return uniqueString.hashCode();
    }
}