package devs.erasmus.epills.controller;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.broadcast_receiver.BootReceiver;
import devs.erasmus.epills.widget.NavigationDrawer;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static Drawer drawer;
    private static View parentLayout;
    private static Button panicButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);
        panicButton = (Button) findViewById(R.id.panic_button);

        ButterKnife.bind(this);
        LitePal.initialize(getApplicationContext());
        enableBootReceiver(true);

        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSupport.deleteAll("intakeMoment");
                Toast.makeText(MainActivity.this, "Restart device now", Toast.LENGTH_SHORT).show();
            }
        });

        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();
        drawer.setSelection(-1);


    }



    @Override
    public void onBackPressed() {
        drawer.openDrawer();
    }

    private void enableBootReceiver(boolean b) {
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();

        if(b){
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
        else{
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

}
