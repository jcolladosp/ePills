package devs.erasmus.epills.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.utils.AlarmUtil;
import devs.erasmus.epills.utils.SQLiteManageUtils;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class BootReceiver extends BroadcastReceiver {
    String DATABASE_TABLE_NAME = "/data/data/devs.erasmus.epills/databases/ePills.db";

    //Unfortunately we can't inizialize LitePal without an application context, so we need to inizialize
    // the DB for retrieving the alarms using standard SQLite

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SQLiteManageUtils.bootAlarmsRetrieve(context);
        }
    }
}
