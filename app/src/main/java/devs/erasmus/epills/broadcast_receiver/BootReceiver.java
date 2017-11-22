package devs.erasmus.epills.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import devs.erasmus.epills.model.IntakeMoment;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class BootReceiver extends BroadcastReceiver {
    String DATABASE_TABLE_NAME = "/data/data/devs.erasmus.epills/databases/ePills.db";

    //Unfortunately we can't inizialize LitePal without an application context, so we need to inizialize
    // the DB for retrieving the alarms using standard SQLite
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarms here
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_TABLE_NAME, null, OPEN_READWRITE);

           String[] columns = new String[]{"startDate", "endDate", "receipt", "medicine", "quantity", "alarmRequestCode", "isAlarmSet"};
            Cursor c = db.query("IntakeMoment", columns, null, null, null, null, null);

            int i = 0;
            int test = 0;

            if(c.getCount() > 0) {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    test = c.getInt(1);
                    Toast.makeText(context, String.valueOf(test), Toast.LENGTH_SHORT).show();
                    i++;
                }
            }
        }

    }
}
