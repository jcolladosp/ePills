package devs.erasmus.epills.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import devs.erasmus.epills.R;
import devs.erasmus.epills.utils.ClockUtils;


public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        startSplash();
    }
    private void startSplash() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(ClockUtils.isFirstTime(getApplicationContext())) {
                    ClockUtils.setFirstTimeDone(getApplicationContext());
                    Intent i = new Intent(getApplicationContext(), ClockActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(getApplicationContext(), ClockActivity.class);
                    startActivity(i);
                    finish();
                }
            }

        }, 1500);
    }

}
