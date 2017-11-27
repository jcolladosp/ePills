package devs.erasmus.epills.utils;

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
}
