package com.arondight.timezone;

import com.arondight.timezone.data.JavaZone;
import com.arondight.timezone.data.ZoneDataSource;

import java.io.IOException;
import java.util.*;

import static com.arondight.timezone.TzConstants.*;

/**
 * The TimeZoneUtil is a set of utilities to make dealing with TimeZones easier,
 * for instance allowing you to take user text input and convert it to a TimeZone object
 * that can be used in your code.
 *
 * <b>TimeZone ID Input</b>
 *     All getById methods that take an "id" as input will accept a Java / IANA TimeZone ID or an old Apple iOS TimeZone ID.
 *     For example:
 *     <blockquote>
 *     <table>
 *         <caption>Examples</caption>
 *         <tr><th>Input ID</th></tr>
 *         <tr><td>US/Pacific</td></tr>
 *         <tr><td>America/Los_Angeles</td></tr>
 *         <tr><td>America/New_York</td></tr>
 *         <tr><td>IST</td></tr>
 *         <tr><td>GMT</td></tr>
 *     </table>
 *     </blockquote>
 *
 * <b>GMT String Input</b>
 *     A GMT String is a string that represents an offset from GMT. The utility allows a great flexibility
 *     in what is acceptable as it will parse the string and attempt to normalize it.
 *     As examples, the function {@link TimeZoneUtil#toStandardizedGmtString(String)}
 *     will process the following input to the following standard output format
 *     <blockquote>
 *     <table>
 *         <caption>Examples</caption>
 *         <tr><th>Input</th><th>Standardized Output</th></tr>
 *         <tr><td>GMT-00</td><td>GMT</td></tr>
 *         <tr><td>GMT+1</td><td>GMT+01:00</td></tr>
 *         <tr><td>GMT+01</td><td>GMT+01:00</td></tr>
 *         <tr><td>GMT-11</td><td>GMT-11:00</td></tr>
 *         <tr><td>GMT+800</td><td>GMT+08:00</td></tr>
 *         <tr><td>GMT-830</td><td>GMT-08:30</td></tr>
 *         <tr><td>GMT+1100</td><td>GMT+11:00</td></tr>
 *         <tr><td>GMT-1130</td><td>GMT-11:30</td></tr>
 *         <tr><td>GMT+1:00</td><td>GMT+01:00</td></tr>
 *         <tr><td>GMT-00:00</td><td>GMT</td></tr>
 *         <tr><td>GMT+01:00</td><td>GMT+01:00</td></tr>
 *         <tr><td>GMT-11:00</td><td>GMT-11:00</td></tr>
 *         <tr><td>GMT-11:30</td><td>GMT-11:30</td></tr>
 *         <tr><td>GMT+2:0</td><td>GMT+02:00</td></tr>
 *         <tr><td>GMT-06:0</td><td>GMT-06:00</td></tr>
 *         <tr><td>{empty string}</td><td>GMT</td></tr>
 *         <tr><td>-00:00</td><td>GMT</td></tr>
 *         <tr><td>-00</td><td>GMT</td></tr>
 *         <tr><td>+1</td><td>GMT+01:00</td></tr>
 *         <tr><td>+01</td><td>GMT+01:00</td></tr>
 *         <tr><td>-11</td><td>GMT-11:00</td></tr>
 *         <tr><td>+800</td><td>GMT+08:00</td></tr>
 *         <tr><td>-830</td><td>GMT-08:30</td></tr>
 *         <tr><td>+1100</td><td>GMT+11:00</td></tr>
 *         <tr><td>-1130</td><td>GMT-11:30</td></tr>
 *         <tr><td>+1:00</td><td>GMT+01:00</td></tr>
 *         <tr><td>+01:00</td><td>GMT+01:00</td></tr>
 *         <tr><td>-11:00</td><td>GMT-11:00</td></tr>
 *         <tr><td>-11:30</td><td>GMT-11:30</td></tr>
 *         <tr><td>+2:0</td><td>GMT+02:00</td></tr>
 *         <tr><td>-06:0</td><td>GMT-06:00</td></tr>
 *     </table>
 *     </blockquote>
 *
 * <b>Location String</b>
 *     See the <a href="LocationUtil.html#lsinput">Location String Input</a> section of {@link LocationUtil} for more information and details.
 *
 * <b>US Zipcode</b>
 *     See the <a href="LocationUtil.html#zipcode">US Zipcode</a> section of {@link LocationUtil} for more information and details.
 */
