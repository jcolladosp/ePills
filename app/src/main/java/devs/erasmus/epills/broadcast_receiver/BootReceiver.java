package devs.erasmus.epills.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.litepal.LitePal;

import devs.erasmus.epills.utils.OnBootResumeAlarms;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarms here
            new OnBootResumeAlarms(context);
        }

    }
}
