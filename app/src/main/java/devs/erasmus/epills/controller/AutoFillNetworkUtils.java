package devs.erasmus.epills.controller;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Remo Andreoli on 09/11/2017.
 */

public class AutoFillNetworkUtils {
    // EXAMPLE OF RESULT: https://api.fda.gov/drug/label.json?search=openfda.substance_name:%22aspirin%22&limit=1
    final static String OPENFDA_BASE_URL = "https://api.fda.gov/drug/label.json";

    final static String PARAM_QUERY = "search";
    final static String PARAM_QUERY_EXTRA = "openfda.substance_name:";

    final static String PARAM_LIMIT = "limit";
    final static String limitBy = "10";

    //Builds the URL for the substance name
    public static URL buildUrl(String substanceName) {
        String toBuild=PARAM_QUERY_EXTRA + '"' + substanceName + '"';

        Uri builtUri = Uri.parse(OPENFDA_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, toBuild)
                .appendQueryParameter(PARAM_LIMIT, limitBy)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            // no internet connection
            return false;
        } else
            return true;
    }

    //Returns the entire result from the HTTP response.
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
