package com.dya.noor.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dya.noor.database.MydbClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Ayah home-widget data helper — modes match Flutter: daily / screenshot / fixed.
 */
public final class AyahWidgetHelper {

    public static final String PREFS = "ayah_widget";
    public static final String MODE_DAILY = "daily";
    public static final String MODE_SCREENSHOT = "screenshot";
    public static final String MODE_FIXED = "fixed";

    public static final String KEY_MODE = "ayah_widget_mode";
    public static final String KEY_FIXED_SURA = "ayah_widget_fixed_sura";
    public static final String KEY_FIXED_AYAH = "ayah_widget_fixed_ayah";
    public static final String KEY_DAY = "ayah_widget_day_key";
    public static final String KEY_TEXT = "ayah_text";
    public static final String KEY_TRANSLATION = "ayah_translation";
    public static final String KEY_META = "ayah_meta";
    public static final String KEY_POOL = "ayah_pool";
    public static final String KEY_POOL_INDEX = "ayah_pool_index";

    private static final int POOL_SIZE = 40;

    private AyahWidgetHelper() {
    }

    public static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static String mode(Context context) {
        String m = prefs(context).getString(KEY_MODE, MODE_DAILY);
        if (MODE_SCREENSHOT.equals(m) || MODE_FIXED.equals(m) || MODE_DAILY.equals(m)) {
            return m;
        }
        return MODE_DAILY;
    }

    public static String modeLabel(String mode) {
        switch (mode) {
            case MODE_SCREENSHOT:
                return "سکریشۆت / پەنجە";
            case MODE_FIXED:
                return "جێگیر";
            case MODE_DAILY:
            default:
                return "ڕۆژانە";
        }
    }

    public static void setMode(Context context, String mode) {
        prefs(context).edit().putString(KEY_MODE, mode).apply();
    }

    public static void setFixed(Context context, int suraId, int ayah) {
        prefs(context).edit()
                .putInt(KEY_FIXED_SURA, suraId)
                .putInt(KEY_FIXED_AYAH, ayah)
                .apply();
    }

    public static void refresh(Context context, boolean forceNew) {
        SharedPreferences p = prefs(context);
        String mode = mode(context);

        if (!forceNew) {
            String existing = p.getString(KEY_TEXT, "");
            if (MODE_DAILY.equals(mode)) {
                String today = dayKey();
                if (today.equals(p.getString(KEY_DAY, "")) && existing != null && !existing.isEmpty()) {
                    AyahWidget.updateAllWidgetViews(context);
                    return;
                }
            } else if (MODE_SCREENSHOT.equals(mode)) {
                String pool = p.getString(KEY_POOL, "");
                if (existing != null && !existing.isEmpty() && pool != null && !pool.isEmpty()) {
                    AyahWidget.updateAllWidgetViews(context);
                    return;
                }
            } else if (MODE_FIXED.equals(mode)) {
                if (existing != null && !existing.isEmpty()) {
                    AyahWidget.updateAllWidgetViews(context);
                    return;
                }
            }
        }

        buildFresh(context, mode);
        AyahWidget.updateAllWidgetViews(context);
    }

    public static void advanceFromPool(Context context) {
        if (MODE_FIXED.equals(mode(context)) || MODE_DAILY.equals(mode(context))) {
            refresh(context, false);
            return;
        }
        SharedPreferences p = prefs(context);
        try {
            String raw = p.getString(KEY_POOL, "[]");
            JSONArray list = new JSONArray(raw);
            if (list.length() == 0) {
                refresh(context, true);
                return;
            }
            int index = (p.getInt(KEY_POOL_INDEX, 0) + 1) % list.length();
            JSONObject item = list.getJSONObject(index);
            saveCurrent(context, item, MODE_SCREENSHOT);
            p.edit().putInt(KEY_POOL_INDEX, index).apply();
            AyahWidget.updateAllWidgetViews(context);
        } catch (Exception e) {
            refresh(context, true);
        }
    }

