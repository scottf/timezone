package com.arondight.timezone;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
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
