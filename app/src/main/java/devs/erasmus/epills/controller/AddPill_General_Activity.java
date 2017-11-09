package devs.erasmus.epills.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import devs.erasmus.epills.widget.SquareImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.Drawer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.widget.NavigationDrawer;

public class AddPill_General_Activity extends AppCompatActivity {

    static final String EXTRA_PHOTO_URI = "devs.erasmus.epills.extra_photo_uri";

    //TODO: STORE the picture path somewhere in the DB
    private String mCurrentPhotoPath;

    //Views
    @BindView(R.id.autofill_button)
    Button autofill_button;
    @BindView(R.id.FAB)
    FloatingActionButton confirm_Button;
    @BindView(R.id.name_text)
    EditText name_text;
    @BindView(R.id.description_text)
    MultiAutoCompleteTextView description_text;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill__general_);
        ButterKnife.bind(this);
        mCurrentPhotoPath = getIntent().getStringExtra(EXTRA_PHOTO_URI);

        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();

        SquareImageView imageView = findViewById(R.id.image_view);
        Glide.with(this)
                .load(mCurrentPhotoPath)
                .into(imageView);

        //Set onclicklistener
        confirm_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        autofill_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                autoFill_Description();
            }
        });
    }

    private void autoFill_Description() {

    }

    private void confirm() {
        //Check if the all Fields are filled out
        if(TextUtils.isEmpty(name_text.getText().toString())) {
            name_text.setError(getString(R.string.empty_Name));
        } else {
            //TODO: Save new data and make transition to next view.
        }
    }

}
