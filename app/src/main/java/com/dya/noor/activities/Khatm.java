package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.database.MydbClass;
import com.dya.noor.download.DownloadFilesTask;
import com.dya.noor.notifications.ScheduleNotification;
import com.dya.noor.utlis.QuranPageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Khatm extends BaseActivity {
    ImageView minusBtn
            , plusBtn;
    TextView txtStart
            , nextDay
            , juzzStart ,
            SuraStart,
            SuraEnd,
            AyaStart,
            txtKhatmNUmber,
            txtStartNow,
            btnNew,
            btnDuaa;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String KHATM_PREFS = "KhatmPrefs"; // Separate prefs for Khatm
    public static final String LAYOUT_PREFS = "LayoutPrefs"; // Separate prefs for Khatm
    public static final String NOTIFICATION_PREFS = "NotificationPrefs"; // Separate prefs for general notifications



    // Juz start pages (Madani Mushaf)
    // Get the Juz start page from the array
    int[] juzStartPage = {
            1, 22, 42, 62, 82, 102, 122, 142, 162, 182, 202, 222, 242, 262, 282,
            302, 322, 342, 362, 382, 402, 422, 442, 462, 482, 502, 522, 542, 562, 582
    };

    int[] juzEndPages = {
            21, 41, 61, 81, 101, 121, 141, 161, 181, 201, 221, 241, 261, 281, 301,
            321, 341, 361, 381, 401, 421, 441, 461, 481, 501, 521, 541, 561, 581, 604
    };

    // Arrays to store Juz start and end ayah information
    // Juz start and end sura names
    private String[] juzStartSuraNames = {
            "الفاتحة", "البقرة", "البقرة", "آل عمران", "النساء", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "هود", "يوسف", "الحجر", "الإسراء", "الكهف", "الأنبياء", "المؤمنون", "الفرقان", "النمل", "العنكبوت", "الأحزاب", "يس", "الزمر", "فصلت", "الأحقاف", "الذاريات", "المجادلة", "الملك", "النبأ"

             };

    private String[] juzEndSuraNames = {
            "البقرة", "البقرة", "آل عمران", "النساء", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "هود", "يوسف", "إبراهيم", "النحل", "الكهف", "طه", "الحج", "الفرقان", "النمل", "العنكبوت", "الأحزاب", "يس", "الزمر", "فصلت", "الجاثية", "الذاريات", "الحديد", "التحريم", "المرسلات", "الناس"
    };

    // Juz start and end sura numbers
    private int[] juzStartSuraNumbers = {
            1, 2, 2, 3, 4, 4, 5, 6, 7, 8, 9, 11, 12, 15, 17, 18, 21, 23, 25, 27, 29, 33, 36, 39, 41, 46, 51, 58, 67, 78
    };

    private int[] juzEndSuraNumbers = {
            2, 2, 3, 4, 4, 5, 6, 7, 8, 9, 11, 12, 14, 16, 18, 20, 22, 25, 27, 29, 33, 36, 39, 41, 45, 51, 57, 66, 77, 114
    };

    // Juz start and end ayah numbers
    private int[] juzStartAyahNumbers = {
            1, 142, 253, 93, 24, 148, 82, 111, 88, 41, 93, 6, 53, 1, 1, 75, 1, 1, 21, 56, 46, 31, 28, 32, 47, 1, 31, 1, 1, 1
    };

    private int[] juzEndAyahNumbers = {
            141, 252, 92, 23, 147, 81, 110, 87, 40, 92, 5, 52, 52, 128, 74, 135, 78, 20, 55, 45, 30, 27, 31, 46, 37, 30, 29, 12, 50, 6
    };

    int currentJuz;
    ImageView btnBack;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchAfterPrayer,
            switchBeforePrayer,
            switchAfterIsha;

    String PreferencesName = "NotificationPrefs";
    SharedPreferences sharedPreferencesNotification;
    SharedPreferences.Editor editorNotification ;
    SharedPreferences sharedPreferencesLayout;
    SharedPreferences.Editor editorLayout;

    ProgressBar progressBar;

    MydbClass mydbClass;
   public int KhatmNumberS;



    // check krdny internet
    ConnectivityManager connectivityManager ;
    NetworkInfo wifi ;
    NetworkInfo mobileNetwork;
    //////////

    String folderName = "quranPaage";
    String pngLink = "https://raw.githubusercontent.com/w-coding/Holy-Quran/main/Quran_per_page/";
    HashMap<Integer, List<String>> fileUrlsMap =new HashMap<>();
    List<String> fileUrls = new ArrayList<>();
    List<String> urls ;

    List<String> missingFiles = new ArrayList<>();

    LinearLayout LayoutStart
            , LayoutSettings ,
            HelpNoorLayout ,
            KhatmENDLayout;


    int layoutStartVisibility ;
    int khatmENDLayoutVisibility ;
    int layoutSettingsVisibility ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khatm);

        minusBtn = findViewById(R.id.minusBtn);
        plusBtn = findViewById(R.id.plusBtn);
        txtStart = findViewById(R.id.txtStart);
        nextDay = findViewById(R.id.nextDay);
        juzzStart = findViewById(R.id.juzzStart);
        btnBack = findViewById(R.id.btnBack);
        SuraStart = findViewById(R.id.SuraStart);
        SuraEnd = findViewById(R.id.SuraEnd);
        AyaStart = findViewById(R.id.AyaStart);
        txtKhatmNUmber = findViewById(R.id.txtKhatmNUmber);
        txtStartNow = findViewById(R.id.txtStartNow);
        LayoutStart = findViewById(R.id.LayoutStart);
        LayoutSettings = findViewById(R.id.LayoutClous);
        HelpNoorLayout = findViewById(R.id.HelpNoorLayout);
        KhatmENDLayout = findViewById(R.id.KhatmENDLayout);
        btnNew = findViewById(R.id.btnNew);
        btnDuaa = findViewById(R.id.btnDuaa);

        progressBar = findViewById(R.id.progressBar);

        switchAfterPrayer = findViewById(R.id.NotifiSwitch);
        switchBeforePrayer = findViewById(R.id.NotifiSwitchHour);
        switchAfterIsha = findViewById(R.id.NotifiSwitchEveryEsha);

        HelpNoorLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, HelpNoor.class);
            intent.putExtra("Activity", "Khatm");
            startActivity(intent);
        });

        // check krdny internet
        connectivityManager  = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        mydbClass = new MydbClass(this);

        //sharedPreferencesNotification = getSharedPreferences(PreferencesName, MODE_PRIVATE);
       // editorNotification = sharedPreferencesNotification.edit();

        sharedPreferencesNotification = getSharedPreferences(KHATM_PREFS, MODE_PRIVATE); // Use KHATM_PREFS
        editorNotification = sharedPreferencesNotification.edit();
        QuranPageUtils.folderName = folderName;

        sharedPreferencesLayout = getSharedPreferences(LAYOUT_PREFS, MODE_PRIVATE); // Use LAYOUT_PREFS
        editorLayout = sharedPreferencesLayout.edit();

        boolean afterPrayer = sharedPreferencesNotification.getBoolean("switchAfterPrayer", false);
        boolean beforePrayer = sharedPreferencesNotification.getBoolean("switchBeforePrayer", false);
        boolean afterIsha = sharedPreferencesNotification.getBoolean("switchAfterIsha", false);
        boolean hich = sharedPreferencesNotification.getBoolean("hich", false);


        // Now set the switch states based on the retrieved values
        switchAfterPrayer.setChecked(afterPrayer);
        switchBeforePrayer.setChecked(beforePrayer);
        switchAfterIsha.setChecked(afterIsha);

        ScheduleNotification.scheduleKhatmNotification(this); // Schedule on app start

        switchAfterPrayer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchBeforePrayer.setChecked(false);
            switchAfterIsha.setChecked(false);
            editorNotification.putBoolean("switchAfterPrayer", isChecked); // Save the actual state
            editorNotification.putBoolean("switchBeforePrayer", false); // Save the actual state
            editorNotification.putBoolean("switchAfterIsha", false); // Save the actual state

            editorNotification.apply(); // Apply the changes


            ScheduleNotification.scheduleKhatmNotification(this); // Schedule on app start
        });

        switchBeforePrayer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchAfterIsha.setChecked(false);
            switchAfterPrayer.setChecked(false);
            editorNotification.putBoolean("switchBeforePrayer", isChecked); // Save the actual state
            editorNotification.putBoolean("switchAfterPrayer", false); // Save the actual state
            editorNotification.putBoolean("switchAfterIsha", false); // Save the actual state


            editorNotification.apply(); // Apply the changes


            ScheduleNotification.scheduleKhatmNotification(this); // Schedule on app start
        });

        switchAfterIsha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchBeforePrayer.setChecked(false);
            switchAfterPrayer.setChecked(false);
            editorNotification.putBoolean("switchAfterIsha", isChecked); // Save the actual state
            editorNotification.putBoolean("switchAfterPrayer", false); // Save the actual state
            editorNotification.putBoolean("switchBeforePrayer", false); // Save the actual state

            editorNotification.apply(); // Apply the changes

            ScheduleNotification.scheduleKhatmNotification(this); // Schedule on app start
        });

        btnBack.setOnClickListener( v -> {
            onBackPressed();
        });

        sharedPreferences = getSharedPreferences("Khatm", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize Juz to 1 if not set
        currentJuz = sharedPreferences.getInt("currentJuz", 1);
        QuranPageUtils.KhatmNumber = sharedPreferences.getInt("KhatmNumber", 1);

        txtKhatmNUmber.setText(""+QuranPageUtils.KhatmNumber);
        KhatmNumberS = sharedPreferences.getInt("KhatmNumber", 1);


         layoutStartVisibility = sharedPreferencesLayout.getInt("LayoutStart", View.GONE);
         khatmENDLayoutVisibility = sharedPreferencesLayout.getInt("KhatmENDLayout", View.GONE);
         layoutSettingsVisibility = sharedPreferencesLayout.getInt("LayoutSettings", View.VISIBLE);

        LayoutStart.setVisibility(layoutStartVisibility);
        KhatmENDLayout.setVisibility(khatmENDLayoutVisibility);
        LayoutSettings.setVisibility(layoutSettingsVisibility);


       txtStartNow.setOnClickListener(v -> {
           for (int i = 1; i <= 604; i++) {
               // Format the number to ensure it's always three digits
               //  String formattedNumber = String.format("%03d", i);
               // Construct the URL with the formatted number
               // String url = pngLink + i + ".png";
               // Add the URL to the list
               // fileUrls.add(url);

               // Format the number to ensure it's always three digits
               String formattedNumber = String.format(Locale.ENGLISH,"%03d", i);
               // Construct the URL with the formatted number
               String url = pngLink + "page" + formattedNumber + ".png";
               // Add the URL to the list
               fileUrls.add(url);
           }


           fileUrlsMap.put(1,fileUrls);

           urls = fileUrlsMap.get(1);
           // Create a File object for the folder
           File folder = new File(this.getExternalFilesDir(null), folderName);

           missingFiles = new ArrayList<>();
           for (String url : urls) {
               // Extract file name from URL
               String fileName = url.substring(url.lastIndexOf('/') + 1);
               // Assuming your files are saved in the external storage directory
               File file = new File(folder, fileName);
               if (!file.exists()) {
                   missingFiles.add(url);
               }
           }

           if (missingFiles.isEmpty()) {
               // Toast.makeText(this, "Reciter selected ", Toast.LENGTH_SHORT).show();

               LayoutStart.setVisibility(View.VISIBLE);
               LayoutSettings.setVisibility(View.GONE);
               KhatmENDLayout.setVisibility(View.GONE);

               editorLayout.putInt("LayoutStart", View.VISIBLE); // Save the actual state
               editorLayout.putInt("LayoutSettings", View.GONE); // Save the actual state
               editorLayout.putInt("KhatmENDLayout", View.GONE); // Save the actual state
               editorLayout.apply();

           }
           else {
               AgadariFile();
           }

       });

        minusBtn.setOnClickListener(v -> {
            if (QuranPageUtils.KhatmNumber > 1) { // Ensure it doesn't go below 1
                --QuranPageUtils.KhatmNumber;
            }
            txtKhatmNUmber.setText("" + QuranPageUtils.KhatmNumber);

            // Update KhatmNumberS before updating UI
            KhatmNumberS = QuranPageUtils.KhatmNumber;

            editor.putInt("KhatmNumber", QuranPageUtils.KhatmNumber);
            editor.apply();

            updateJuzInfo(currentJuz);
        });

        plusBtn.setOnClickListener(v -> {
            if (QuranPageUtils.KhatmNumber < 30) {
                ++QuranPageUtils.KhatmNumber;
            }
            txtKhatmNUmber.setText("" + QuranPageUtils.KhatmNumber);

            // Update KhatmNumberS before updating UI
            KhatmNumberS = QuranPageUtils.KhatmNumber;

            editor.putInt("KhatmNumber", QuranPageUtils.KhatmNumber);
            editor.apply();

            updateJuzInfo(currentJuz);
        });

        progressBar.setProgress(currentJuz);
        juzzStart.setText("جوزئی  " + currentJuz);
        updateJuzInfo(currentJuz);

        btnDuaa.setOnClickListener(v -> {

            LayoutStart.setVisibility(layoutStartVisibility);
            KhatmENDLayout.setVisibility(khatmENDLayoutVisibility);
            LayoutSettings.setVisibility(layoutSettingsVisibility);

            Intent intent = new Intent(this, WallamakanQuestion.class);
            intent.putExtra("catId","2");
            intent.putExtra("ActName","Khatm");
            startActivity(intent);

        });
        btnNew.setOnClickListener(v -> {
            LayoutStart.setVisibility(View.GONE);
            KhatmENDLayout.setVisibility(View.GONE);
            LayoutSettings.setVisibility(View.VISIBLE);

            editorLayout.putInt("LayoutStart", View.GONE); // Save the actual state
            editorLayout.putInt("KhatmENDLayout", View.GONE); // Save the actual state
            editorLayout.putInt("LayoutSettings", View.VISIBLE); // Save the actual state
            editorLayout.apply();

        });

        nextDay.setOnClickListener(v -> {
             KhatmNumberS = sharedPreferences.getInt("KhatmNumber", 1);

                currentJuz += KhatmNumberS;


            if (currentJuz > 30) {
                currentJuz = 1; // Reset to Juz 1 after reaching Juz 30
                QuranPageUtils.KhatmNumber = 1;
                txtKhatmNUmber.setText("" +QuranPageUtils.KhatmNumber);
                editor.putInt("KhatmNumber", 1);
                editor.apply();

                // Reset to Notification
                switchAfterIsha.setChecked(false);
                switchBeforePrayer.setChecked(false);
                switchAfterPrayer.setChecked(false);

                editorNotification.putBoolean("switchAfterPrayer", false); // Save the actual state
                editorNotification.putBoolean("switchBeforePrayer", false); // Save the actual state
                editorNotification.putBoolean("switchAfterIsha", false); // Save the actual state

                editorNotification.apply(); // Apply the changes

                LayoutStart.setVisibility(View.GONE);
                LayoutSettings.setVisibility(View.GONE);
                KhatmENDLayout.setVisibility(View.VISIBLE);

                editorLayout.putInt("LayoutStart", View.GONE); // Save the actual state
                editorLayout.putInt("KhatmENDLayout", View.VISIBLE); // Save the actual state
                editorLayout.putInt("LayoutSettings", View.GONE); // Save the actual state
                editorLayout.apply();

            }
            // Reset last read page (so it starts at Juz start next day)
            editor.putInt("currentJuz", currentJuz);
            editor.putInt("lastReadPage", -1); // Reset to Juz start page
            editor.apply();

            progressBar.setProgress(currentJuz);

            juzzStart.setText("جوزئی  " + currentJuz);
            updateJuzInfo(currentJuz);
        });

        txtStart.setOnClickListener(v -> {// Get the selected Khatm number (1 Juz or 2 Juz per day)
            KhatmNumberS = sharedPreferences.getInt("KhatmNumber", 1);
            int lastReadPage = sharedPreferences.getInt("lastReadPage", -1);

            // Corrected index calculation
            int adjustedJuz = Math.min(Math.max(currentJuz - 1, 0), 29);

            // Fix: Use the correct Juz start page
            int startPage = (lastReadPage == -1) ? juzStartPage[currentJuz - 1] : lastReadPage;

            // Fix: Ensure the correct end page
            int endJuzIndex = Math.min(adjustedJuz + (KhatmNumberS - 1), 29);
            int endPage = juzEndPages[endJuzIndex];

            // Save last read page correctly
            editor.putInt("lastReadPage", startPage);
            editor.apply();

            // Pass the corrected values
            Intent intent = new Intent(this, QuranImageActivity.class);
            intent.putExtra("sura", "iSurat");
            intent.putExtra("sura_ar", "iSurat");
            intent.putExtra("id", "1");
            intent.putExtra("ACTName", "Khatm");
            QuranPageUtils.pageNumber = startPage;
            QuranPageUtils.pageNumberEnd = endPage;
            startActivity(intent);
        });
    }


    @SuppressLint("SetTextI18n")
    private void updateJuzInfo(int juzNumber) {
        if (juzNumber >= 1 && juzNumber <= 30) {
            int startJuzIndex = juzNumber - 1; // Adjust for 0-based index
            int endJuzIndex = Math.min(startJuzIndex + KhatmNumberS - 1, 29); // Prevent out of bounds

            // Start Juz details
            String startSuraName = juzStartSuraNames[startJuzIndex];
            int startSuraNumber = juzStartSuraNumbers[startJuzIndex];
            int startAyahNumber = juzStartAyahNumbers[startJuzIndex];
            int startPageNumber = juzStartPage[startJuzIndex];

            // End Juz details
            String endSuraName = juzEndSuraNames[endJuzIndex];
            int endSuraNumber = juzEndSuraNumbers[endJuzIndex];
            int endAyahNumber = juzEndAyahNumbers[endJuzIndex];
            int endPageNumber = juzEndPages[endJuzIndex];

            if (juzNumber == 2 || juzNumber == 3) { // Log only for Juz 2 and 3
                Log.d("JuzArrayData", "Juz " + juzNumber + ":");
                Log.d("JuzArrayData", "Start: Sura " + juzStartSuraNames[startJuzIndex] + " (" + juzStartSuraNumbers[startJuzIndex] + ":" + juzStartAyahNumbers[startJuzIndex] + ")");
                Log.d("JuzArrayData", "End: Sura " + juzEndSuraNames[endJuzIndex] + " (" + juzEndSuraNumbers[endJuzIndex] + ":" + juzEndAyahNumbers[endJuzIndex] + ")");
            }

            SuraStart.setText("لە سورەتی "+ startSuraName+" ئایەتی "+ startAyahNumber+ " لاپەڕە "+startPageNumber);
            SuraEnd.setText("بۆ سورەتی "+ endSuraName+" ئایەتی "+ endAyahNumber+ " لاپەڕە "+endPageNumber);

            String id = String.valueOf(startSuraNumber);
            String ayahId = String.valueOf(startAyahNumber);

            StoreDataInArrayList(id,ayahId);


        } else {
            SuraStart.setText(""); // Clear text if Juz number is invalid
            SuraEnd.setText("");
        }
    }


    @SuppressLint("SetTextI18n")
    void StoreDataInArrayList(String id , String ayahId) {
        Cursor cursor = mydbClass.readAllAyahDataKhatm(id, ayahId);
        StringBuilder allAyahText = new StringBuilder(); // Use StringBuilder for efficiency

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String ayahText = cursor.getString(cursor.getColumnIndexOrThrow("text"));
                    allAyahText.append(ayahText).append(" "); // Append each ayah with a space (or other separator)
                } while (cursor.moveToNext()); // Iterate through all rows
                AyaStart.setText(allAyahText.toString().trim()); // Set the combined text (trimming any extra space at the end)

            } else {
                AyaStart.setText("Ayah not found"); // Handle no results
            }
            cursor.close();
        }

    }


    public void  AgadariFile(){
        ViewGroup viewGroup =findViewById(android.R.id.content);

        Button btnSetting;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view= LayoutInflater.from(Khatm.this).inflate(R.layout.dialog_no_file_internet_layout,viewGroup,false);
        builder.setCancelable(false);
        builder.setView(view);


        btnSetting =view.findViewById(R.id.btnSetting);

        final   AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        btnSetting.setOnClickListener(v -> {
            downloadFiles();
            alertDialog.dismiss();
        });


        alertDialog.show();

    }


    @SuppressLint("MissingInflatedId")
    public void AgadariInternet() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        Button btnNo;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_no_internet_layout, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view);


        btnNo = view.findViewById(R.id.btnSetting);

        final AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        btnNo.setOnClickListener(v -> alertDialog.dismiss());


        alertDialog.show();

    }

    public void downloadFiles(){

        //DownloadFilesTask downloadTask = new DownloadFilesTask(this, missingFiles);
        // downloadTask.startDownload();
        if (missingFiles.isEmpty()) {
            // Toast.makeText(this, "Reciter selected ", Toast.LENGTH_SHORT).show();

        } else {

            // gar wifi yan mobile data habw ba download dastpebkat
            if (wifi.isConnected()|| mobileNetwork.isConnected()) {
                //  Toast.makeText(this, " " + missingFiles.toString(), Toast.LENGTH_LONG).show();
                new DownloadFilesTask(this, missingFiles).execute();
            }else {
                AgadariInternet();
            }
        }


    }



    @Override
    protected void onResume() {

        progressBar.setProgress(currentJuz);
        juzzStart.setText("جوزئی  " + currentJuz);
        txtKhatmNUmber.setText(""+QuranPageUtils.KhatmNumber);
        updateJuzInfo(currentJuz);

        LayoutStart.setVisibility(layoutStartVisibility);
        KhatmENDLayout.setVisibility(khatmENDLayoutVisibility);
        LayoutSettings.setVisibility(layoutSettingsVisibility);

        super.onResume();
    }
}