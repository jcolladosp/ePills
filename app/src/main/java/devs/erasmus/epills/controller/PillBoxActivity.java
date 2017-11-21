package devs.erasmus.epills.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mikepenz.materialdrawer.Drawer;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.widget.NavigationDrawer;

public class PillBoxActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_box);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();

        test();
    }

    private void test(){
        Log.e("in","pillbox");
    }
}
