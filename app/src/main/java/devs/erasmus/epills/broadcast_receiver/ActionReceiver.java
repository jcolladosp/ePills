package devs.erasmus.epills.broadcast_receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ActionReceiver extends BroadcastReceiver {

    NotificationManager notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        String action = intent.getStringExtra("action");
        int notificationId = intent.getIntExtra("id", 69);
        int alarmId = intent.getIntExtra("alarmId", 69);

        if(action.equals("Skip")){
            skipMethod(context, alarmId);
        }
        else if(action.equals("Snooze")){
            snoozeMethod(context, alarmId);
        }
        else{
            takeMethod(context, alarmId);
        }

        notificationManager.cancel(notificationId);
    }

    private void skipMethod(Context context, int alarmId) {
        Toast.makeText(context, "Skipped intake", Toast.LENGTH_SHORT).show();


    }

    private void snoozeMethod(Context context, int alarmId){
        Toast.makeText(context, "Snoozed", Toast.LENGTH_SHORT).show();
    }

    private void takeMethod(Context context, int alarmId){
        Toast.makeText(context, "Took intake", Toast.LENGTH_SHORT).show();
    }
}
