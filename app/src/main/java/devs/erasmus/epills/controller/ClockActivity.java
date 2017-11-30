package devs.erasmus.epills.controller;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikepenz.materialdrawer.Drawer;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;
import com.viewpagerindicator.as.library.indicator.RecyclerCirclePageIndicator;
import com.viewpagerindicator.as.library.pageview.RecyclerViewPager;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devs.erasmus.epills.R;
import devs.erasmus.epills.broadcast_receiver.BootReceiver;
import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.utils.ClockUtils;
import devs.erasmus.epills.widget.NavigationDrawer;
import devs.erasmus.epills.widget.PillCardAdapter;

public class ClockActivity extends AppCompatActivity {
    private List<IntakeMoment> intakeMomentList;
    private PillCardAdapter pillCardAdapter;

    @BindView(R.id.list)
    RecyclerViewPager mRecyclerView;
    @BindView(R.id.analog_clock)
    CustomAnalogClock analogClock;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView (R.id.indicator)
    RecyclerCirclePageIndicator indicator;
    @BindView(R.id.noPills)
    TextView noPillsTV;
    @BindView(R.id.cards_layout)
    ConstraintLayout cards_layout;

    private static Drawer drawer;
    private View parentLayout;
    private com.google.api.services.calendar.Calendar mService = null;

    ProgressDialog mProgress;
    GoogleAccountCredential mCredential;
    List<IntakeMoment> allIntake;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ButterKnife.bind(this);
        LitePal.initialize(getApplicationContext());
        enableBootReceiver(true);
        setSupportActionBar(toolbar);
        parentLayout = findViewById(R.id.clock_layout);

        drawer = NavigationDrawer.getDrawerBuilder(this,this,toolbar).build();
        drawer.setSelection(1,false);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getString(R.string.sync_google_calendar_loader));


        prepareAnalogClock();
        preparePillsAdapter();
        setPills();

        if(getIntent().getBooleanExtra("pill",false)){
            pillAddedSuccess();
        }
        if(getIntent().getBooleanExtra("modified",false)){
            pillEditSuccess();
        }

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());



    }
    private void prepareAnalogClock(){
        analogClock.init(this, R.drawable.clock_face, R.drawable.hour_hand, R.drawable.minute_hand,R.drawable.pill_hand,R.drawable.pill_highlight ,0, true, false);
        analogClock.setAutoUpdate(true);
        analogClock.setScale(1.1f);
    }
    private void preparePillsAdapter(){


        intakeMomentList = new ArrayList<>();
        pillCardAdapter = new PillCardAdapter(this,intakeMomentList);
        LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(pillCardAdapter);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        indicator.setViewPager(mRecyclerView);
        indicator.setRadius(12);
        indicator.setFillColor(Color.parseColor("#FF33B5E5"));

        mRecyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
              //  analogClock.highlightPill(newPosition);
            }

        });



    }
    private void setPills(){
        if(DataSupport.count(IntakeMoment.class)!=0) {
            noPillsTV.setVisibility(View.GONE);
            intakeMomentList.clear();
            pillCardAdapter.notifyDataSetChanged();

            Date today = new Date();
            Date tomorrow = ClockUtils.addDays(today,1);


            List<IntakeMoment> allIntakeToday = DataSupport.where("startdate between "
            +today.getTime() +" and "+ tomorrow.getTime()).find(IntakeMoment.class);

            if(allIntakeToday.size()==0){
                noPillsTV.setText(R.string.no_pills_today);
                noPillsTV.setVisibility(View.VISIBLE);
                cards_layout.setVisibility(View.GONE);

            }
            for (IntakeMoment intake : allIntakeToday) {
                intake.setMedicine(DataSupport.find(Medicine.class, intake.getMedicineId()));
                intakeMomentList.add(intake);
                analogClock.drawPill(intake.getStartDate().getHours());
            }

            pillCardAdapter.notifyDataSetChanged();
        }
        else{
            noPillsTV.setText(R.string.no_pills_found);
            noPillsTV.setVisibility(View.VISIBLE);
            cards_layout.setVisibility(View.GONE);

        }
    }
    @OnClick(R.id.add_fab)
    void add_fab(){
        startActivity(new Intent(this,AddPillGeneralActivity.class));
    }

    public void pillAddedSuccess(){
        drawer.closeDrawer();

        Snackbar mySnackbar = Snackbar.make(parentLayout,
                R.string.pill_added_success, Snackbar.LENGTH_SHORT);
        mySnackbar.show();


    }
    public void pillEditSuccess(){
        drawer.closeDrawer();

        Snackbar mySnackbar = Snackbar.make(parentLayout,
                R.string.pill_edited_success, Snackbar.LENGTH_SHORT);
        mySnackbar.show();


    }

    @Override
    public void onBackPressed() {
        drawer.openDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sync_calendar) {
            allIntake = DataSupport.findAll(IntakeMoment.class);
            getResultsFromApi(allIntake);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void enableBootReceiver(boolean b) {
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();

        if(b){
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
        else{
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    // GOOGLE CALENDAR LOGIC - some code extracted from Google Calendar API guidelines

    private void getResultsFromApi(List<IntakeMoment> intakes) {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this,"No network connection available.",Toast.LENGTH_SHORT);
        } else {
            new MakeRequestTask(mCredential,intakes).execute();
        }
    }


    private void chooseAccount() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.GET_ACCOUNTS)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        String accountName = getPreferences(Context.MODE_PRIVATE)
                                .getString(PREF_ACCOUNT_NAME, null);
                        if (accountName != null) {
                            mCredential.setSelectedAccountName(accountName);
                            getResultsFromApi(allIntake);
                        } else {
                            // Start a dialog from which the user can choose an account
                            startActivityForResult(
                                    mCredential.newChooseAccountIntent(),
                                    REQUEST_ACCOUNT_PICKER);
                        }
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

    }
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {

                    Toast toast = Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    getResultsFromApi(allIntake);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi(allIntake);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi(allIntake);
                }
                break;
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ClockActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Exception mLastError = null;
        private List<IntakeMoment> allIntakes;
        MakeRequestTask(GoogleAccountCredential credential,List<IntakeMoment> intakes) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            allIntakes = intakes;
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                 createEvents();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }


        private void createEvents() throws IOException {
            for (IntakeMoment intakeMoment : allIntakes) {
                intakeMoment.setMedicine(DataSupport.find(Medicine.class, intakeMoment.getMedicineId()));

                Event event = new Event()
                        .setSummary(intakeMoment.getMedicine().getName())
                        .setDescription("A chance to hear more about Google's developer products.");

                DateTime startDateTime = new DateTime(intakeMoment.getStartDate());
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("Europe/Dublin");
                event.setStart(start);

                DateTime endDateTime = new DateTime(ClockUtils.addHours(intakeMoment.getStartDate(),1));
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("Europe/Dublin");
                event.setEnd(end);


                String calendarId = "ePills";
                event = mService.events().insert(calendarId, event).execute();
                Log.i("Events","Event created: "+ event.getHtmlLink());
            }

        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                Toast.makeText(ClockActivity.this,"No results returned.",Toast.LENGTH_SHORT);
            } else {
                output.add(0, "Data retrieved using the Google Calendar API:");
                    Log.i("results",TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ClockActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(ClockActivity.this,"The following error occurred:\n"
                            + mLastError.getMessage(),Toast.LENGTH_SHORT);
                }
            } else {
                Toast.makeText(ClockActivity.this,"Request cancelled.",Toast.LENGTH_SHORT);
            }
        }
    }
}




