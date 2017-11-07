package devs.erasmus.epills.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

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

    private Drawer drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //toolbar.setTitle(R.string.title_activity_clock);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();

        analogClock.init(this, R.drawable.clock_face, R.drawable.hour_hand, R.drawable.minute_hand, 0, true, false);
        analogClock.setAutoUpdate(true);
        analogClock.setScale(1.1f);


        medicineQuantities = new ArrayList<>();
        pillCardAdapter = new PillCardAdapter(this,medicineQuantities);
        LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(pillCardAdapter);
        mRecyclerView.setScrollbarFadingEnabled(false);

        Date date =  new Date();
        IntakeMoment intake = new IntakeMoment(0,date,null,null);
        Medicine medicine = new Medicine(0,"Ibuprofeno","http://omicrono.elespanol.com/wp-content/uploads/2015/05/ibuprofeno.jpg");
        Medicine medicine2 = new Medicine(0,"Paracetamol","https://www.supermadre.net/wp-content/uploads/2016/05/paracetamol.jpg");
        Medicine medicine3 = new Medicine(0,"Strepsils","http://www.londondrugs.com/dw/image/v2/AAJI_PRD/on/demandware.static/-/Sites-londondrugs-master/default/dw39a84b23/products/L7861065/large/L7861065.JPG?sw=556&sh=680&sm=fit");


        MedicineQuantity a = new MedicineQuantity(1,intake,medicine);
        MedicineQuantity b = new MedicineQuantity(2,intake,medicine2);
        MedicineQuantity c = new MedicineQuantity(3,intake,medicine3);

        medicineQuantities.add(a);
        medicineQuantities.add(b);
        medicineQuantities.add(c);

        pillCardAdapter.notifyDataSetChanged();



    }

    @Override
    public void onBackPressed() {
        drawer.openDrawer();
    }


}
