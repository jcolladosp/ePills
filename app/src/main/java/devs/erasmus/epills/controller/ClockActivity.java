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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.model.MedicineQuantity;
import devs.erasmus.epills.widget.PillCardAdapter;

public class ClockActivity extends AppCompatActivity {
    private List<MedicineQuantity> medicineQuantities;
    private PillCardAdapter pillCardAdapter;

    @BindView(R.id.list)
    RecyclerViewPager mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        medicineQuantities = new ArrayList<>();

        pillCardAdapter = new PillCardAdapter(this,medicineQuantities);
        // setLayoutManager like normal RecyclerView, you do not need to change any thing.
        LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layout);

        //set adapter
        //You just need to implement ViewPageAdapter by yourself like a normal RecyclerView.Adpater.
        mRecyclerView.setAdapter(pillCardAdapter);

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

}
