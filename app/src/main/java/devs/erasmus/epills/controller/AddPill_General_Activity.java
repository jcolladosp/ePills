package devs.erasmus.epills.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import devs.erasmus.epills.R;

public class AddPill_General_Activity extends AppCompatActivity {

    static final String EXTRA_PHOTO_URI = "devs.erasmus.epills.extra_photo_uri";

    //TODO: STORE the picture path somewhere in the DB
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill__general_);
        mCurrentPhotoPath = getIntent().getStringExtra(EXTRA_PHOTO_URI);

        ImageView imageView = findViewById(R.id.image_view);
        Glide.with(this)
                .load(mCurrentPhotoPath)
                .into(imageView);
    }

}
