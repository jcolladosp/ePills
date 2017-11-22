package devs.erasmus.epills.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import devs.erasmus.epills.R;
import devs.erasmus.epills.controller.AddPillSetTime;
import devs.erasmus.epills.controller.MainActivity;


/**
 * A simple {@link DialogFragment} subclass.
 * to handle interaction events.
 * Use the {@link AddPillFinishDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPillFinishDialog extends DialogFragment {
    public static final String tag = "AddPillFinishDialog";

    public AddPillFinishDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddPillFinishDialog.
     */
    public static AddPillFinishDialog newInstance() {
        AddPillFinishDialog fragment = new AddPillFinishDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_pill_finish_dialog, container, false);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title)
                .setItems(R.array.addPillFinish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddPillSetTime setAlarmActivity = (AddPillSetTime) getActivity();
                        switch (which) {
                            case 0:
                                AddPillSetTime activity = (AddPillSetTime)getActivity();
                                Intent intent = new Intent(getActivity(), AddPillSetTime.class);
                                intent.putExtra(AddPillSetTime.EXTRA_MEDICINEID, activity.getMedicineId())
                                        .putExtra(AddPillSetTime.EXTRA_RECEIPTID, activity.getReceiptID());

                                //setAlarmActivity.setAlarm();
                                startActivity(intent);
                                activity.finish();
                                break;
                            case 1:
                                MainActivity.pillAddedSuccess();
                                setAlarmActivity.setAlarm();
                                getActivity().finish();
                                break;
                            default:
                                throw new RuntimeException("Programming error! You should never reach default");
                        }
                    }
                });
        return builder.create();
    }
}
