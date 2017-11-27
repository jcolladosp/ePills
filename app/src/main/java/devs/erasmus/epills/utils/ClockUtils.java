package devs.erasmus.epills.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by colla on 27/11/2017.
 */

public class ClockUtils {
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PrefKeys.NAME.toString(), Context.MODE_PRIVATE);
    }
    public static boolean isFirstTime(Context context) {
        return  getPrefs(context).getBoolean(PrefKeys.FIRST_TIME.toString(), true);

    }
    public static void setFirstTimeDone(Context context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(PrefKeys.FIRST_TIME.toString(),false);
        editor.commit();
    }
}
