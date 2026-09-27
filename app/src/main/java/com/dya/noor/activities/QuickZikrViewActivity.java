package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.ZikrViewAdapter2;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.ZikrCount;

import java.util.ArrayList;

public class QuickZikrViewActivity extends BaseActivity {


    MydbClass mydbClass;
    SQLiteDatabase db;
    String id;
    RecyclerView zRecyclerView;
    TextView txtZikrName, duration;
    SeekBar seekBar2;
    ArrayList aArZ, aKrZ;
    CardView cardViewPlay2;
    ImageView btnPlay2 , back;
    MediaPlayer mediaPlayer2;
    Handler handler2 = new Handler();
    Runnable runnable2;
    ArrayList<Integer> aCount;
    ArrayList<Integer> minList;
    ArrayList<Integer> maxList ;

    ConstraintLayout mainLayoutOfzikr;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhikr_view2);
        zRecyclerView=findViewById(R.id.zRecyclerView2);

        txtZikrName = findViewById(R.id.txtZikrName2);
        mainLayoutOfzikr = findViewById(R.id.mainLayoutOfzikr);
        cardViewPlay2 = findViewById(R.id.cardViewPlay2);
        btnPlay2 = findViewById(R.id.btnPlay2);
        seekBar2 = findViewById(R.id.seek_bar2);
        back = findViewById(R.id.back);
        mediaPlayer2= new MediaPlayer();
        mydbClass = new MydbClass(this);
        db=mydbClass.getWritableDatabase();
        duration = findViewById(R.id.duration);
        aArZ = new ArrayList<>();
        aKrZ = new ArrayList<>();
        aCount = new ArrayList<>();

        minList = new ArrayList<>();
        maxList = new ArrayList<>();

        id=getIntent().getStringExtra("id");

        back.setOnClickListener(view -> onBackPressed());


        StoreDataInArrayList();

        ZikrViewAdapter2 zikrViewAdapter2 = new ZikrViewAdapter2(this,  aArZ,aKrZ, minList, maxList);
        zRecyclerView.setAdapter(zikrViewAdapter2);
        zRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtZikrName.setText(getIntent().getStringExtra("sura"));

        if (txtZikrName.getText().toString().equals("بەیانیان")){
            mediaPlayer2 = MediaPlayer.create(this,R.raw.b);
        }
        else if (txtZikrName.getText().toString().equals("ئێواران")){
            mediaPlayer2 = MediaPlayer.create(this,R.raw.e);
        }
        else {
            mediaPlayer2 = MediaPlayer.create(this,R.raw.x);
        }





        btnPlay2.setOnClickListener(v -> {


            if (!mediaPlayer2.isPlaying()) {

                mediaPlayer2.start();
                btnPlay2.setImageResource(R.drawable.ic_pause);
                seekBar2.setMax(mediaPlayer2.getDuration());
                handler2.postDelayed(runnable2, 0);

            }else {
                mediaPlayer2.pause();
                btnPlay2.setImageResource(R.drawable.ic_play);
            }




        });
///////////////////////////////////////////
        runnable2 = new Runnable() {
            @Override
            public void run() {
                seekBar2.setProgress(mediaPlayer2.getCurrentPosition());
                int currentDuration;
                // if (mediaPlayer.isPlaying()) {
                currentDuration = mediaPlayer2.getCurrentPosition();
                updatePlayer(currentDuration);
                duration.postDelayed(this, 1000);
                //   }else {
                duration.removeCallbacks(this);
                //  }
                handler2.postDelayed(this,500);

            }
        };


        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Check condition
                if(fromUser){
                    // when drag the seek bar
                    // set progress on seek bar
                    mediaPlayer2.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer2.seekTo(seekBar.getProgress());

            }
        });


        mediaPlayer2.setOnCompletionListener(mp -> {
            btnPlay2.setImageResource(R.drawable.ic_play);
            mediaPlayer2.seekTo(0);
        });

    }

    void StoreDataInArrayList(){

        Cursor cursor = mydbClass.readAllAzkar_item(id,"ckb");

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "no data ", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {

                String arText = cursor.getString(1);
                String krText = cursor.getString(2);

                aArZ.add(arText);
                aKrZ.add(krText);


                ZikrCount count = extractNumbers(arText);

                minList.add(count.min);
                maxList.add(count.max);
            }
        }
    }
    private void updatePlayer(int currentDuration){

        duration.setText("" + milliSecondsToTimer((long) currentDuration));
    }

    public  String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        mediaPlayer2.stop();
        super.onBackPressed();
        finish();
    }


    public ZikrCount extractNumbers(String text) {

        if (text == null) return new ZikrCount(1,1);

        ArrayList<Integer> numbers = new ArrayList<>();

        if (text.contains("واحدة")) numbers.add(1);
        if (text.contains("مرتين") || text.contains("اثنتين") || text.contains("اثنين")) numbers.add(2);
        if (text.contains("ثلاث")) numbers.add(3);
        if (text.contains("أربع")) numbers.add(4);
        if (text.contains("خمس")) numbers.add(5);
        if (text.contains("ست")) numbers.add(6);
        if (text.contains("سبع")) numbers.add(7);
        if (text.contains("ثمان")) numbers.add(8);
        if (text.contains("تسع")) numbers.add(9);
        if (text.contains("عشر")) numbers.add(10);
        if (text.contains("مائة")) numbers.add(100);

        if (numbers.size() == 0) return new ZikrCount(1,1);

        int min = numbers.get(0);
        int max = numbers.get(0);

        for (int n : numbers) {
            if (n < min) min = n;
            if (n > max) max = n;
        }

        return new ZikrCount(min, max);
    }


}