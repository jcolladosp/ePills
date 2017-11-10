package devs.erasmus.epills.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.erasmus.epills.R;

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
    @BindView(R.id.loading_indicator)
    ProgressBar loading_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill__general_);
        ButterKnife.bind(this);
        mCurrentPhotoPath = getIntent().getStringExtra(EXTRA_PHOTO_URI);

        ImageView imageView = findViewById(R.id.image_view);


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
                makeSearchQuery();
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
                name_text.setError("INCORRECT SUBSTANCE NAME");
            }
        }
    }
}
