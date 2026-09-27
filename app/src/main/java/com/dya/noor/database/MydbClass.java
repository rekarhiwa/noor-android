
package com.dya.noor.database;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dya.noor.notifications.AlarmReceiver;
import com.dya.noor.utlis.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;



@SuppressLint("Range")
public class MydbClass extends SQLiteOpenHelper {
    public static String dbName = "noor_dbs_last_vs6.db";
    public static int dbVersion = 14;
    public static String dbPath ="";
    public Context myContext;
    //new
    public MydbClass(@Nullable Context context) {
        super(context,dbName,null,dbVersion);
        myContext=context;
        StartWork();
    }



    public static ArrayList<Long> getPrayerForDate(String date) {
        return getPrayerForDate(date, false);
    }


    public static ArrayList<Long> getPrayerForDate(String date, boolean includeSunrise) {
        ArrayList<Long> prayers = new ArrayList<>();

        try {
            MydbClass db = new MydbClass(Utils.activeContext);

            Cursor prayerCursor = db.readAllDate(getSelectedCity(), date);

            prayerCursor.moveToNext();

            prayers.add(Utils.getMilliFromTextedTime(date, prayerCursor.getString(prayerCursor.getColumnIndex("bayani"))));

            if (includeSunrise) {
                prayers.add(Utils.getMilliFromTextedTime(date, prayerCursor.getString(prayerCursor.getColumnIndex("xorhalatn"))));
            }

            prayers.add(Utils.getMilliFromTextedTime(date, prayerCursor.getString(prayerCursor.getColumnIndex("niwaro"))));

            prayers.add(Utils.getMilliFromTextedTime(date, prayerCursor.getString(prayerCursor.getColumnIndex("asr"))));

            prayers.add(Utils.getMilliFromTextedTime(date, prayerCursor.getString(prayerCursor.getColumnIndex("eywara"))));

            prayers.add(Utils.getMilliFromTextedTime(date, prayerCursor.getString(prayerCursor.getColumnIndex("esha"))));
        } catch (Exception e) {
            Log.e("-HAX-", "error in fetching prayer time ", e);
        }
        return prayers;
    }

    public static ArrayList<Long> getPrayerWithDateAdded(int add_day) {
        return getPrayerWithDateAdded(add_day, false);
    }