public class TimeZoneUtil extends BaseUtil {

    /**
     * Gets a TimeZone object by id, accepting the fallback option
     * @see TimeZoneUtil#getById(String, boolean)
     * @param id the id to parse, a Java TimeZone id, a known non-java TimeZone id or a parseable GMT string
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getById(String id) {
        return getById(id, true);
    }

    /**
     * Gets a TimeZone object by id, optionally accepting the fallback option
     * @param id the id to parse, a Java TimeZone id, a non Java TimeZone id or a GMT String
     * @param acceptFallback whether to accept a fallback (best guess) if the id is not a standard Java TimeZone or a known non-java TimeZone or a parseable GMT string
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getById(String id, boolean acceptFallback) {
        String upper = validateInputUpper(id);
        if (upper == null) {
            return null;
        }

        if ( ALL_JAVA_IDS.containsKey(upper) ) {
            return TimeZone.getTimeZone(id);
        }
        String nonJavaCodeId = NON_JAVA_CODES_TO_ID.get(upper);
        if (nonJavaCodeId != null) {
            return TimeZone.getTimeZone(nonJavaCodeId);
        }
        return getByGmtString(id, acceptFallback);
    }

    /**
     * Gets a TimeZone object by minutes, accepting the fallback option
     * @param offsetMinutes number of minutes plus (+) or minus (-) offset from GMT.
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByOffset(int offsetMinutes) {
        return getByOffset(offsetMinutes, true);
    }

    /**
     * Gets a TimeZone object by minutes, optionally accepting the fallback option
     * @param offsetMinutes number of minutes plus (+) or minus (-) offset from GMT.
     * @param acceptFallback whether to accept a fallback (best guess) if the id is not a standard Java TimeZone or a known non-java TimeZone or a parseable GMT string with a direct correlation
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByOffset(int offsetMinutes, boolean acceptFallback) {
        if (acceptFallback) {
            JavaZone jz = FALLBACK_BY_OFFSET.get(offsetMinutes);
            if (jz != null) {
                return jz.getTimeZone();
            }
        }

        String standard = toStandardizedGmtString(offsetMinutes);
        return standard == null ? null : TimeZone.getTimeZone(standard);
    }

    /**
     * Gets a TimeZone object by minutes, take into account whether the TimeZone uses Daylight Savings Time
     * @param offsetMinutes number of minutes plus (+) or minus (-) offset from GMT.
     * @param useDaylight whether or not the expected TimeZone will use Daylight Savings Time
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByOffsetAndDst(int offsetMinutes, boolean useDaylight) {
        JavaZone jz = PRIMARY_BY_OFFSET_KEY.get(getOffsetKey(offsetMinutes, useDaylight));
        if (jz != null) {
            return jz.getTimeZone();
        }
        // this is really an error case, if didn't find it before, it's unlikely to be found here
        return getByOffset(offsetMinutes, true);
    }

    /**
     * Gets a TimeZone object by a GMT string, accepting the fallback option
     * @param gmtString a parseable GMT string
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByGmtString(String gmtString) {
        return _getByStandardizedGmtString(toStandardizedGmtString(gmtString), true);
    }

    /**
     * Gets a TimeZone object by a GMT string, optionally accepting the fallback option
     * @param gmtString a parseable GMT string
     * @param acceptFallback whether to accept a fallback (best guess) if the input cannot determine a direct correlation
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByGmtString(String gmtString, boolean acceptFallback) {
        return _getByStandardizedGmtString(toStandardizedGmtString(gmtString), acceptFallback);
    }

    /**
     * Gets a TimeZone object by a GMT string, optionally accepting the fallback option
     * @param gmtString a parseable GMT string
     * @param useDaylight whether or not the expected TimeZone will use Daylight Savings Time
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByGmtStringAndDst(String gmtString, boolean useDaylight) {
        String standard = toStandardizedGmtString(gmtString);
        if (standard != null) {
            String gmtStringKey = JavaZone.getGmtStringKey(standard, useDaylight);
            JavaZone jz = PRIMARY_BY_GMT_KEY.get(gmtStringKey);
            if (jz != null) {
                return jz.getTimeZone();
            }
            // this is really an error case, if didn't find it before, it's unlikely to be found here
            return _getByStandardizedGmtString(standard, true);
        }
        return null;
    }

    private TimeZone _getByStandardizedGmtString(String standard, boolean acceptFallback) {
        if (standard != null) {
            if (acceptFallback) {
                JavaZone fallback = FALLBACK_BY_GMT_STRING.get(standard);
                if (fallback != null) {
                    return fallback.getTimeZone();
                }
            }

            return TimeZone.getTimeZone(standard);
        }
        return null;
    }

    /**
     * Gets a TimeZone object by a location address string. Do not "defaultIfUnresolvedInMultiTzCountry"
     * Delegates directly to {@link LocationUtil#getByLocation(String, boolean)}
     * @param location a string representing some world location
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByLocation(String location) {
        return LOCATION_UTIL.getByLocation(location);
    }

    /**
     * Gets a TimeZone object by a location address string.
     * Delegates directly to {@link LocationUtil#getByLocation(String, boolean)}
     * @param location a string representing some world location
     * @param defaultIfUnresolvedInMultiTzCountry If true, if the string can resolve the country, but not any further, use the default TimeZone for that country
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByLocation(String location, boolean defaultIfUnresolvedInMultiTzCountry) {
        return LOCATION_UTIL.getByLocation(location, defaultIfUnresolvedInMultiTzCountry);
    }

    /**
     * Gets a TimeZone object by a location US address string. Do not "defaultIfUnresolvedInMultiTzCountry"
     * Delegates directly to {@link LocationUtil#getByUsLocation(String)}
     * @param location a string representing some world location
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsLocation(String location) {
        return LOCATION_UTIL.getByUsLocation(location);
    }

    /**
     * Gets a TimeZone object by a location US address string.
     * Delegates directly to {@link LocationUtil#getByUsLocation(String, boolean)}
     * @param location a string representing some world location
     * @param defaultIfUnresolvedInMultiTzCountry If true, if the string is not empty but can't resolve to a TimeZone, use the default TimeZone for the US
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsLocation(String location, boolean defaultIfUnresolvedInMultiTzCountry) {
        return LOCATION_UTIL.getByUsLocation(location, defaultIfUnresolvedInMultiTzCountry);
    }

    /**
     * Gets a TimeZone object by a US zipcode (number)
     * Delegates directly to {@link LocationUtil#getByUsZip(int)}
     * @param usZipcode a number representing a US 5 digit zipcode
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsZip(int usZipcode) {
        return LOCATION_UTIL.getByUsZip(usZipcode);
    }

    /**
     * Gets a TimeZone object by a US zipcode (String)
     * Delegates directly to {@link LocationUtil#getByUsZip(String)}
     * @param usZipcode a string representing a US 5 digit zipcode
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsZip(String usZipcode) {
        return LOCATION_UTIL.getByUsZip(usZipcode);
    }

    /**
     * Gets a string suitable for display from a TimeZone object. Replaces {@link TimeZone#toString}
     * @param timeZone the TimeZone
     * @return the TimeZone or null if it could not be resolved
     */
    public String toTimezoneDisplayString(TimeZone timeZone) {
        String raw = toStandardizedGmtString(getRawOffsetMinutes(timeZone));
        String current = toStandardizedGmtString(getCurrentOffsetMinutes(timeZone));
        if (raw.equals(current)) {
            return timeZone.getID() + SPACE + raw;
        }
        return timeZone.getID() + SPACE + current + " [" + raw + " adjusted for DST]";
    }

