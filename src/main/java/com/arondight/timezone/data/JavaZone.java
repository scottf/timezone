package com.arondight.timezone.data;

import java.util.TimeZone;

import static com.arondight.timezone.TzConstants.*;

public class JavaZone implements HasConstructorString {
    private String id;
    private String gmtString;
    private boolean useDaylightTime;
    private boolean primary;
    private boolean fallback;
    private int offsetMinutes;

    public JavaZone(String record) {
        //US/Mountain                     |GMT-07:00|yes|primary    |fallback
        //America/Boise                   |GMT-07:00|yes|alternative|not
        //US/Arizona                      |GMT-07:00|no |primary    |not
        String[] fields = record.split(SEP_REGEX);
        id =              fields[0].trim();
        gmtString =       fields[1].trim();
        useDaylightTime = fields[2].trim().equals(YES);
        primary =         fields[3].trim().equals(PRIMARY);
        fallback =        fields[4].trim().equals(FALLBACK);
        initOffsetMinutes();
    }

    public JavaZone(String timezone_id, String gmt_string, boolean use_daylight, boolean is_primary, boolean is_fallback) {
        id =              timezone_id;
        gmtString =       gmt_string;
        useDaylightTime = use_daylight;
        primary =         is_primary;
        fallback =        is_fallback;
        initOffsetMinutes();
    }

    private void initOffsetMinutes() {
        if (gmtString.equals(GMT)) {
            offsetMinutes = 0;
        }
        else {
            //012345678
            //GMT-01:00
            normalGmtStringToMinutes();
        }
    }

    /*
    create table java_zone (
        timezone_id varchar(150) not null,
        gmt_string varchar(10) not null,
        use_daylight_time bit not null,
        is_primary bit not null,
        is_fallback bit not null,
    );

     */
    private void normalGmtStringToMinutes() {
        int i = Integer.parseInt(gmtString.substring(4, 6));
        offsetMinutes = i * 60;
        i = Integer.parseInt(gmtString.substring(7));
        offsetMinutes += i;
        if (gmtString.charAt(3) == MINUS) {
            offsetMinutes *= -1;
        }
    }

    public String getGmtStringKey() {
        return getGmtStringKey(gmtString, useDaylightTime);
    }

    public String getOffsetKey() {
        return getOffsetKey(offsetMinutes, useDaylightTime);
    }

    public String getId() {
        return id;
    }

    public String getGmtString() {
        return gmtString;
    }

    public boolean isUseDaylightTime() {
        return useDaylightTime;
    }

    public boolean isPrimary() {
        return primary;
    }

    public boolean isFallback() {
        return fallback;
    }

    public int getOffsetMinutes() {
        return offsetMinutes;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(id);
    }

    public static String getGmtStringKey(String standardGmtString, boolean useDaylightTime) {
        return standardGmtString + COLON + COLON + useDaylightTime;
    }

    public static String getOffsetKey(int offsetMinutes, boolean useDaylightTime) {
        return Integer.toString(offsetMinutes) + COLON + COLON + useDaylightTime;
    }

    @Override
    public String toConstructorString() {
        return id
                + PIPE + gmtString
                + PIPE + (useDaylightTime ? YES : NO)
                + PIPE + (primary ? PRIMARY : ALTERNATIVE)
                + PIPE + (fallback ? FALLBACK : NOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaZone javaZone = (JavaZone) o;

        if (fallback != javaZone.fallback) return false;
        if (offsetMinutes != javaZone.offsetMinutes) return false;
        if (primary != javaZone.primary) return false;
        if (useDaylightTime != javaZone.useDaylightTime) return false;
        if (gmtString != null ? !gmtString.equals(javaZone.gmtString) : javaZone.gmtString != null) return false;
        if (id != null ? !id.equals(javaZone.id) : javaZone.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (gmtString != null ? gmtString.hashCode() : 0);
        result = 31 * result + (useDaylightTime ? 1 : 0);
        result = 31 * result + (primary ? 1 : 0);
        result = 31 * result + (fallback ? 1 : 0);
        result = 31 * result + offsetMinutes;
        return result;
    }
}
