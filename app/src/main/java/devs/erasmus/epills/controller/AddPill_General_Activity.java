package devs.erasmus.epills.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import butterknife.OnClick;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.model.Receipt;
import devs.erasmus.epills.utils.AutoFillNetworkUtils;
import devs.erasmus.epills.widget.SquareImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;
import devs.erasmus.epills.widget.NavigationDrawer;

public class AddPill_General_Activity extends AppCompatActivity {
//TODO: Make responsive to orientation changes
    static final String EXTRA_PHOTO_URI = "devs.erasmus.epills.extra_photo_uri";

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
    @BindView(R.id.loading_indicator)
    ProgressBar loading_indicator;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_view)
    SquareImageView imageView;

    private Drawer drawer;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill__general_);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();

        dispatchPictureIntent();
    }

    @OnClick(R.id.FAB)
    void onConfirm(){
        confirm();
    }
    @OnClick(R.id.autofill_button)
    void onAutoFill(){
        makeSearchQuery();
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
            Glide.with(this)
                    .load(mCurrentPhotoPath)
                    .into(imageView);
        } else {
            finish();
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

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void confirm() {
        //Check if the all Fields are filled out
        if(TextUtils.isEmpty(name_text.getText().toString())) {
            name_text.setError(getString(R.string.empty_Name));
        } else {
            Medicine medicine = new Medicine(name_text.getText().toString(), mCurrentPhotoPath);
            medicine.save();
            int id = DataSupport.count(Medicine.class); //Possible because of auto-increment.

            //Create new Receipt
            Receipt receipt = new Receipt();
            receipt.save();

            //Start time activity
            Intent intent = new Intent(this, AddPillSetTime.class);
            intent.putExtra(AddPillSetTime.EXTRA_MEDICINEID, id)
                    .putExtra(AddPillSetTime.EXTRA_RECEIPTID, receipt.getId());
            startActivity(intent);
            finish();
        }
    }



    //AutoFill logic
    
    
    //Retrieves the substance name from name_text, constructs the URL with AutoFillNetworkUtils.buildUrl
    //and finally fires an AsyncTask to perform the GET request using queryTask
    private void makeSearchQuery() {
        String searchQuery = name_text.getText().toString();
        URL searchUrl = AutoFillNetworkUtils.buildUrl(searchQuery);
        //DEBUG(show the url): description_text.setText(searchUrl.toString());
        new queryTask().execute(searchUrl);

    }

    
    public class queryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading_indicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResult = null;
            try {
                searchResult = AutoFillNetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResult;
        }

        @Override
        protected void onPostExecute(String queryResults) {
            //default string to display if there is no info on openFDA
            String drugDescription="no description provided";

            loading_indicator.setVisibility(View.INVISIBLE);
            if (queryResults != null && !queryResults.equals("")) {

                try{
                    JSONObject jsonQueryResult = new JSONObject(queryResults);
                    JSONArray jsonResultArray = jsonQueryResult.getJSONArray("results");
                    if (jsonResultArray != null && jsonResultArray.length() > 0) {
                        for (int i=0; i<jsonResultArray.length(); i++) {
                            JSONObject result = jsonResultArray.getJSONObject(i);
                            JSONObject openfda = result.getJSONObject("openfda");

                            JSONArray jsonDescription = openfda.getJSONArray("pharm_class_epc");
                            if (jsonDescription != null && jsonDescription.length() > 0) {
                                String description = " ";
                                for (int j=0; j<jsonDescription.length(); j++) {
                                    // This is the purpose;
                                    description = description + "\n" + jsonDescription.getString(j);
                                }
                                drugDescription=new String(description);
                            }
                        }
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

                description_text.setText(drugDescription);

            } else {
                name_text.setError("INCORRECT SUBSTANCE NAME OR NO INTERNET CONNECTION");
            }
        }
    }



}
