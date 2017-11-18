package devs.erasmus.epills.broadcast_receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import devs.erasmus.epills.widget.NotificationService;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int end = intent.getIntExtra("end", 0);
        String medicineName = intent.getStringExtra("medicineName");
        int quantity = intent.getIntExtra("quantity", 69);
        int alarmId = intent.getIntExtra("alarmId", 69);

        if(end==1){ //end alarm!
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,alarmId, intent, 0);
            alarmManager.cancel(pendingIntent);
            Log.e("alarm cancelled",String.valueOf(alarmId));
        }
        else {
            Log.e("this", medicineName + String.valueOf(alarmId));
            //create intent goto NotificationService
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.putExtra("medicineName", medicineName);
            serviceIntent.putExtra("alarmId", alarmId);
            serviceIntent.putExtra("quantity", quantity);

            //PUT EXTRAS FOR NOTIFICATION INFOS
            context.startService(serviceIntent);
        }
    }
}
