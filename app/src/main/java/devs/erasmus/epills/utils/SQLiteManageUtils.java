package devs.erasmus.epills.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import devs.erasmus.epills.model.IntakeMoment;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

/**
 * Created by Lenovo-PC on 26/11/2017.
 */

public class SQLiteManageUtils {
    static String DATABASE_TABLE_NAME = "/data/data/devs.erasmus.epills/databases/ePills.db";
    static SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_TABLE_NAME, null, OPEN_READWRITE);

    static public void bootAlarmsRetrieve(Context context){
        //String[] columns = new String[]{"startdate", "enddate", "medicine", "quantity", "alarmrequestcode", "isonce"};
        Cursor intakeCursor = db.query("intakemoment", null, null, null, null, null, null);

        for (intakeCursor.moveToFirst(); !intakeCursor.isAfterLast(); intakeCursor.moveToNext()) {
            long startDateMillis = intakeCursor.getLong(intakeCursor.getColumnIndex("startdate"));
            long endDateMillis = intakeCursor.getLong(intakeCursor.getColumnIndex("enddate"));
            int quantity = intakeCursor.getInt(intakeCursor.getColumnIndex("quantity"));
            int alarmRequestCode = intakeCursor.getInt(intakeCursor.getColumnIndex("alarmrequestcode"));
            long medicineId = intakeCursor.getLong(intakeCursor.getColumnIndex("medicineid"));
            int isOnce = intakeCursor.getInt(intakeCursor.getColumnIndex("isonce"));
            Cursor medicineCursor = db.query("medicine", null, "id = " + String.valueOf(medicineId), null, null, null, null);
            String medicineName = "placeholder name";

            if (medicineCursor.moveToFirst()) {
                medicineName = medicineCursor.getString(medicineCursor.getColumnIndex("name"));
            }


            //if the alarm is already passed, set it off as a one-time alarm!
            if(startDateMillis < System.currentTimeMillis()){
                Date currentDate = Calendar.getInstance().getTime();

                Toast.makeText(context, "passed alarm", Toast.LENGTH_SHORT).show();
                AlarmUtil.setAlarm(context, medicineName, quantity, currentDate, currentDate, alarmRequestCode+1);
            }
            //else set it as usual
            else{
                Date startDate = SQLiteManageUtils.long2Date(startDateMillis);
                Date endDate = SQLiteManageUtils.long2Date(endDateMillis);

                AlarmUtil.setAlarm(context, medicineName, quantity, startDate, endDate, alarmRequestCode);
            }
            medicineCursor.close();
        }
        intakeCursor.close();
    }

    static public void deleteIntakeByAlarmId(int alarmId){
        if(db == null) throw new RuntimeException("db not initialized");

        String whereClause="alarmrequestcode =?";
        String[] whereArgs = new String[]{String.valueOf(alarmId)};
        db.delete("intakemoment",whereClause, whereArgs);
    }

    static public void updateIntake(int alarmId, long dateInMillis){
        ContentValues cv = new ContentValues();
        cv.put("startdate", dateInMillis);

        db.update("intakemoment", cv, "alarmrequestcode = "+String.valueOf(alarmId), null);
        Log.e("updating","intake");
    }


    static public Date long2Date(long dateInMillis){
        Date date = new Date();
        date.setTime(dateInMillis);

        return date;
    }
}
