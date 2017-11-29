package devs.erasmus.epills.controller;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;
import com.viewpagerindicator.as.library.indicator.RecyclerCirclePageIndicator;
import com.viewpagerindicator.as.library.pageview.RecyclerViewPager;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devs.erasmus.epills.R;
import devs.erasmus.epills.broadcast_receiver.BootReceiver;
import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.utils.ClockUtils;
import devs.erasmus.epills.widget.NavigationDrawer;
import devs.erasmus.epills.widget.PillCardAdapter;

public class ClockActivity extends AppCompatActivity {
    private List<IntakeMoment> intakeMomentList;
    private PillCardAdapter pillCardAdapter;

    @BindView(R.id.list)
    RecyclerViewPager mRecyclerView;
    @BindView(R.id.analog_clock)
    CustomAnalogClock analogClock;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView (R.id.indicator)
    RecyclerCirclePageIndicator indicator;
    @BindView(R.id.noPills)
    TextView noPillsTV;
    @BindView(R.id.cards_layout)
    ConstraintLayout cards_layout;

    private static Drawer drawer;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ButterKnife.bind(this);
        LitePal.initialize(getApplicationContext());
        enableBootReceiver(true);
        setSupportActionBar(toolbar);
        parentLayout = findViewById(R.id.clock_layout);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();
        drawer.setSelection(1,false);

        prepareAnalogClock();
        preparePillsAdapter();
        setPills();

        if(getIntent().getBooleanExtra("pill",false)){
            pillAddedSuccess();
        }
        if(getIntent().getBooleanExtra("modified",false)){
            pillEditSuccess();
        }





    }
    private void prepareAnalogClock(){
        analogClock.init(this, R.drawable.clock_face, R.drawable.hour_hand, R.drawable.minute_hand,R.drawable.pill_hand,R.drawable.pill_highlight ,0, true, false);
        analogClock.setAutoUpdate(true);
        analogClock.setScale(1.1f);
    }
    private void preparePillsAdapter(){


        intakeMomentList = new ArrayList<>();
        pillCardAdapter = new PillCardAdapter(this,intakeMomentList);
        LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(pillCardAdapter);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        indicator.setViewPager(mRecyclerView);
        indicator.setRadius(12);
        indicator.setFillColor(Color.parseColor("#FF33B5E5"));

        mRecyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
              //  analogClock.highlightPill(newPosition);
            }

        });



    }
    private void setPills(){
        if(DataSupport.count(IntakeMoment.class)!=0) {
            noPillsTV.setVisibility(View.GONE);
            intakeMomentList.clear();
            pillCardAdapter.notifyDataSetChanged();

            Date today = new Date();
            Date tomorrow = ClockUtils.addDays(today,1);


            List<IntakeMoment> allIntake = DataSupport.where("startdate between "
            +today.getTime() +" and "+ tomorrow.getTime()).find(IntakeMoment.class);

            if(allIntake.size()==0){
                noPillsTV.setText(R.string.no_pills_today);
                noPillsTV.setVisibility(View.VISIBLE);
                cards_layout.setVisibility(View.GONE);

            }
            for (IntakeMoment intake : allIntake) {
                intake.setMedicine(DataSupport.find(Medicine.class, intake.getMedicineId()));
                intakeMomentList.add(intake);
                analogClock.drawPill(intake.getStartDate().getHours());
            }

            pillCardAdapter.notifyDataSetChanged();
        }
        else{
            noPillsTV.setText(R.string.no_pills_found);
            noPillsTV.setVisibility(View.VISIBLE);
            cards_layout.setVisibility(View.GONE);

        }
    }
    @OnClick(R.id.add_fab)
    void add_fab(){
        startActivity(new Intent(this,AddPillGeneralActivity.class));
    }

    public void pillAddedSuccess(){
        drawer.closeDrawer();

        Snackbar mySnackbar = Snackbar.make(parentLayout,
                R.string.pill_added_success, Snackbar.LENGTH_SHORT);
        mySnackbar.show();


    }
    public void pillEditSuccess(){
        drawer.closeDrawer();

        Snackbar mySnackbar = Snackbar.make(parentLayout,
                R.string.pill_edited_success, Snackbar.LENGTH_SHORT);
        mySnackbar.show();


    }

    @Override
    public void onBackPressed() {
        drawer.openDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sync_calendar) {

            return true;
        }

        return super.onOptionsItemSelected(item);
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
