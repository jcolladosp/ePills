package devs.erasmus.epills;

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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPill_General_Activity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //TODO: STORE the picture path somewhere in the DB
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill__general_);

        dispatchPictureIntent();
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
            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(imageBitmap);
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

}
