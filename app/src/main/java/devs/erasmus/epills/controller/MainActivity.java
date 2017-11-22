package devs.erasmus.epills.controller;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);


        ButterKnife.bind(this);
        LitePal.initialize(getApplicationContext());
        enableBootReceiver(true);

        //TODO: remember to remove it, it's only for debug purpose
        DataSupport.deleteAll("intakeMoment");

        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();
        drawer.setSelection(-1);


    }

    public static void pillAddedSuccess(){
        drawer.closeDrawer();

        Snackbar mySnackbar = Snackbar.make(parentLayout,
                R.string.pill_added_success, Snackbar.LENGTH_SHORT);
        mySnackbar.show();


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
