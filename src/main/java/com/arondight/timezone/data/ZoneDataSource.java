package com.arondight.timezone.data;

import com.arondight.timezone.TzException;

import java.util.List;
import java.util.Map;

public interface ZoneDataSource {
    void beforeReads();
    void afterReads();
    void close() throws TzException;
    long getLastUpdated() throws TzException;
    List<JavaZone> getJavaZones() throws TzException;
    List<LocationZone> getLocationZoneData() throws TzException;
    Map<String, String> getNonJavaCodesMap() throws TzException;
    Map<String, String> getZipToUsMap() throws TzException;
    Map<String, String> getUsStatesMap() throws TzException;
    Map<String, String> getAuStatesMap() throws TzException;
}
