package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.Prayer30Adapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.PrayerModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityThirtyDayPrayerTime extends BaseActivity {

    String CityNameEnglish , CityNameKurdish;

    MydbClass dbHelper;
    List<PrayerModel> prayers;
    RecyclerView recycler;

    Prayer30Adapter adapter;

    TextView cityName , hijryDate;
    HijrahDate hijrahDate;

    ImageView save , back;
    DateTimeFormatter formatterD, formatterM ,formatterY;
    String formattedD, formattedM , formattedY;
    String replaceToArabick;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thirty_day_prayer_time);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbHelper = new MydbClass(this);
        hijrahDate = HijrahDate.now();

        formatterD = DateTimeFormatter.ofPattern("dd");
        formatterM = DateTimeFormatter.ofPattern("MMMM");
        formatterY = DateTimeFormatter.ofPattern("yyyy");
        formattedD = formatterD.format(hijrahDate);
        formattedM = formatterM.format(hijrahDate);
        formattedY = formatterY.format(hijrahDate);



        replaceToArabick = (formattedM + "").replaceAll("Muharram", "مُحَرَّم")
                .replaceAll("Safar", "صَفَر")
                .replaceAll("Rabiʻ I", "رَبِيع ٱلْأَوَّل").replaceAll("Rabiʻ II", "رَبِيع ٱلْآخِر")
                .replaceAll("Jumada I", "جُمَادَىٰ ٱلْأُولَىٰ").replaceAll("Jumada II", "جُمَادَىٰ ٱلْآخِرَة")
                .replaceAll("Rajab", "رَجَب").replaceAll("Shaʻban", "شَعْبَان")
                .replaceAll("Ramadan", "رَمَضَان").replaceAll("Shawwal", "شَوَّال")
                .replaceAll("Dhuʻl-Qiʻdah", "ذُو ٱلْقَعْدَة").replaceAll("Dhuʻl-Hijjah", "ذُو ٱلْحِجَّة");




        CityNameEnglish = getIntent().getStringExtra("City");
        CityNameKurdish = getIntent().getStringExtra("City2");

        cityName = findViewById(R.id.cityName);
        hijryDate = findViewById(R.id.hijryDate);
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);

        hijryDate.setText(String.format("%s ی %sی %s ی هجری ", formattedD, replaceToArabick, formattedY));

        back.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


        cityName.setText(new StringBuilder().append("کاتەکانی بانگ بۆ شاری ")
                .append(CityNameKurdish).toString());


        SQLiteDatabase db = dbHelper.getReadableDatabase();

        prayers = get30DayPrayerTimes(db,CityNameEnglish);// your city

        recycler = findViewById(R.id.recyclerPrayer30);

        recycler.setLayoutManager(new LinearLayoutManager(this));

