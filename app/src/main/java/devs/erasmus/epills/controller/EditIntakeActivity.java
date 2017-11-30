package devs.erasmus.epills.controller;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devs.erasmus.epills.R;
import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.utils.AlarmUtil;
import devs.erasmus.epills.utils.LitePalManageUtil;
import devs.erasmus.epills.utils.SQLiteManageUtils;
import devs.erasmus.epills.widget.SquareImageView;

public class EditIntakeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar2)
    Toolbar toolbar;
    @BindView(R.id.image_view)
    SquareImageView imageView;
    @BindView(R.id.time_tv)
    TextView time_text;
    @BindView(R.id.seekbar)
    DiscreteSeekBar seekBar;
    @BindView(R.id.switch1)
    Switch aSwitch;
    @BindView(R.id.time_layout)
    ConstraintLayout time_layout;

    private long intakeID;
    private IntakeMoment intakeMoment;
    private Medicine medicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_intake);
        ButterKnife.bind(this);
        //setSupportActionBar(toolbar);
        intakeID = getIntent().getLongExtra("intakeID",-1);
        intakeMoment  = DataSupport.find(IntakeMoment.class,intakeID);
        medicine = DataSupport.find(Medicine.class,intakeMoment.getMedicineId());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.pill_placeholder);
        requestOptions.error(R.drawable.pill_placeholder);
        requestOptions.centerCrop();
        String image_path= medicine.getImage();

        Glide.with(this).setDefaultRequestOptions(requestOptions).load(image_path).into(imageView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(medicine.getName());
        imageView.setBackgroundColor(getResources().getColor(R.color.grey));

        time_text.setText(String.format("%02d:%02d", intakeMoment.getStartDate().getHours(),intakeMoment.getStartDate().getMinutes()));
        seekBar.setProgress(intakeMoment.getQuantity());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editintake, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.delete_intake) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @OnClick(R.id.time_layout)
    void onText(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(intakeMoment.getStartDate());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        time_text.setText(String.format("%02d:%02d", hourOfDay,minute));
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }
    @OnClick(R.id.fab_edit)
    void onFab(){
        //saving the old values in case we need to set a multi-time alarm
        int oldQuantity = intakeMoment.getQuantity();
        Date oldStartDate = intakeMoment.getStartDate();
        int oldIsOnce = intakeMoment.getIsOnce();

        int newQuantity = seekBar.getProgress();
        int hour = Integer.parseInt(time_text.getText().toString().substring(0,2));
        int minutes = Integer.parseInt(time_text.getText().toString().substring(3,5));
        Calendar newCalendar = Calendar.getInstance();

        newCalendar.set(Calendar.HOUR_OF_DAY, hour);
        newCalendar.set(Calendar.MINUTE, minutes);
        Date newStartDate = newCalendar.getTime();

        //update intake moment with new dates
        intakeMoment.setStartDate(newStartDate);
        intakeMoment.setQuantity(newQuantity);
        intakeMoment.setSwitchState(aSwitch.isChecked());
        intakeMoment.setIsOnce(1);
        intakeMoment.save();

        //set refreshed alarm as a one-time alarm with the new startdate
        AlarmUtil.setAlarm(this, medicine.getName(),
                intakeMoment.getQuantity(),
                intakeMoment.getStartDate(),
                intakeMoment.getStartDate(),
                intakeMoment.getAlarmRequestCode(),
                intakeMoment.getSwitchState());

        //OLD INTAKE & ALARM LOGIC

        //check if i need to set next week alarm
        if(oldIsOnce==0) {
            long startDateInMillis = oldStartDate.getTime();
            long endDateInMillis = intakeMoment.getEndDate().getTime();
            long currentTime = System.currentTimeMillis();

            //if end-date isnt come yet, re-set old multi-time alarm with new id to the next week
            if(endDateInMillis > currentTime){ //end date isnt come yet
                int newAlarmId = (int) System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startDateInMillis);

                //refresh startDate to the next week date
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);

                //if next date is before the endDate, create a new alarm e intake
                if(calendar.getTimeInMillis() < endDateInMillis) {
                    Date startDate = calendar.getTime();
                    Date endDate = intakeMoment.getEndDate();

                    //set new intakemoment for the old multi-time alarm
                    IntakeMoment newIntakeMoment = new IntakeMoment(startDate, endDate,
                            intakeMoment.getReceipt(),
                            intakeMoment.getMedicineId(),
                            oldQuantity,
                            newAlarmId,
                            0);

                    newIntakeMoment.save();

                    AlarmUtil.setAlarm(this, medicine.getName(), newIntakeMoment.getQuantity(),
                            startDate,
                            endDate,
                            newAlarmId,
                            true);
                }
            }
        }
        finish();
        Intent i = new Intent(this,ClockActivity.class);
        i.putExtra("modified",true);
        startActivity(i);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