    @SuppressLint("DefaultLocale")
    public static ArrayList<Long> getPrayerWithDateAdded(int add_day, boolean includeSunrise) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, add_day);

        int month = cal.get(Calendar.MONTH) + 1;

        return getPrayerForDate(String.format("%02d-%02d", month, cal.get(Calendar.DAY_OF_MONTH)), includeSunrise);
    }

    public static ArrayList<Long> getTodayPrayers() {
        return getPrayerWithDateAdded(0);
    }

    public static ArrayList<Long> getTodayPrayers(boolean includeSunrise) {
        return getPrayerWithDateAdded(0, includeSunrise);
    }

    public static PrayerInfo getNextPrayer() {
        ArrayList<Long> prayers = getTodayPrayers();

        Log.d("HAX","didn't pick any prayer. returning null. : "+getPrayerWithDateAdded(1).get(0));

        //bangi bayani sbaine
        prayers.add(getPrayerWithDateAdded(1).get(0));

        for (int i = 0; i < prayers.size(); i++) {
            if (prayers.get(i) > System.currentTimeMillis()) {
                return new PrayerInfo(i, prayers.get(i));
            }
        }

        Log.d("HAX","didn't pick any prayer. returning null.");

        return null;
    }

    /**
     * ✅ THIS IS THE FULLY CORRECTED AND UPDATED METHOD ✅
     * It correctly schedules the next prayer notification.
     */
    public static void setNextPrayer(Context context) {
        // Retain your global context logic
        if (Utils.activeContext == null && context != null) {
            Utils.activeContext = context;
        }

        // It's crucial that the context passed in is not null
        if (context == null) {
            Log.e("HAX", "Context is null, cannot set next prayer alarm.");
            return;
        }

        PrayerInfo pr = getNextPrayer();
        // CRITICAL: Handle the case where there is no next prayer.
        if (pr == null) {
            Log.e("HAX", "PrayerInfo is null, cannot set alarm (No upcoming prayers found).");
            return;
        }

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(context, AlarmReceiver.class);
        in.putExtra("what_prayer", pr.prayerName);

        // Using FLAG_IMMUTABLE is the modern, secure standard.
        // FLAG_UPDATE_CURRENT ensures that if you set a new alarm with the same request code,
        // it updates the old one with new data (e.g., new prayer time).
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                762001, // This is a unique request code for your prayer alarm
                in,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerTime = pr.milli;

        // Check if the alarm time is in the past. This prevents immediate firing for a missed prayer.
        if (triggerTime < System.currentTimeMillis()) {
            Log.w("HAX", "Attempted to set an alarm for the past. Prayer: " + pr.prayerName);
            return;
        }

        // Correctly handle permissions and set ONLY ONE exact alarm
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    Log.d("HAX", "[S+] " + pr.prayerName + " timer set for: " + triggerTime);
                } else {
                    // Guide user to settings. It's best to show a dialog explaining why first.
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Log.w("HAX", "Cannot schedule exact alarms. Asking user for permission.");
                }
            } else { // For older Android versions (API 23+)
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                Log.d("HAX", "[Legacy] " + pr.prayerName + " timer set for: " + triggerTime);
            }
        } catch (Exception e) {
            Log.e("HAX", "Could not set alarm for " + pr.prayerName, e);
        }
    }


    public static PrayerInfo getCurrentClosestPrayer() {
        ArrayList<Long> main = getTodayPrayers();
        ArrayList<Long> prTimesMilli = new ArrayList<>();

        prTimesMilli.addAll(main);

        long now = System.currentTimeMillis();

        for (int i = 0; i < prTimesMilli.size(); i++) {
            prTimesMilli.set(i, Math.abs(now - prTimesMilli.get(i)));
        }

        int chosen = 0;
        for (int i = 0; i < prTimesMilli.size(); i++) {
            //we find the smallest value the closest time to current phone time.
            if (prTimesMilli.get(i) < prTimesMilli.get(chosen)) {
                chosen = i;
            }
        }

        return new PrayerInfo(chosen, main.get(chosen));
    }

    public static long getNextPrayerMilli() {
        return (getNextPrayer()).milli;
    }

    public static String getSelectedCity() {
        SharedPreferences pref = Utils.activeContext.getSharedPreferences("key", Context.MODE_PRIVATE);
        return pref.getString("City", "Kalar");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private boolean ExistDatabase(){
        File myFile = new File(dbPath+dbName);

        return myFile.exists();
    }
    private static void CopyDataBase(Context context){

        try {
            InputStream myInput = context.getAssets().open(dbName);
            OutputStream myOutput =new FileOutputStream(dbPath+dbName);
            byte [] myBuffer = new byte[1024];
            int length;
            while ((length = myInput.read(myBuffer))>0){
                myOutput.write(myBuffer,0,length);
            }
            myOutput.flush(); myOutput.close(); myInput.close();
        }catch (Exception ex){

        }
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        db.disableWriteAheadLogging();
        super.onOpen(db);
    }

    public void StartWork(){

        dbPath = myContext.getFilesDir().getParent()+"/databases/";
        if (!ExistDatabase()){
            this.getReadableDatabase();
            CopyDataBase(myContext);
        }

    }

    public Cursor readAllData(){
        String query ="select * from surat";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }



    public String getSurahNameByPage(int pageNumber) {
        String surahName = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT sura_name FROM surat WHERE ? BETWEEN start_page_number AND end_page_number";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(pageNumber)});

        if (cursor != null && cursor.moveToFirst()) {
            surahName = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return surahName;
    }

    public Cursor readAllNameData(String id){
        String query ="select name,link from surat WHERE soraid ="+id;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }

    public Cursor readAllZikrData(){
        String Lang ="ckb";
        String query ="select * from azkar_chapter_translation WHERE language ='ckb'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllAllahName(){
        String query ="select * from names";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }

    public Cursor readAllAyahData(String id){

        String query ="select * from ayah_text WHERE suraId ="+id+" ORDER BY ayah ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllAyahDataPage(String id){

        String query ="select * from allquran WHERE sura_no ="+id+" ORDER BY aya_no ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllAyahDataKhatm(String id , String ayaId){

        String query ="select text From ayah_text WHERE suraId ="+id+" AND ayah="+ayaId+"";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllqurannewData(){

        String query ="select suraId ,juzz,page,link,sura_name_ar,ayah,text,search from ayah_text  ORDER BY page ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }

    public Cursor readAllFarmudaTData(){
        String query ="select _id,titel from farmuda";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllFarmudaData(String id){

        String query ="select * from farmuda WHERE _id ="+id+"";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }

    public Cursor readAllFarmudaDataSearch(){

        String query ="select * from farmuda ORDER BY _id ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllCities(){

        String query ="select * from cities WHERE Jegir =1  AND iso IN ('IQ', 'IR') ORDER BY iso  ASC";

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllDate(@NonNull String City, String Date) {

        String CityFormat = (City)
                .replaceAll("Amedi","Akre")
                .replaceAll("Arbat","Slemani")
                .replaceAll("Barznja","Darbandikhan")
                .replaceAll("Bazyan","Qaladze")
                .replaceAll("Darbandixan","Darbandikhan")
                .replaceAll("HajiAwa","Chamchamal")
                .replaceAll("HalabjaN","Halabja")
                .replaceAll("Kfri","Kifri")
                .replaceAll("Penjuin","SaidSadq")
                .replaceAll("Piramagrun","Dukan")
                .replaceAll("Ranya","Chamchamal")
                .replaceAll("SaidSadiq","SaidSadq")
                .replaceAll("Slemany","Slemani")
                .replaceAll("Takya","Chamchamal(Kon)")
                .replaceAll("TaqTaq","Chamchamal")
                .replaceAll("Tasluja","Qaladze")
                .replaceAll("Xalakan","Chamchamal")
                .replaceAll("Zaxo","Zakho")
                .replaceAll("mosul","Mosul")
                .replaceAll("tuzxurmatu","Dwz");


        String query = "select * from PrayerTimesforKurdistantable WHERE cities ='" + CityFormat + "' and date ='" + Utils.replaceEasternNumbers(Date) + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public Cursor readAllAzkar_item(String chapterId, String lang){

        String query =
                "SELECT i._id, i.item AS ar_text, " +
                        "t.item_translation AS kr_text, " +
                        "r.reference AS ref_text " +
                        "FROM azkar_item i " +
                        "LEFT JOIN azkar_item_translation t " +
                        "ON i._id = t.item_id AND t.language = ? " +
                        "LEFT JOIN azkar_reference_translation r " +
                        "ON i._id = r.reference_id AND r.language = ? " +
                        "WHERE i.chapter_id = ? " +
                        "ORDER BY i._id ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, new String[]{lang, lang, chapterId});
    }
    public Cursor readHawalan(){

        String query ="select * from hawalan";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor mirat(){

        String query ="select * from miratt";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor wallamakanCategory(){

        String query ="select * from walamakan_catagore WHERE main_id=0 ";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor wallamakanCategory2(String id){

        String query ="select * from walamakan_catagore WHERE main_id="+id+"";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor wallamakanAnswer(String id){

        String query ="select * from walamakan_ans WHERE cat_id="+id+"";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor wallamakanAnswerSearch(){

        String query ="select * from walamakan_ans";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;

    }






    //hadiths datas

    public Cursor readAllBook(){

        // سەحیحەکان
        String query ="select * from book WHERE count !=1 ORDER BY sort  ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllKitab(int id){
        // کتێبی ناو سەحیحەکان
        String query ="select * from kitab WHERE book_id="+id+" ORDER BY kitab_sort  ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readAllChapter(int id){

        // بابەکان
        String query ="select * from chapters WHERE kitab_id="+id+" ORDER BY chapter_sort ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }

    public Cursor readAllHadith( int kitab_id ){
        String query ="select * from hadiths WHERE kitab_id ="+kitab_id+" ORDER BY sort ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    public Cursor readHadithWithChapters(int kitabId) {
        String query =
                "SELECT h.*, " +
                        "c.title_ar, " +
                        "c.title, " +
                        "c.chapter_sort AS chapter_sort " +
                        "FROM hadiths h " +
                        "LEFT JOIN chapters c ON h.chapter_id = c.id " +
                        "WHERE h.kitab_id = ? " +
                        "ORDER BY " +
                        "c.chapter_sort IS NULL, " +
                        "c.chapter_sort, " +
                        "h.sort";

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, new String[]{String.valueOf(kitabId)});
    }





    public Cursor readQariName(){
        String query ="select * from Qari_name ORDER BY id ASC";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }


    public Cursor searchHadith(String keyword){

        String query =
                "SELECT " +
                        "h.id, " +
                        "h.book_id, " +
                        "h.kitab_id, " +
                        "h.normalized_text_ar, " +
                        "h.normalized_text, " +
                        "k.title AS kitab_name, " +
                        "b.name AS book_name " +
                        "FROM hadiths h " +
                        "LEFT JOIN kitab k ON h.kitab_id = k.id " +
                        "LEFT JOIN book b ON h.book_id = b.id " +
                        "WHERE " +
                        "h.normalized_text_ar LIKE ? OR " +
                        "h.normalized_text LIKE ? " +
                        "ORDER BY b.sort, k.kitab_sort, h.sort";

        SQLiteDatabase db = this.getReadableDatabase();
        String key = "%" + keyword + "%";

        return db.rawQuery(query, new String[]{key, key});
    }


    /**

     public Cursor readHadithWithChapters(int kitabId) {
     String query =
     "SELECT h.*, c.title_ar, c.title " +
     "FROM hadiths h " +
     "JOIN chapters c ON h.chapter_id = c.id " +
     "WHERE h.kitab_id = ? " +
     "ORDER BY h.chapter_id, h.sort";

     SQLiteDatabase db = this.getReadableDatabase();
     return db.rawQuery(query, new String[]{String.valueOf(kitabId)});
     }
     */



}