    /**
     * Helper method to get the current (as of right now) offset from GMT in milliseconds for the TimeZone
     * @see TimeZone#getOffset(long)
     * @param timeZone the TimeZone
     * @return offset from GMT in milliseconds
     */
    public int getCurrentOffsetMillis(TimeZone timeZone) {
        return timeZone.getOffset(System.currentTimeMillis());
    }

    /**
     * Helper method to get the current (as of right now) offset from GMT in minutes for the TimeZone
     * @see TimeZone#getOffset(long)
     * @param timeZone the TimeZone
     * @return offset from GMT in minutes
     */
    public int getCurrentOffsetMinutes(TimeZone timeZone) {
        return timeZone.getOffset(System.currentTimeMillis()) / 60000;
    }

    /**
     * Delegate method to get the raw offset for the TimeZone
     * @see TimeZone#getRawOffset for full explanation of Raw Offset
     * @param timeZone the TimeZone
     * @return raw offset in milliseconds
     */
    public int getRawOffsetMillis(TimeZone timeZone) {
        return timeZone.getRawOffset();
    }

    /**
     * Helper method to get the raw offset for the TimeZone in minutes
     * @see TimeZone#getRawOffset for full explanation of Raw Offset
     * @param timeZone the TimeZone
     * @return raw offset in minutes
     */
    public int getRawOffsetMinutes(TimeZone timeZone) {
        return timeZone.getRawOffset() / 60000;
    }

