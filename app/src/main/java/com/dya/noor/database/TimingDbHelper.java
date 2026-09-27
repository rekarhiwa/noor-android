package com.dya.noor.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dya.noor.module.AyahTiming;

import java.io.File;
import java.util.ArrayList;

public class TimingDbHelper {

    private final SQLiteDatabase db;

    public TimingDbHelper(Context context , String DataBaseName) {
        File dbFile = context.getDatabasePath(DataBaseName+".db");
        db = SQLiteDatabase.openDatabase(
                dbFile.getPath(),
                null,
                SQLiteDatabase.OPEN_READONLY
        );
    }

    public ArrayList<AyahTiming> getTimings(int suraId) {
        ArrayList<AyahTiming> list = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT sura, ayah, time FROM timings WHERE sura = ? ORDER BY time ASC",
                new String[]{String.valueOf(suraId)}
        );

        while (c.moveToNext()) {
            list.add(new AyahTiming(
                    c.getInt(0),
                    c.getInt(1),
                    c.getLong(2)
            ));
        }
        c.close();
        return list;
    }
}
