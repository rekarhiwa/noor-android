package com.dya.noor.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import com.dya.noor.R;
import com.dya.noor.activities.AyahWidgetSettings;
import com.dya.noor.splash_screen.SplashScreen;

/**
 * Home-screen Quran ayah widget (daily / tap-cycle / fixed) — like Flutter AyahWidget.
 */
public class AyahWidget extends AppWidgetProvider {

    public static final String ACTION_NEXT = "com.dya.noor.widget.AYAH_NEXT";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences p = AyahWidgetHelper.prefs(context);
        String mode = AyahWidgetHelper.mode(context);
        String meta = p.getString(AyahWidgetHelper.KEY_META, "قورئان");
        String text = p.getString(AyahWidgetHelper.KEY_TEXT, "بسم الله الرحمن الرحيم");
        String translation = p.getString(AyahWidgetHelper.KEY_TRANSLATION, "");

        if (text == null || text.isEmpty()) {
            AyahWidgetHelper.refresh(context, true);
            p = AyahWidgetHelper.prefs(context);
            meta = p.getString(AyahWidgetHelper.KEY_META, "قورئان");
            text = p.getString(AyahWidgetHelper.KEY_TEXT, "بسم الله الرحمن الرحيم");
            translation = p.getString(AyahWidgetHelper.KEY_TRANSLATION, "");
            mode = AyahWidgetHelper.mode(context);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ayah_widget_layout);
        views.setTextViewText(R.id.ayah_widget_meta, meta);
        views.setTextViewText(R.id.ayah_widget_text, text);
        if (translation == null || translation.isEmpty()) {
            views.setViewVisibility(R.id.ayah_widget_translation, View.GONE);
        } else {
            views.setViewVisibility(R.id.ayah_widget_translation, View.VISIBLE);
            views.setTextViewText(R.id.ayah_widget_translation, translation);
        }

        Intent open = new Intent(context, SplashScreen.class);
        PendingIntent openPi = PendingIntent.getActivity(
                context, appWidgetId + 100, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ayah_widget_root, openPi);

        if (AyahWidgetHelper.MODE_SCREENSHOT.equals(mode)) {
            Intent next = new Intent(context, AyahWidget.class);
            next.setAction(ACTION_NEXT);
            PendingIntent nextPi = PendingIntent.getBroadcast(
                    context, appWidgetId + 200, next, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.ayah_widget_text, nextPi);
            views.setOnClickPendingIntent(R.id.ayah_widget_meta, nextPi);
        } else {
            Intent settings = new Intent(context, AyahWidgetSettings.class);
            PendingIntent settingsPi = PendingIntent.getActivity(
                    context, appWidgetId + 300, settings, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.ayah_widget_meta, settingsPi);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        AyahWidgetHelper.refresh(context, false);
        for (int id : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent != null && ACTION_NEXT.equals(intent.getAction())) {
            AyahWidgetHelper.advanceFromPool(context);
        }
    }

    @Override
    public void onEnabled(Context context) {
        AyahWidgetHelper.refresh(context, true);
    }

    public static void updateAllWidgetViews(Context context) {
        try {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            int[] ids = mgr.getAppWidgetIds(new ComponentName(context, AyahWidget.class));
            for (int id : ids) {
                updateAppWidget(context, mgr, id);
            }
        } catch (Exception ignored) {
        }
    }
}
