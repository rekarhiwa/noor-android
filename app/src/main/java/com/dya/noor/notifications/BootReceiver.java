package com.dya.noor.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.dya.noor.database.MydbClass;
import com.dya.noor.utlis.Utils;
import com.dya.noor.widget.CallaUpdateWidget;
import com.dya.noor.widget.SalatWidget;
import com.dya.noor.widget.SalatWidgetVertical;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // First, ensure the intent and its action are not null
        if (intent != null && intent.getAction() != null) {

            // Check if the received action is that the device has finished booting.
            // All your code should go inside this block.
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Log.d(TAG, "Device has finished booting. Rescheduling all alarms...");

                try {
                    // Initialize necessary classes
                    new Utils(context);
                    new MydbClass(context);

                    // 1. Reschedule the next prayer alarm
                    Log.d(TAG, "Rescheduling next prayer alarm.");
                    MydbClass.setNextPrayer(context);

                    // 2. Reschedule all other daily notifications
                    Log.d(TAG, "Rescheduling daily Zikr notifications.");
                    ScheduleNotification.scheduleNotification(context, 2, 8, 0, "ویردەکانی بەیانیان", "ئیستا کاتی خویندنی ویردەکانی بەیانیانە ☀");
                    ScheduleNotification.scheduleNotification(context, 3, 16, 40, "ویردەکانی ئیواران", "ئیستا کاتی خویندنی ویردەکانی ئیوارانە ✨");
                    ScheduleNotification.scheduleNotification(context, 4, 21, 30, "ویردەکانی خەوتنان", "ئیستا کاتی خویندنی ویردەکانی خەوتنانە 💤");
                    ScheduleNotification.scheduleNotification(context, 5, 22, 10, "سورەتی مولک", "شەوانە پێش خەوتن 🛌 سورەتی { الملک } بخوێنن چونکه  ① دەبێتە ڕێگر لە سزای گـۆڕ ② دەبێتە شەفاعەت و تکاکار بۆخوێنەرەکەی تاوەکو خوای گەورە لێی خۆش دەبێت ");
                    ScheduleNotification.scheduleKhatmNotification(context);

                    SharedPreferences prefs = context.getSharedPreferences("ThirdNightPrefs", Context.MODE_PRIVATE);
                    boolean isSwitched = prefs.getBoolean("thirdNightSwitch", false);

                    ScheduleNotification.scheduleThirdNightNotification(context, isSwitched);

                    // 3. Update widgets and their alarms
                    Log.d(TAG, "Updating widgets.");
                    SalatWidget.updateAllWidgetViews(context);
                    SalatWidgetVertical.updateAllWidgetViews(context);
                    CallaUpdateWidget.setRepeatingAlarm(context);

                    Log.d(TAG, "All tasks rescheduled successfully.");

                } catch (Exception e) {
                    Log.e(TAG, "An error occurred while rescheduling tasks on boot.", e);
                }
            }
        }
    }
}