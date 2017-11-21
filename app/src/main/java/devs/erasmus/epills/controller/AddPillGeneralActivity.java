package devs.erasmus.epills.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import com.bumptech.glide.request.RequestOptions;
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

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class AddPillGeneralActivity extends AppCompatActivity {
    private static final String STATE_PHOTO = "STATE_PHOTOURL";

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
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private Drawer drawer;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill_general);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(STATE_PHOTO, null) ;

        }

        if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            appBarLayout.setExpanded(false,true);
        } else {
            appBarLayout.setExpanded(true, true);
        }

        Glide.with(this)
                .load(mCurrentPhotoPath)
                .error(
                        Glide.with(this).load(R.mipmap.ic_picture_foreground)
                )
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(this)
                    .load(mCurrentPhotoPath)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle instance) {
        super.onSaveInstanceState(instance);
        if (mCurrentPhotoPath != null) {
            instance.putString(STATE_PHOTO,mCurrentPhotoPath);
        }
    }

    @OnClick(R.id.FAB)
    void onConfirm(){
        confirm();
    }
    @OnClick(R.id.autofill_button)
    void onAutoFill() {
        makeSearchQuery();
    }

    @OnClick(R.id.image_view)
    public void dispatchPictureIntent() {
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

    private void setAppBarOffset(int offset) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.onNestedScroll(coordinatorLayout, appBarLayout, null, 0 ,offset,0,0, 0);
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



    //--------------------------------AutoFill logic------------------------------@author{REMO}
    
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
                name_text.setError("INCORRECT SUBSTANCE NAME");
            }
        }
    }

}
