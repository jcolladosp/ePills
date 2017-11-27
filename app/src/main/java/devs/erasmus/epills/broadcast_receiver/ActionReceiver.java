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
        int id = intent.getIntExtra("id", 69);

        if(action.equals("Skip")){
            skipMethod(context, id);
        }
        else if(action.equals("Snooze")){
            snoozeMethod(context, id);
        }
        else if(action.equals("Take")){
            takeMethod(context, id);
        }
        else{
            //
        }
    }

    private void skipMethod(Context context, int id) {
        Toast.makeText(context, "in skip method", Toast.LENGTH_SHORT).show();
        notificationManager.cancel(id);
    }

    private void snoozeMethod(Context context, int id){
        Toast.makeText(context, "in snooze method", Toast.LENGTH_SHORT).show();
        notificationManager.cancel(id);
    }

    private void takeMethod(Context context, int id){
        Toast.makeText(context, "in take method", Toast.LENGTH_SHORT).show();
        notificationManager.cancel(id);
    }
}
