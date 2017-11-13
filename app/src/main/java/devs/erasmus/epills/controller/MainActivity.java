package devs.erasmus.epills.controller;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;

import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
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

        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();
        drawer.setSelection(-1);

        pillAddedSuccess();

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

}
