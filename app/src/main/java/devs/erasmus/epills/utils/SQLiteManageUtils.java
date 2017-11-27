package devs.erasmus.epills.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import devs.erasmus.epills.model.IntakeMoment;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

/**
 * Created by Lenovo-PC on 26/11/2017.
 */

public class SQLiteManageUtils {
    static String DATABASE_TABLE_NAME = "/data/data/devs.erasmus.epills/databases/ePills.db";
    static SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_TABLE_NAME, null, OPEN_READWRITE);

    static public void deleteIntakesByAlarmId(int alarmId){
        if(db==null) throw new RuntimeException("db not initialized");

        String whereClause="alarmrequestcode =?";
        String[] whereArgs = new String[]{String.valueOf(alarmId)};
        db.delete("intakemoment",whereClause, whereArgs);
    }
}
