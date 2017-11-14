package devs.erasmus.epills.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.Drawer;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;
import com.viewpagerindicator.as.library.indicator.RecyclerCirclePageIndicator;
import com.viewpagerindicator.as.library.pageview.RecyclerViewPager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.model.MedicineQuantity;
import devs.erasmus.epills.widget.NavigationDrawer;
import devs.erasmus.epills.widget.PillCardAdapter;

public class ClockActivity extends AppCompatActivity {
    private List<MedicineQuantity> medicineQuantities;
    private PillCardAdapter pillCardAdapter;

    @BindView(R.id.list)
    RecyclerViewPager mRecyclerView;
    @BindView(R.id.analog_clock)
    CustomAnalogClock analogClock;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView (R.id.indicator)
    RecyclerCirclePageIndicator indicator;

    private Drawer drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();
        prepareAnalogClock();
        preparePillsAdapter();
        setExampleMedicines();

        analogClock.drawPill(21);


    }
    private void prepareAnalogClock(){
        analogClock.init(this, R.drawable.clock_face, R.drawable.hour_hand, R.drawable.minute_hand,R.drawable.pill_hand, 0, true, false);
        analogClock.setAutoUpdate(true);
        analogClock.setScale(1.1f);
    }
    private void preparePillsAdapter(){
        medicineQuantities = new ArrayList<>();

        pillCardAdapter = new PillCardAdapter(this,medicineQuantities);
        LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(pillCardAdapter);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        indicator.setViewPager(mRecyclerView);
        indicator.setRadius(15);
        indicator.setFillColor(Color.parseColor("#FF33B5E5"));


    }
    private void setExampleMedicines(){

        Date date =  new Date();
        IntakeMoment intake = new IntakeMoment(date,null);
        intake.save();


        Medicine medicine = new Medicine("Ibuprofeno","http://omicrono.elespanol.com/wp-content/uploads/2015/05/ibuprofeno.jpg");
        Medicine medicine2 = new Medicine("Paracetamol","https://www.supermadre.net/wp-content/uploads/2016/05/paracetamol.jpg");
        Medicine medicine3 = new Medicine("Strepsils","http://www.londondrugs.com/dw/image/v2/AAJI_PRD/on/demandware.static/-/Sites-londondrugs-master/default/dw39a84b23/products/L7861065/large/L7861065.JPG?sw=556&sh=680&sm=fit");


        MedicineQuantity a = new MedicineQuantity(intake,medicine);
        MedicineQuantity b = new MedicineQuantity(intake,medicine2);
        MedicineQuantity c = new MedicineQuantity(intake,medicine3);

        medicineQuantities.add(a);
        medicineQuantities.add(b);
        medicineQuantities.add(c);

        pillCardAdapter.notifyDataSetChanged();
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


}
