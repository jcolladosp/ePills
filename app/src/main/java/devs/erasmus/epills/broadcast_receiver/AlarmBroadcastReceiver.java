package devs.erasmus.epills.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Receiver","okay");
        Toast.makeText(context, "IT'S TIME YOU MORON", Toast.LENGTH_SHORT).show();
        //create intent goto NotificationService
        //Intent serviceIntent=new Intent(context,NotificationService.class);
        //context.startService(serviceIntent);
    }
}
