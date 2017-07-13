package com.arondight.timezone;

import com.arondight.timezone.data.JavaZone;
import com.arondight.timezone.data.LocationZone;
import com.arondight.timezone.data.ZoneDataSource;
import com.arondight.timezone.data.ZoneDataSourceInternal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TimeZoneBeanTests {

    private static ZoneDataSource _zoneDataSource;

    private ZoneDataSource getZoneDataSource() throws TzException {
        if (_zoneDataSource == null) {
            _zoneDataSource = new ZoneDataSourceInternal();
        }
        return _zoneDataSource;
    }

    @Test
    public void JavaZone() throws TzException {
        List<JavaZone> javaZones = getZoneDataSource().getJavaZones();
        for (JavaZone javaZone : javaZones) {
            JavaZone copy = new JavaZone(javaZone.toConstructorString());
            assertEquals(javaZone, copy);
        }
    }

    @Test
    public void LocationZone() throws TzException {
        List<LocationZone> locationZones = getZoneDataSource().getLocationZoneData();
        for (LocationZone locationZone : locationZones) {
            LocationZone copy = new LocationZone(locationZone.toConstructorString());
            assertEquals(locationZone, copy);
        }
    }
}
