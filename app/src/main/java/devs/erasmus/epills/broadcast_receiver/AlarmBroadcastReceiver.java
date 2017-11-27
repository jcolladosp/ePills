package devs.erasmus.epills.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import devs.erasmus.epills.utils.LitePalManageUtil;
import devs.erasmus.epills.utils.SQLiteManageUtils;
import devs.erasmus.epills.widget.NotificationService;

import static devs.erasmus.epills.utils.AlarmUtil.cancelAlarm;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int end = intent.getIntExtra("end", 0);
        String medicineName = intent.getStringExtra("medicineName");
        int quantity = intent.getIntExtra("quantity", 69);
        int alarmId = intent.getIntExtra("alarmId", 69);
        boolean isOnce = intent.getBooleanExtra("isOnce", true);

        if(end==1){ //end alarm!
            cancelAlarm(context, alarmId);
        }
        else {
            Log.e("ringing", medicineName + String.valueOf(alarmId));

            //create intent goto NotificationService
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.putExtra("medicineName", medicineName);
            serviceIntent.putExtra("alarmId", alarmId);
            serviceIntent.putExtra("quantity", quantity);



            if(isOnce){
                SQLiteManageUtils.deleteIntakesByAlarmId(alarmId);
            }
            //PUT EXTRAS FOR NOTIFICATION INFOS
            context.startService(serviceIntent);
        }
    }
}
