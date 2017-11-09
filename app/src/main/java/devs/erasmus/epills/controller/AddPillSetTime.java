package devs.erasmus.epills.controller;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.widget.TimePickerFragment;

public class AddPillSetTime extends AppCompatActivity {
    final String PACKAGENAME = "devs.erasmus.epills.contoller";
    final String EXTRA_MEDICINEID = PACKAGENAME + "medicine_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill_set_time);
        ButterKnife.bind(this);
    }

    public void showTimePickerDialog(View v) {
       DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
}
