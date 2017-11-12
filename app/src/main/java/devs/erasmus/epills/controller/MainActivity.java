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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;

import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devs.erasmus.epills.R;
import devs.erasmus.epills.widget.NavigationDrawer;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;

    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button button = findViewById(R.id.Add_pill_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchPictureIntent();
            }
        });

        ButterKnife.bind(this);
        LitePal.initialize(getApplicationContext());

        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();
        drawer.setSelection(-1);

    }

    private void dispatchPictureIntent() {
        Intent takepicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takepicture.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPictureFile();
            } catch (IOException ioE) {
                ioE.printStackTrace();
                System.exit(-1);
            }

            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "devs.erasmus.epills.FileProvider", photoFile);
                takepicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takepicture, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent addPillIntent = new Intent(this, AddPill_General_Activity.class);
            System.out.println("Created intent"); //TODO: delete. it's only for testing.
            addPillIntent.putExtra(AddPill_General_Activity.EXTRA_PHOTO_URI, mCurrentPhotoPath);
            startActivity(addPillIntent);
        }
    }

    private File createPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp + R.string.app_name;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        //TODO: Save the path also in the DB
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onBackPressed() {
        drawer.openDrawer();
    }

}
