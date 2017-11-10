package devs.erasmus.epills.controller;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mikepenz.materialdrawer.Drawer;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.widget.TimePickerFragment;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class AddPillSetTime extends AppCompatActivity implements VerticalStepperForm{
    final String PACKAGENAME = "devs.erasmus.epills.contoller";
    final String EXTRA_MEDICINEID = PACKAGENAME + "medicine_id";

    //Stepnumbers of each element
    String[] mySteps = {"Repetition","Time", "Date"};
    private final int REPETITION_STEP = 0;
    private final int TIME_STEP = 1;
    private final int DATE_STEP = 2;
    //Bind views
    @BindView(R.id.stepper)
    VerticalStepperFormLayout verticalStepper;
    @BindView(R.id.toolbar)
    Toolbar toolBar;

    //Stepper
    TextView timeTextView;
    TextView dateTextView;

    private TimePickerDialog timePicker;
    private Pair<Integer, Integer> time;
    private Drawer drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill_set_time);
        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance();
        time = new Pair(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
        setTimePicker(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        setSupportActionBar(toolBar);
        //TODO: drawer: Yes or no? TOOLBAR: how?

        VerticalStepperFormLayout.Builder.newInstance(verticalStepper, mySteps, this, this)
                .primaryColor(ContextCompat.getColor(getApplicationContext(),R.color.primary))
                .primaryDarkColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark))
                .displayBottomNavigation(false)
                .init();

    }

    private void setTimePicker(final int hour, int minute) {
        timePicker = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time = new Pair<>(hourOfDay, minute);
                        timeTextView.setText(getTimeString());
                    }
                }, hour, minute, true);
    }

    @NonNull
    private String getTimeString() {
        String hourString = ((time.first<9) ? "0"+ time.first : String.valueOf(time.first));
        String minuteString = (time.second<9) ? "0" + time.second : String.valueOf(time.second);
        String time = hourString + ":" + minuteString;
        return hourString + ":" + minuteString;
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case TIME_STEP:
                view = createTimeStep();
                break;
            case DATE_STEP:
                view = createDateStep();
                break;
        }
        return view;
    }

    private View createDateStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        ConstraintLayout dateStepContent =
                (ConstraintLayout) inflater.inflate(R.layout.stepper_time,null, false);
        dateTextView = dateStepContent.findViewById(R.id.time_label);
        dateTextView.setText("iajsdoijaoisdu");
        return dateStepContent;
    }

    private View createTimeStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        ConstraintLayout timeStepContent =
                (ConstraintLayout) inflater.inflate(R.layout.stepper_time, null, false);
        timeTextView = timeStepContent.findViewById(R.id.time_label);
        timeTextView.setText(getTimeString());
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });
        return timeStepContent;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case TIME_STEP:
                verticalStepper.setActiveStepAsCompleted();
                break;
            case DATE_STEP:
                verticalStepper.setActiveStepAsCompleted();
                break;
        }
    }

    @Override
    public void sendData() {

    }
}
