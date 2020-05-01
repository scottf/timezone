package com.arondight.timezone;

public class TimeZoneUtilFileDataSourceTests extends TimeZoneUtilTests {

    private static TimeZoneUtil _timeZoneUtil;

    @Override
    protected TimeZoneUtil getTimeZoneUtil() throws TzException {
        if (_timeZoneUtil == null) {
            _timeZoneUtil = new TimeZoneUtil();
        }
        return _timeZoneUtil;
    }
}
