package com.arondight.timezone.data;

import com.arondight.timezone.TzException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arondight.timezone.TzConstants.*;

public class ZoneDataSourceInternal implements ZoneDataSource {
    @Override
    public long getLastUpdated() throws TzException {
        // this will always make the data seem up to date, which it is since it NEVER changes
        return 0;
    }

    @Override
    public void beforeReads() {
        // no need to do anything;
    }

    @Override
    public void afterReads() {
        // no need to do anything;
    }

    @Override
    public void close() throws TzException {
        // no need to do anything;
    }

    @Override
    public List<JavaZone> getJavaZones() throws TzException {
        final List<JavaZone> list = new ArrayList<JavaZone>();
        read("JavaZoneData.txt", new LineReader() {
            @Override
            public void process(String line) {
                list.add( new JavaZone(line) );
            }
        });
        return list;
    }

    @Override
    public List<LocationZone> getLocationZoneData() throws TzException {
        final List<LocationZone> list = new ArrayList<LocationZone>();
        read("LocationZoneData.txt", new LineReader() {
            @Override
            public void process(String line) {
                list.add( new LocationZone(line) );
            }
        });
        return list;
    }

    @Override
    public Map<String, String> getNonJavaCodesMap() throws TzException {
        final Map<String, String> map = new HashMap<String, String>();
        read("NonJavaCodesData.txt", new LineReader() {
            @Override
            public void process(String line) {
                String[] split = line.split(COMMA);
                map.put(split[0], split[1]);
            }
        });
        return map;
    }

    @Override
    public Map<String, String> getZipToUsMap() throws TzException {
        final Map<String, String> map = new HashMap<String, String>();
        read("ZipToStateSimplifiedData.txt", new LineReader() {
            @Override
            public void process(String line) {
                String[] split = line.split(COMMA);
                map.put(split[0], split[1]);
            }
        });
        return map;
    }

    @Override
    public Map<String, String> getUsStatesMap() throws TzException {
        return _getStatesMap("UsStatesData.txt");
    }

    @Override
    public Map<String, String> getAuStatesMap() throws TzException {
        return _getStatesMap("AuStatesData.txt");
    }

    private Map<String, String> _getStatesMap(String filename) throws TzException {
        final Map<String, String> map = new HashMap<String, String>();
        read(filename, new LineReader() {
            @Override
            public void process(String line) {
                String[] split = line.split(COMMA);
                map.put(split[0], split[0]);
                map.put(split[1].toUpperCase(), split[0]);
            }
        });
        return map;
    }

    private void read(String file, LineReader lineReader) throws TzException {
        try {
            InputStream inputStream = ZoneDataSourceInternal.class.getResourceAsStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(isr);
            String line = in.readLine();
            while (line != null) {
                line = line.trim();
                if (isDataLine(line)) {
                    lineReader.process(line);
                }
                line = in.readLine();
            }
            in.close();
        }
        catch (IOException e) {
            // really have no idea what to do if this fails
            throw new TzException(e);
        }
    }

    /* inner */ interface LineReader {
        void process(String line);
    }

    private boolean isDataLine(String line) {
        return line.length() > 0 &&
                !line.startsWith(DATA_COMMENT_SLASHES) &&
                !line.startsWith(DATA_COMMENT_POUND) &&
                !line.startsWith(DATA_COMMENT_EXCLAIM);
    }
}
