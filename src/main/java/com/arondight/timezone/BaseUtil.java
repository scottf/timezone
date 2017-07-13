package com.arondight.timezone;

import com.arondight.timezone.data.ZoneDataSource;
import com.arondight.timezone.data.ZoneDataSourceInternal;

abstract class BaseUtil {

    private ZoneDataSource zoneDataSource;
    private long dateLoaded;

    BaseUtil() throws TzException {
        // this is the only constructor because I always want the internal data to load no matter what
        // this provides that if the first reload from a datasource fails, there is always data.
        setZoneDataSource( new ZoneDataSourceInternal() );
    }

    ZoneDataSource getZoneDataSource() {
        return zoneDataSource;
    }

    /**
     * Set the data source for the data. Reloads the data
     * @param zoneDataSource the ZoneDataSource implementation
     */
    public void setZoneDataSource(ZoneDataSource zoneDataSource) throws TzException {
        this.zoneDataSource = zoneDataSource;
        reload();
    }

    /**
     * Reload the data if the data has changed
     */
    public void reloadIfChanged() throws TzException {
        // has the data source been updated since we loaded it?
        if (zoneDataSource.getLastUpdated() > dateLoaded) {
            reload();
        }
    }

    /**
     * Reload the data
     */
    public void reload() throws TzException {
        try {
            zoneDataSource.beforeReads();
            _reload();
            dateLoaded = System.currentTimeMillis();
        }
        finally {
            zoneDataSource.afterReads();
        }
    }

    abstract void _reload() throws TzException;
}