    /**
     * Gets the minutes offset from GMT for the gmt string
     * @param gmtString the gmt string
     * @return offset in minutes
     * @throws IOException if there is an error
     */
    public int toMinutes(String gmtString) throws IOException {
        try {
            return _toMinutes(gmtString);
        }
        catch (Exception e) {
            throw new IOException("Invalid timezone string: " + gmtString);
        }
    }

    private int _toMinutes(String gmtString) throws Exception {
        // GMT+1, GMT-00, GMT+01, GMT-11, GMT+800, GMT-830, GMT+1100, GMT-1130
        // GMT+1:00, GMT-00:00, GMT+01:00, GMT-11:00, GMT-11:30, GMT+2:0, GMT-06:0
        // May not even start with gmt

        String work = gmtString.trim().toUpperCase();
        int at = work.indexOf(GMT);
        if (at != -1) {
            work = work.substring(at + GMT.length());
        }

        boolean minus = false;
        int hours = 0;
        int minutes = 0;
        if (work.length() > 0) {
            int c = work.charAt(0);
            if (c == MINUS) {
                minus = true;
                work = work.substring(1);
            }
            else if (c == PLUS) {
                work = work.substring(1);
            }

           // 1, 00, 01, 11, 800, 830, 0800, 0830, 1100, 1130
           // 1:00, 00:00, 01:00, 11:00, 11:30, 2:0, 06:0

            String hrs = "00";
            String mins = "00";
            at = work.indexOf(COLON);
            if (at != -1) {
                hrs = work.substring(0, at);
                mins = work.substring(at + 1);
            }
            else {
                switch (work.length()) {
                    case 1:
                    case 2:
                        hrs = work;
                        break;
                    case 3:
                        hrs = work.substring(0, 1);
                        mins = work.substring(1);
                        break;
                    case 4:
                        hrs = work.substring(0, 2);
                        mins = work.substring(2);
                        break;
                    default:
                        throw new Exception();
                }
            }

            if (mins.length() == 2 && mins.charAt(0) == ZERO) {
                minutes = Integer.parseInt(mins.substring(1));
            }
            else {
                minutes = Integer.parseInt(mins);
            }

            if (hrs.length() == 2 && hrs.charAt(0) == ZERO) {
                hours = Integer.parseInt(hrs.substring(1));
            }
            else {
                hours = Integer.parseInt(hrs);
            }
        }

        int offsetMinutes = (hours * 60) + minutes;
        if (minus) {
            offsetMinutes *= -1;
        }
        return offsetMinutes;
    }

