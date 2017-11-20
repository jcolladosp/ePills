package devs.erasmus.epills.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.model.Receipt;
import devs.erasmus.epills.utils.AlarmUtil;
import devs.erasmus.epills.widget.AddPillFinishDialog;
import devs.erasmus.epills.R;
import devs.erasmus.epills.model.Medicine;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class AddPillSetTime extends AppCompatActivity implements VerticalStepperForm{
    //STATE STRINGS
    private final static String
            STATE_STARTDATE = "STATE_STARTDATE",
            STATE_ENDDATE = "State_ENDDATE",
            STATE_SUBTITLES = "STATE_SUBTITLES",
            STATE_WEEK = "STATE_WEEK",
            STATE_REP_Single= "STATE_REP_SINGLE";

    final static String PACKAGENAME = "devs.erasmus.epills.contoller";
    public final static String EXTRA_MEDICINEID = PACKAGENAME + "medicine_id";
    public final static String EXTRA_RECEIPTID = PACKAGENAME +"receipt_id";

    //Database variables
    Medicine medicine;
    Receipt receipt;

    //Stepnumbers of each element
    String[] stepTitles;
    String[] stepSubTitles;

    private final int TIME_STEP = 0;
    private final int DATE_STEP = 1;
    private final int QUANTITY_STEP = 2;
    private final int REPETITION_STEP = 3;
    private final int TOTAL_STEPS = 4;

    //Stepper-Views
    private TextView timeTextView;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private ConstraintLayout repetitionContent;
    private ConstraintLayout quantityContent;
    private RadioButton singleRadioButton;
    private RadioButton multiRadioButton;
    private DiscreteSeekBar seekBar;
    private TimePickerDialog timePicker;
    private DatePickerDialog startDatePicker;
    private DatePickerDialog endDatePicker;

    //State variables
    private Calendar startDate; //Holds the current startDate.
    private Calendar endDate;
    private boolean[] weekdaysSelection = new boolean[7];
    private boolean singleSelected = true; //First time the single button is selected.


    //Bind views
    @BindView(R.id.stepper)
    VerticalStepperFormLayout verticalStepper;
    @BindView(R.id.toolbar)
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill_set_time);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);

        //TODO: drawer: Yes or no? TOOLBAR: how?
        //Get the medicine object from DB
        int medicineId = getIntent().getIntExtra(EXTRA_MEDICINEID, -1);
        long receiptId = getIntent().getLongExtra(EXTRA_RECEIPTID, -1);
        if (medicineId == -1 || receiptId == -1l) {
            throw new RuntimeException("No ID for medicine!");
        }

        receipt = DataSupport.where("id = ? ", receiptId+"").find(Receipt.class).get(0);
        medicine = DataSupport.find(Medicine.class, medicineId);

        String title = getString(R.string.timeactivity_title) + " " + medicine.getName();
        setTitle(title);

        //load names of step in arrays
        stepTitles = new String[]{
                getResources().getString(R.string.time_label),
                getResources().getString(R.string.startDate_Label),
                getResources().getString(R.string.quantity_label),
                getResources().getString(R.string.repetition_Label)};

        if (savedInstanceState == null) { //If the activity is shown the first time
            stepSubTitles = new String[]{
                    getResources().getString(R.string.time_descr),
                    getResources().getString(R.string.startDate_descr),
                    getResources().getString(R.string.quantity_descr),
                    getResources().getString(R.string.repetition_descr)
            };

            //Set Time variables
            Calendar calendar = Calendar.getInstance();
            startDate = (Calendar) calendar.clone();
            calendar.roll(Calendar.DAY_OF_MONTH,true);
            endDate = calendar;
        } else {
            //Reload the subTitle names
            ArrayList list =  savedInstanceState.getStringArrayList(STATE_SUBTITLES);
            stepSubTitles = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                stepSubTitles[i] = (String) list.get(i);
            }

            //Reload Time Variables
            startDate = (Calendar)savedInstanceState.getSerializable(STATE_STARTDATE);
            endDate = (Calendar) savedInstanceState.getSerializable(STATE_ENDDATE);

            singleSelected = savedInstanceState.getBoolean(STATE_REP_Single);
        }

        setTimePicker(startDate.get(Calendar.HOUR_OF_DAY),startDate.get(Calendar.MINUTE));
        setStartDatePicker(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        setEndDatePicker(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));

        VerticalStepperFormLayout.Builder.newInstance(verticalStepper, stepTitles, this, this)
                .materialDesignInDisabledSteps(true)
                .showVerticalLineWhenStepsAreCollapsed(true)
                .primaryColor(ContextCompat.getColor(getApplicationContext(), R.color.primary))
                .primaryDarkColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark))
                .stepsSubtitles(stepSubTitles)
                .displayBottomNavigation(false)
                .init();

    }

    @Override
    public void onStart() {
        if(singleSelected) { //disable/enable when the verticalStepper exists.
            disableDays();
        } else {
            enableDays();
        }
        super.onStart();
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        boolean[] weekstate = state.getBooleanArray(STATE_WEEK);
        for (int i = 0; i < weekstate.length; i++) {
            LinearLayout day = getDayLayout(i);
            day.setTag(weekstate[i]);
        }

        if (singleSelected) {
            disableDays();
        } else {
            enableDays();
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putSerializable(STATE_ENDDATE,endDate);
        state.putSerializable(STATE_STARTDATE,startDate);
        ArrayList<String> subtitles = new ArrayList<>();
        for (int i = 0; i < TOTAL_STEPS; i++ ) {
            subtitles.add(verticalStepper.getStepsSubtitles(i));
        }
        state.putStringArrayList(STATE_SUBTITLES, subtitles);
        boolean[] week_state = new boolean[weekdaysSelection.length];

        for (int i = 0; i < week_state.length; i++) {
            LinearLayout day = getDayLayout(i);
            week_state[i] = (boolean) day.getTag();
        }
        state.putBooleanArray(STATE_WEEK,week_state);
        state.putBoolean(STATE_REP_Single, singleSelected);
        super.onSaveInstanceState(state);
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
            case REPETITION_STEP:
                view = createRepetitionStep();
                break;
            case QUANTITY_STEP:
                view = createQuantityStep();
                break;
        }
        return view;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case TIME_STEP:
                verticalStepper.setActiveStepAsCompleted();
                break;
            case DATE_STEP:
                verticalStepper.setActiveStepAsCompleted();
                verticalStepper.setStepSubtitle(stepNumber-1, getTimeString()); //Set Timesubtitle when we change.
                break;
            case QUANTITY_STEP:
                verticalStepper.setActiveStepAsCompleted();
                verticalStepper.setStepSubtitle(stepNumber-1,getDateString(startDate));
                break;
            case REPETITION_STEP:
                verticalStepper.setStepSubtitle(stepNumber-1, seekBar.getProgress()+"");
                if ((multiRadioButton.isChecked() && startDate.before(endDate))|| singleRadioButton.isChecked()) {
                    verticalStepper.setActiveStepAsCompleted();
                }
                break;
            default:
                //Check whether the date is appropriate.
                if (multiRadioButton.isChecked() && endDate.before(startDate)) {
                    verticalStepper.setStepAsUncompleted(REPETITION_STEP,"Please choose an ending date after the starting date.");
                } else {
                    verticalStepper.setStepSubtitle(REPETITION_STEP, getRepetitionDescr());
                }
                break;
        }
    }

    @Override
    public void sendData() {

        //TODO: Save data! Remo.
        //time.first HOUR
        //time.second MINUTE
        //singleTime    IF TRUE I TAKE THE PILL ONLY ONCE IN MY LIFE, ELSE WEEK INTERVALS AND END DATE
        //weekdaysSelected[] INDEX 0 MONDAY, INDEX 1 THUSDAY ...
        //startDate / endDate.get(Calendar.DAY_OF_MONTH) DATE OF START/END
        //seekBar.getProgress() HOW MANY PILLS TO TAKE AT ONCE

        //after data save show interface whether additional intake should be shown.
        AddPillFinishDialog finishDialog = AddPillFinishDialog.newInstance();
        finishDialog.show(getSupportFragmentManager(),AddPillFinishDialog.tag);
    }


    public void setAlarm(){
        Log.e("alarm","setting");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        int hourOfDay = startDate.get(Calendar.HOUR_OF_DAY); //HOUR
        int minuteOfDay = startDate.get(Calendar.MINUTE); //MINUTE
        int Day = this.startDate.get(Calendar.DAY_OF_MONTH); //DAY OF START
        int Month=this.startDate.get(Calendar.MONTH); //MONTH OF START
        int Year=this.startDate.get(Calendar.YEAR); //YEAR OF START
        String medicineName = medicine.getName(); // MEDICINE NAME
        int quantity = seekBar.getProgress(); //HOW MANY PILLS TO TAKE AT ONCE

        Date startDate = this.startDate.getTime();
        Date endDate =this.endDate.getTime();

        if(singleSelected) {
            int alarmId = (int)System.currentTimeMillis(); //it creates an unique id
            AlarmUtil alarm = new AlarmUtil(this, medicineName, quantity, startDate, alarmId);
        } else {
            for(int weekday=0; weekday<weekdaysSelection.length; weekday++) {
                if(weekdaysSelection[weekday]) {
                    int alarmId = (int)System.currentTimeMillis(); //it creates an unique id
                    AlarmUtil alarm = new AlarmUtil(this, medicineName, quantity, startDate, endDate, alarmId, weekday+1); //because Calendar counts from 1
                }
            }
        }

    }
    private View createQuantityStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        quantityContent = (ConstraintLayout) inflater.inflate(R.layout.stepper_quantity, null, false);
        seekBar = quantityContent.findViewById(R.id.seekbar);
        seekBar.setProgress(1);

        return quantityContent;
    }

    private View createRepetitionStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        repetitionContent =
                (ConstraintLayout) inflater.inflate(R.layout.stepper_repetition,null, false);

        multiRadioButton = repetitionContent.findViewById(R.id.multipleRep_radio);
        singleRadioButton = repetitionContent.findViewById(R.id.singleRep_radio);
        endDateTextView = repetitionContent.findViewById(R.id.endDateLabel);
        endDateTextView.setText (getDateString(endDate));
        final String[] weekdays = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};

        for(int i = 0; i < weekdays.length; i++) {
            final LinearLayout dayLayout = getDayLayout(i);
            final int index = i;
            activateDay(i, dayLayout, false);

            dayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(boolean) v.getTag()) {
                        activateDay(index, dayLayout, true);
                    } else {
                        deactivateDay(index, dayLayout, true);
                    }
                }
            });

            TextView dayText = dayLayout.findViewById(R.id.day);
            dayText.setText(weekdays[i]);
        }

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePicker.show();
            }
        });

        //Load the default or saved settings of selection
        singleRadioButton.setChecked(singleSelected);
        multiRadioButton.setChecked(!singleSelected);
        return repetitionContent;
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

    private View createDateStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        ConstraintLayout dateStepContent =
                (ConstraintLayout) inflater.inflate(R.layout.stepper_time,null, false);
        startDateTextView = dateStepContent.findViewById(R.id.time_label);
        startDateTextView.setText(getDateString(startDate));
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePicker.show();
            }
        });

        return dateStepContent;
    }

    public void onRadioButtonPressed(View view) {
        switch (view.getId()) {
            case R.id.singleRep_radio:
                singleRadioButton.setChecked(true);
                multiRadioButton.setChecked(false);
                disableDays();
                verticalStepper.setActiveStepAsCompleted();
                singleSelected = true;
                break;
            case R.id.multipleRep_radio:
                singleRadioButton.setChecked(false);
                multiRadioButton.setChecked(true);
                if (endDate.before(startDate)) {
                    verticalStepper.setStepAsUncompleted(REPETITION_STEP,getResources().getString(R.string.end_after_startDateError));
                }
                singleSelected = false;
                enableDays();
                break;
        }
    }

    /**
     * This view disables tapping on the day objects and on the endDate label. It also
     * colors them following the material design specification.
      */
    private void disableDays() {
        double opacity = 0.26; //Following the material specification, disabled buttons have opacity 26%
        for (int i = 0; i < weekdaysSelection.length; i++) {
            LinearLayout day = getDayLayout(i);
            day.setOnClickListener(null);
            endDateTextView.setOnClickListener(null);
            endDateTextView.setAlpha( (float) (opacity * 255 /100));
            int colorBlack = ContextCompat.getColor(getBaseContext(), R.color.black);
            if (day.getBackground()== null) {
                Drawable bg = ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_step);
                bg.setColorFilter(new PorterDuffColorFilter(colorBlack, PorterDuff.Mode.SRC_IN));
                bg.setAlpha((int) (opacity * 255));
                day.setBackground(bg);
            } else {
                day.getBackground().setColorFilter(new PorterDuffColorFilter(colorBlack, PorterDuff.Mode.SRC_IN));
                day.getBackground().setAlpha((int) (opacity * 255));
            }
        }
    }

    /**
     * This method colors the Day-circles and enables tapping. It also enables the End Date-label.
     */
    private void enableDays() {
        for (int i = 0; i < weekdaysSelection.length; i++) {
            final int index = i ;
            final LinearLayout dayLayout = getDayLayout(i);
            dayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(boolean) v.getTag()) {
                        activateDay(index, dayLayout, true);
                    } else {
                        deactivateDay(index, dayLayout, true);
                    }
                }
            });
            if ((boolean) dayLayout.getTag()) {
                dayLayout.getBackground().setAlpha(255);
                activateDay(index, dayLayout, false);
            } else {
                deactivateDay(index, dayLayout, false);
            }

        }
        endDateTextView.setAlpha(1.0f);
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePicker.show();
            }
        });

    }

    /**
     * Selects the Day on position 'index'.
     * @param index
     * @param dayLayout
     * @param check: True if checkDays() should be called.
     */
    private void activateDay(int index, LinearLayout dayLayout, boolean check) {
        weekdaysSelection[index] = true;

        dayLayout.setTag(true);
        Drawable bg = ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_step);
        int colorPrimary = ContextCompat.getColor(getBaseContext(), R.color.accent);
        bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
        dayLayout.setBackground(bg);
        TextView day = (TextView) dayLayout.findViewById(R.id.day);
        day.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.md_white_1000));
        if(check) {
            checkDays();
        }
    }

    /**
     * Deselects the day on position 'index'.
     * @param index
     * @param dayLayout
     * @param check: True if method checkDays() should be called.
     */
    private void deactivateDay(int index, LinearLayout dayLayout, boolean check) {
        weekdaysSelection[index] = false;

        dayLayout.setTag(false);

        dayLayout.setBackgroundResource(0);

        TextView textView = dayLayout.findViewById(R.id.day);
        textView.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.primary));

        if (check) {
            checkDays();
        }
    }

    /**
     * Checks, whether at least one day is selected and sets the RepetitionStep completed or uncompleted
     * accordingly.
     * @return
     */
    private boolean checkDays() {
        boolean thereIsAtLeastOneDaySelected = false;
        for(int i = 0; i < weekdaysSelection.length && !thereIsAtLeastOneDaySelected; i++) {
            if(weekdaysSelection[i]) {
                verticalStepper.setStepAsCompleted(REPETITION_STEP);
                thereIsAtLeastOneDaySelected = true;
            }
        }
        if(!thereIsAtLeastOneDaySelected) {
            verticalStepper.setStepAsUncompleted(REPETITION_STEP, getResources().getString(R.string.no_days_Error));
        }

        return thereIsAtLeastOneDaySelected;
    }

    private void setEndDatePicker(int year, int month, int day) {
        endDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate.set(year,month, dayOfMonth);
                endDateTextView.setText(getDateString(endDate));
                if (startDate.after(endDate)) {
                    verticalStepper.setStepAsUncompleted(REPETITION_STEP,getResources().getString(R.string.end_after_startDateError));
                } else if(checkDays()) {
                    verticalStepper.setActiveStepAsCompleted();
                }
            }
        }, year, month, day);
    }

    private void setStartDatePicker(int year, int month, int day) {
        startDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate.set(year, month, dayOfMonth);
                startDateTextView.setText(getDateString(startDate));
            }
        }, year, month, day);
    }

    private void setTimePicker(int hour, int minute) {
        timePicker = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                       startDate.set(Calendar.MINUTE, minute);
                       timeTextView.setText(getTimeString());
                    }
                }, hour, minute, true);
    }

    public String getRepetitionDescr() {
        if (singleRadioButton.isChecked()) {
            return getResources().getString(R.string.rep_onlyOnce);
        } else {
            return getResources().getString(R.string.rep_severalTimesDescr);
        }
    }

    public int getMedicineId () {
        return getIntent().getIntExtra(EXTRA_MEDICINEID, -1);
    }

    /**
     * Returns a string representation of time.
     * @return
     */
    @NonNull
    private String getTimeString() {
        int minute = startDate.get(Calendar.MINUTE);
        int hour = startDate.get(Calendar.HOUR_OF_DAY);
        String hourString = ((hour<9) ? "0"+ hour : String.valueOf(hour));
        String minuteString = (minute<9) ? "0" + minute : String.valueOf(minute);
        String time = hourString + ":" + minuteString;
        return hourString + ":" + minuteString;
    }

    private String getDateString(Calendar date) {
        String dateString = "";
        if (date.get(Calendar.DAY_OF_MONTH) <10) {
            dateString += "0"+ date.get(Calendar.DAY_OF_MONTH);
        } else {
            dateString += date.get(Calendar.DAY_OF_MONTH);
        }
        if (date.get(Calendar.MONTH) < 10 ) {
            dateString += ".0" + (date.get(Calendar.MONTH) +1);
        } else{
            dateString += "."+ (date.get(Calendar.MONTH) + 1);
        }
        dateString += "." + date.get(Calendar.YEAR);
        return dateString;
    }

    /**
     * Retrieves the Layout for the day on position i.
     * @param i
     * @return
     */
    private LinearLayout getDayLayout(int i) {
        int id = repetitionContent.getResources().getIdentifier("day_"+i, "id", getPackageName());
        return (LinearLayout) repetitionContent.findViewById(id);
    }

    public long getReceiptID() {
        return receipt.getId();
    }
}
