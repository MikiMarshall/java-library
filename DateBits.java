/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * DateBits - A few additions/wrappers for Date to work with other
 *            utility classes betterer.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created: 2018.02.17
 * Version: 2019.12.30
 *
 * Notes:
 */

package mikilib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is here because of the challenges of saving dates in SQLite,
 * which has no real Date type. Since date is a common sort field, it
 * needs to be stored in a Sorting format (YY.MM.DD - HH.MM.SS), while
 * the Locale format is more useful onscreen. This class attempts to
 * make that mess simpler.
 * Static/singleton - this class is merely a container for helpful methods.
 */
public class DateBits {
    private static DateBits ourInstance = new DateBits();

    // Preferences useful date formats as constants
    public static final SimpleDateFormat SQL_DATE =
            new SimpleDateFormat("yyyyMMdd-HHmmss");

    // >>> Later: get the Locale date formats to work without crashing
    public static final SimpleDateFormat SHORT_DATE =
            new SimpleDateFormat("yyyy.MM.dd");
//            new SimpleDateFormat(DateFormat.getDateInstance(
//                    DateFormat.SHORT, Locale.getDefault()).toString());

    public static final SimpleDateFormat SHORT_TIME =
            new SimpleDateFormat("HH:mm:ss");
//            new SimpleDateFormat(DateFormat.getTimeInstance(
//                    DateFormat.SHORT, Locale.getDefault()).toString());

    public static final SimpleDateFormat SHORT_DATETIME =
            new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
//            new SimpleDateFormat(DateFormat.getDateTimeInstance(
//                    DateFormat.SHORT, DateFormat.SHORT,
//                    Locale.getDefault()).toString());

    private static final String MISSING_DATE = "------";

    /**
     * Return instance of static instance. Not sure where this is useful.
     * @return      the only instance of this class we have.
     */
    public static DateBits getInstance() {
        return ourInstance;
    }

    /**
     * Private constructor, making the class a singleton.
     */
    private DateBits() {
    }

    /**
     * Return the current date-time (timestamp).
     * @return      Current date as Date.
     */
    public static Date now() {
        // A new date object defaults to today
        return (new Date());
    }

    /**
     * Convert a date into a string, in the selected format.
     * @param date      date to convert.
     * @param sdf       selected format.
     * @return          formatted date as String.
     */
    public static String format(Date date, SimpleDateFormat sdf) {
        return ((date != null) ? (sdf.format(date)) : MISSING_DATE);
    }

    /**
     * Parse a date from a string with the selected format.
     * @param date      String date to convert.
     * @param sdf       Date format to expect.
     * @return          Converted date result.
     */
    public static Date parse(String date, SimpleDateFormat sdf) {
        try {
            return (sdf.parse(date));
        } catch (ParseException e) {
            Debug.out("DateBits. parse()", e.getMessage());
        }
        return null;
    }
}
