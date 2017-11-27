package devs.erasmus.epills.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Date;

import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.utils.AlarmUtil;
import devs.erasmus.epills.utils.LitePalManageUtil;
import devs.erasmus.epills.utils.SQLiteManageUtils;
import devs.erasmus.epills.widget.NotificationService;

import static devs.erasmus.epills.utils.AlarmUtil.cancelAlarm;
import static devs.erasmus.epills.utils.AlarmUtil.setAlarm;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicineName");
        int quantity = intent.getIntExtra("quantity", 69);
        int alarmId = intent.getIntExtra("alarmId", 69);
        int isOnce = intent.getIntExtra("isOnce", 1);

        Log.e("ringing", medicineName + String.valueOf(alarmId));

        //create intent goto NotificationService
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("medicineName", medicineName);
        serviceIntent.putExtra("alarmId", alarmId);
        serviceIntent.putExtra("quantity", quantity);
        //PUT EXTRAS FOR NOTIFICATION INFOS
        context.startService(serviceIntent);

        if(isOnce==1){
            SQLiteManageUtils.deleteIntakeByAlarmId(alarmId);
        }
        else {
            long startDateInMillis = intent.getLongExtra("startDate", 0);
            long endDateInMillis = intent.getLongExtra("endDate", 0);

            if(endDateInMillis > System.currentTimeMillis()){ //end date isnt come yet
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startDateInMillis);

                //refresh startDate to the next date
                while (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
                }

                //if next date is before the endDate, create a new alarm
                if(calendar.getTimeInMillis() < endDateInMillis) {
                    Date startDate = calendar.getTime();
                    Date endDate = SQLiteManageUtils.long2Date(endDateInMillis);

                    AlarmUtil.setAlarm(context, medicineName, quantity, startDate, endDate, alarmId);
                    SQLiteManageUtils.updateIntake(alarmId, calendar.getTimeInMillis()); //update intake to refreshed startDate
                }
                //else remove the intake
                else{
                    SQLiteManageUtils.deleteIntakeByAlarmId(alarmId);
                }
            }
            else{
                SQLiteManageUtils.deleteIntakeByAlarmId(alarmId);
            }
        }
    }
}
