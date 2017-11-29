package devs.erasmus.epills.controller;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;
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
import devs.erasmus.epills.widget.SquareImageView;

public class EditIntakeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar2)
    Toolbar toolbar;
    @BindView(R.id.image_view)
    SquareImageView imageView;
    @BindView(R.id.time_text)
    TextInputEditText time_text;
    @BindView(R.id.seekbar)
    DiscreteSeekBar seekBar;
    @BindView(R.id.switch1)
    Switch aSwitch;

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
    @OnClick(R.id.time_text)
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(intakeMoment.getStartDate());

        intakeMoment.setQuantity(seekBar.getProgress());
        int hour = Integer.parseInt(time_text.getText().toString().substring(0,2));
        int minutes = Integer.parseInt(time_text.getText().toString().substring(3,5));
        Date oldStartDate = intakeMoment.getStartDate();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);

        Date startDate = calendar.getTime();
        intakeMoment.setStartDate(startDate);
        intakeMoment.setSwitchState(aSwitch.isChecked());
        intakeMoment.save();

        //the cheat begin:
        //reset the current alarm with same date but with notification disable
        //no one will notice it
        if(intakeMoment.getIsOnce()==0) {
            AlarmUtil.setAlarm(this, medicine.getName(), intakeMoment.getQuantity(),
                    oldStartDate,
                    intakeMoment.getEndDate(),
                    intakeMoment.getAlarmRequestCode(),
                    false);
        }
        //set new alarm as a one-time alarm with the new set alarm date
        AlarmUtil.setAlarm(this, medicine.getName(), intakeMoment.getQuantity(),
                startDate,
                startDate,
                intakeMoment.getAlarmRequestCode()+69,
                intakeMoment.getSwitchState());

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