    private static void buildFresh(Context context, String mode) {
        MydbClass db = new MydbClass(context);
        SQLiteDatabase sqlite = db.getReadableDatabase();
        if (sqlite == null) return;

        try {
            if (MODE_DAILY.equals(mode)) {
                Cursor c = queryAyahForDay(sqlite);
                if (c != null && c.moveToFirst()) {
                    JSONObject payload = payloadFromCursor(c);
                    saveCurrent(context, payload, mode);
                    JSONArray pool = new JSONArray();
                    pool.put(payload);
                    prefs(context).edit()
                            .putString(KEY_POOL, pool.toString())
                            .putInt(KEY_POOL_INDEX, 0)
                            .putString(KEY_DAY, dayKey())
                            .apply();
                    c.close();
                }
                return;
            }

            if (MODE_FIXED.equals(mode)) {
                int sura = prefs(context).getInt(KEY_FIXED_SURA, 1);
                int ayah = prefs(context).getInt(KEY_FIXED_AYAH, 1);
                Cursor c = queryAyah(sqlite, sura, ayah);
                if (c == null || !c.moveToFirst()) {
                    if (c != null) c.close();
                    c = queryAyah(sqlite, 1, 1);
                }
                if (c != null && c.moveToFirst()) {
                    JSONObject payload = payloadFromCursor(c);
                    saveCurrent(context, payload, mode);
                    JSONArray pool = new JSONArray();
                    pool.put(payload);
                    prefs(context).edit()
                            .putString(KEY_POOL, pool.toString())
                            .putInt(KEY_POOL_INDEX, 0)
                            .apply();
                    c.close();
                }
                return;
            }

            // screenshot / tap pool
            Cursor c = sqlite.rawQuery(
                    "SELECT * FROM ayah_text ORDER BY RANDOM() LIMIT " + POOL_SIZE, null);
            JSONArray pool = new JSONArray();
            if (c != null) {
                while (c.moveToNext()) {
                    pool.put(payloadFromCursor(c));
                }
                c.close();
            }
            if (pool.length() == 0) return;
            saveCurrent(context, pool.getJSONObject(0), MODE_SCREENSHOT);
            prefs(context).edit()
                    .putString(KEY_POOL, pool.toString())
                    .putInt(KEY_POOL_INDEX, 0)
                    .apply();
        } catch (Exception ignored) {
        }
    }

    private static Cursor queryAyahForDay(SQLiteDatabase db) {
        Cursor countC = db.rawQuery("SELECT COUNT(*) AS c FROM ayah_text", null);
        int count = 0;
        if (countC != null && countC.moveToFirst()) {
            count = countC.getInt(0);
            countC.close();
        }
        if (count <= 0) return null;
        Calendar cal = Calendar.getInstance();
        int seed = cal.get(Calendar.YEAR) * 1000 + (cal.get(Calendar.MONTH) + 1) * 40
                + cal.get(Calendar.DAY_OF_MONTH);
        int offset = Math.abs(seed) % count;
        return db.rawQuery("SELECT * FROM ayah_text LIMIT 1 OFFSET " + offset, null);
    }

    private static Cursor queryAyah(SQLiteDatabase db, int sura, int ayah) {
        return db.rawQuery(
                "SELECT * FROM ayah_text WHERE suraId = ? AND ayah = ? LIMIT 1",
                new String[]{String.valueOf(sura), String.valueOf(ayah)});
    }

    private static JSONObject payloadFromCursor(Cursor c) throws Exception {
        String suraId = safe(c, "suraId");
        String ayah = safe(c, "ayah");
        String nameAr = safe(c, "sura_name_ar");
        String text = safe(c, "text");
        String translation = safe(c, "asan");
        if (translation.isEmpty()) translation = safe(c, "rebar");
        JSONObject o = new JSONObject();
        o.put("text", text);
        o.put("translation", translation);
        o.put("meta", nameAr.isEmpty()
                ? ("سورة " + suraId + " · " + ayah)
                : (nameAr + " · " + ayah));
        o.put("suraId", suraId);
        o.put("ayah", ayah);
        return o;
    }

    private static void saveCurrent(Context context, JSONObject payload, String mode) throws Exception {
        prefs(context).edit()
                .putString(KEY_TEXT, payload.optString("text", ""))
                .putString(KEY_TRANSLATION, payload.optString("translation", ""))
                .putString(KEY_META, payload.optString("meta", "قورئان"))
                .putString(KEY_MODE, mode)
                .apply();
    }

    private static String dayKey() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1)
                + "-" + cal.get(Calendar.DAY_OF_MONTH);
    }

    private static String safe(Cursor c, String col) {
        try {
            int i = c.getColumnIndex(col);
            if (i < 0) return "";
            String v = c.getString(i);
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "";
        }
    }
}