    /**
     * Normalize (standardize) a gmt string to the exact full format this utility class prefers
     * @param gmtString the gmt string
     * @return the normalized gmt string
     */
    public String toStandardizedGmtString(String gmtString)  {
        try {
            return toStandardizedGmtString(toMinutes(gmtString));
        }
        catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets a normalized (standardized) gmt string from gmt offset minutes
     * @param offsetMinutes the minutes offset from gmt
     * @return the normalized gmt string
     */
    public String toStandardizedGmtString(int offsetMinutes) {
        if (offsetMinutes == 0) {
            return GMT;
        }

        boolean minus = offsetMinutes < 0;
        if (minus) {
            offsetMinutes *= -1;
        }

        Integer hours = offsetMinutes / 60;
        Integer minutes = offsetMinutes - (hours * 60);

        StringBuilder sb = new StringBuilder();
        sb.append(GMT);
        if (minus) {
            sb.append(MINUS);
        }
        else {
            sb.append(PLUS);
        }
        if (hours < 10) {
            sb.append(ZERO);
        }
        sb.append(hours.toString());
        sb.append(COLON);
        if (minutes < 10) {
            sb.append(ZERO);
        }
        sb.append(minutes.toString());
        return sb.toString();
    }

    /**
     * Gets a key or extended standardized gmt string from a gmt string.
     * Useful for hash maps.
     * Considers the gmt string and the fact of whether to use daylight savings.
     * @param gmtString the gmt string
     * @param useDaylight whether the TimeZone should consider Daylight Savings Time
     * @return the gmt string key
     */
    public String getGmtStringKey(String gmtString, boolean useDaylight) {
        String standard = toStandardizedGmtString(gmtString);
        if (standard == null) {
            standard = gmtString;
        }
        return JavaZone.getGmtStringKey(standard, useDaylight);
    }

    /**
     * Gets a a list of all unique gmt strings
     * @return the list
     */
    public List<String> getAllUniqueGmtStrings() {
        return ALL_UNIQUE_GMT_STRINGS;
    }

    /**
     * Gets a a list of all unique offset minutes
     * @return the list
     */
    public List<Integer> getAllUniqueOffsetMinutes() {
        return ALL_UNIQUE_OFFSET_MINUTES;
    }

    /**
     * Gets a key or extended standardized gmt string from gmt offset minutes.
     * Useful for hash maps.
     * Considers the gmt string and the fact of whether to use daylight savings.
     * @param offsetMinutes the minutes offset from gmt
     * @param useDaylight whether the TimeZone should consider Daylight Savings Time
     * @return the gmt string key
     */
    public String getOffsetKey(int offsetMinutes, boolean useDaylight) {
        return JavaZone.getOffsetKey(offsetMinutes, useDaylight);
    }

    /**
     * Gets a key or extended standardized gmt string from a TimeZone.
     * Useful for hash maps.
     * Considers the gmt string and the fact of whether to use daylight savings.
     * @param timeZone the TimeZone
     * @return the gmt string key
     */
    public String getOffsetKey(TimeZone timeZone) {
        int rawOffsetMinutes = getRawOffsetMinutes(timeZone);
        return JavaZone.getOffsetKey(rawOffsetMinutes, timeZone.useDaylightTime());
    }

    private String validateInput(String input) {
        if (input == null) {
            return null;
        }
        input = input.trim();
        if (input.length() == 0) {
            return null;
        }
        return input;
    }

    private String validateInputUpper(String input) {
        if (input == null) {
            return null;
        }
        String upper = input.trim().toUpperCase();
        if (upper.length() == 0) {
            return null;
        }
        return upper;
    }

    private Map<String, String> ALL_JAVA_IDS;
    private Map<String, JavaZone> PRIMARY_BY_GMT_KEY;
    private Map<String, JavaZone> PRIMARY_BY_OFFSET_KEY;
    private Map<String, JavaZone> FALLBACK_BY_GMT_STRING;
    private Map<Integer, JavaZone> FALLBACK_BY_OFFSET;
    private Map<String, String> NON_JAVA_CODES_TO_ID;
    private LocationUtil LOCATION_UTIL;
    private List<String> ALL_UNIQUE_GMT_STRINGS;
    private List<Integer> ALL_UNIQUE_OFFSET_MINUTES;

    public TimeZoneUtil() throws TzException {
        super();
    }

    public TimeZoneUtil(ZoneDataSource zoneDataSource) throws TzException {
        super();
        setZoneDataSource(zoneDataSource);
    }

    @Override
    void _reload() throws TzException {
        Map<String, String> TEMP_ALL_JAVA_IDS = new HashMap<String, String>();
        Map<String, JavaZone> TEMP_PRIMARY_BY_GMT_KEY = new HashMap<String, JavaZone>();
        Map<String, JavaZone> TEMP_PRIMARY_BY_OFFSET_KEY = new HashMap<String, JavaZone>();
        Map<String, JavaZone> TEMP_FALLBACK_BY_GMT_STRING = new HashMap<String, JavaZone>();
        Map<Integer, JavaZone> TEMP_FALLBACK_BY_OFFSET = new HashMap<Integer, JavaZone>();
        List<String> TEMP_ALL_UNIQUE_GMT_STRINGS = new ArrayList<>();
        List<Integer> TEMP_ALL_UNIQUE_OFFSET_MINUTES = new ArrayList<>();

        String lastUniqueGmtStr = "notmatch";

        List<JavaZone> javaZones = getZoneDataSource().getJavaZones();
        for (JavaZone jz : javaZones) {
            String curUniqueGmtStr = jz.getGmtString();
            if (!curUniqueGmtStr.equals(lastUniqueGmtStr)) {
                TEMP_ALL_UNIQUE_GMT_STRINGS.add(curUniqueGmtStr);
                TEMP_ALL_UNIQUE_OFFSET_MINUTES.add(jz.getOffsetMinutes());
                lastUniqueGmtStr = curUniqueGmtStr;
            }

            TEMP_ALL_JAVA_IDS.put(jz.getId().toUpperCase(), jz.getId()); // just need to find not null

            if (jz.isPrimary()) {
                TEMP_PRIMARY_BY_GMT_KEY.put(jz.getGmtStringKey(), jz);
                TEMP_PRIMARY_BY_OFFSET_KEY.put(jz.getOffsetKey(), jz);
            }
            if (jz.isFallback()) {
                TEMP_FALLBACK_BY_GMT_STRING.put(jz.getGmtString(), jz);
                TEMP_FALLBACK_BY_OFFSET.put(jz.getOffsetMinutes(), jz);
            }
        }

        Map<String, String> TEMP_NON_JAVA_CODES_TO_ID = getZoneDataSource().getNonJavaCodesMap();

        ALL_JAVA_IDS = TEMP_ALL_JAVA_IDS;
        PRIMARY_BY_GMT_KEY = TEMP_PRIMARY_BY_GMT_KEY;
        PRIMARY_BY_OFFSET_KEY = TEMP_PRIMARY_BY_OFFSET_KEY;
        FALLBACK_BY_GMT_STRING = TEMP_FALLBACK_BY_GMT_STRING;
        FALLBACK_BY_OFFSET = TEMP_FALLBACK_BY_OFFSET;
        NON_JAVA_CODES_TO_ID = TEMP_NON_JAVA_CODES_TO_ID;
        ALL_UNIQUE_GMT_STRINGS = Collections.unmodifiableList(TEMP_ALL_UNIQUE_GMT_STRINGS);
        ALL_UNIQUE_OFFSET_MINUTES = Collections.unmodifiableList(TEMP_ALL_UNIQUE_OFFSET_MINUTES);

        LocationUtil TEMP_LOCATION_UTIL = new LocationUtil(getZoneDataSource());

        // if it gets here, there was no TzException it's safe to copy the temp
        // otherwise I didn't want to overwrite what was already loaded
        LOCATION_UTIL = TEMP_LOCATION_UTIL;
    }
}