// ⭐ ADD HEADER HERE
        PrayerModel header = new PrayerModel();
        prayers.add(0, header);

        adapter = new Prayer30Adapter(this,prayers);

        recycler.setAdapter(adapter);


        save.setOnClickListener(v -> {
            showSaveDialog();
        });



    }


    public void saveToGallery() {

        // ✅ create full layout with header + all recycler items
        View layout = createFullExportLayout();

        // ✅ measure and layout full view (important!)
        layout.measure(
                View.MeasureSpec.makeMeasureSpec(
                        recycler.getWidth(),
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(
                        0,
                        View.MeasureSpec.UNSPECIFIED));

        layout.layout(
                0,
                0,
                layout.getMeasuredWidth(),
                layout.getMeasuredHeight());

        // ✅ create bitmap
        Bitmap bitmap = Bitmap.createBitmap(
                layout.getMeasuredWidth(),
                layout.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);

        // ✅ save bitmap to gallery
        try {
            String filename = "noor_prayer_time_for" +CityNameEnglish + ".png";

            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, filename);
            values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES);

            android.net.Uri uri = getContentResolver().insert(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            FileOutputStream out = (FileOutputStream) getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            Toast.makeText(this, "وێنەکە هەلگیرا لە گەلەری", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void saveAsPdf() {

        // ✅ create full layout with header + all recycler items
        View layout = createFullExportLayout();

        // ✅ measure and layout full view
        layout.measure(
                View.MeasureSpec.makeMeasureSpec(
                        recycler.getWidth(),
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(
                        0,
                        View.MeasureSpec.UNSPECIFIED));

        layout.layout(
                0,
                0,
                layout.getMeasuredWidth(),
                layout.getMeasuredHeight());

        // ✅ create bitmap
        Bitmap bitmap = Bitmap.createBitmap(
                layout.getMeasuredWidth(),
                layout.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);

        // ✅ save bitmap as PDF
        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                bitmap.getWidth(),
                bitmap.getHeight(),
                1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        page.getCanvas().drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        try {
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "noor_prayer_time_for" +CityNameEnglish + ".pdf");

            FileOutputStream out = new FileOutputStream(file);
            document.writeTo(out);
            out.close();
            document.close();

            Toast.makeText(this, "PDF پاشەکەوت کرا", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void showSaveDialog(){

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("هەڵبژاردن")
                .setMessage("دەتەوێت چ جۆرە فایڵێک پاشەکەوت بکەیت؟")

                .setPositiveButton("وێنە", (d,w)->{

                    findViewById(R.id.layoutToSave).post(() -> {
                        saveToGallery();
                    });

                })

                .setNegativeButton("PDF", (d,w)->{

                    findViewById(R.id.layoutToSave).post(() -> {
                        saveAsPdf();
                    });

                })


                .show();
    }




    private Bitmap getBitmapFromView(View view){

        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        if(width == 0 || height == 0){
            width = view.getWidth();
            height = view.getHeight();
        }

        Bitmap bitmap =
                Bitmap.createBitmap(width,height,
                        Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }



    private View createFullExportLayout(){

        LinearLayout exportLayout = new LinearLayout(this);
        exportLayout.setOrientation(LinearLayout.VERTICAL);
        exportLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        // 👉 get your existing header inside layoutToSave
        LinearLayout header = findViewById(R.id.header_layout);

        // clone header view into export layout
        header.setDrawingCacheEnabled(true);
        header.buildDrawingCache();
        Bitmap headerBitmap = Bitmap.createBitmap(header.getDrawingCache());
        header.setDrawingCacheEnabled(false);

        // create ImageView for header
        ImageView headerCopy = new ImageView(this);
        headerCopy.setImageBitmap(headerBitmap);
        exportLayout.addView(headerCopy);

        // 👉 now add all recycler items manually
        for(int i=0;i<prayers.size();i++){

            View item =
                    getLayoutInflater().inflate(
                            R.layout.item_prayer_30day,
                            exportLayout,
                            false);

            TextView date = item.findViewById(R.id.txtDate);
            TextView bayani = item.findViewById(R.id.txtBayani);
            TextView niwaro = item.findViewById(R.id.txtNiwaro);
            TextView asr = item.findViewById(R.id.txtAsr);
            TextView eywara = item.findViewById(R.id.txtEywra);
            TextView esha = item.findViewById(R.id.txtEsha);

            if(i==0){

                date.setText("بەروار");
                bayani.setText("بەیانی");
                niwaro.setText("نوەڕۆ");
                asr.setText("عەسر");
                eywara.setText("ئێوارە");
                esha.setText("خەوتنان");

            }else{

                PrayerModel p = prayers.get(i);

                date.setText(p.date);
                bayani.setText(p.bayani);
                niwaro.setText(p.niwaro);
                asr.setText(p.asr);
                eywara.setText(p.eywara);
                esha.setText(p.esha);
            }

            exportLayout.addView(item);
        }

        return exportLayout;
    }



    public List<String> getNext30DaysDates() {

        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.US);

        for(int i = 0; i < 30; i++) {
            dates.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }



    public List<PrayerModel> get30DayPrayerTimes(SQLiteDatabase db, String city) {

        List<PrayerModel> list = new ArrayList<>();
        List<String> dates = getNext30DaysDates();

        StringBuilder placeholders = new StringBuilder();
        for(int i=0;i<dates.size();i++){
            placeholders.append("?");
            if(i<dates.size()-1) placeholders.append(",");
        }

        String sql =
                "SELECT bayani,xorhalatn,niwaro,asr,eywara,esha,date " +
                        "FROM PrayerTimesforKurdistantable " +
                        "WHERE cities=? AND date IN ("+placeholders+")";

        List<String> argsList = new ArrayList<>();
        argsList.add(city);
        argsList.addAll(dates);

        String[] args = argsList.toArray(new String[0]);

        Cursor cursor = db.rawQuery(sql,args);

        if(cursor.moveToFirst()){
            do{
                PrayerModel p = new PrayerModel();

                p.bayani = cursor.getString(0);
                p.xorhalatn = cursor.getString(1);
                p.niwaro = cursor.getString(2);
                p.asr = cursor.getString(3);
                p.eywara = cursor.getString(4);
                p.esha = cursor.getString(5);
                p.date = cursor.getString(6);

                list.add(p);

            }while(cursor.moveToNext());
        }

        cursor.close();
        return list;
    }





}