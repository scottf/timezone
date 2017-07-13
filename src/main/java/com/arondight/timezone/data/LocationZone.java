package com.arondight.timezone.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static com.arondight.timezone.TzConstants.*;

public class LocationZone implements HasConstructorString {
    private static final String SEP_REGEX = ",";

    private String country;
    private String state;
    private String stateName;
    private String cityName;
    private String id;
    private String[] altCountry;
    private String[] altState;

    public LocationZone(String record) {
        String[] parts = record.split(SEP_REGEX);
        if (parts[0].equals(US) || parts[0].equals(AU)) {
            // country code,state/province code,state/province name,city,timezoneId
            // US,NV,Nevada,,US/Pacific
            // US,NV,Nevada,Jackpot,US/Mountain
            // AU,NSW,New South Wales,,Australia/NSW
            // AU,NSW,New South Wales,Broken Hill,Australia/Broken_Hill
            country = parts[0];
            state = parts[1].toUpperCase();
            stateName = parts[2].toUpperCase();
            cityName = safeUpper(parts[3]);
            id = parts[4];
        }
        else if (parts[0].equals(CA)) {
            // CA,AB,Alberta
            // CA,PQ,Quebec,Canada/Eastern,QC
            country = parts[0];
            state = parts[1].toUpperCase();
            stateName = parts[2].toUpperCase();
            id = parts[3];
            altStates(parts, 4);
        }
        else if (parts[0].equals(NZ)) {
            // NZ,,Pacific/Auckland
            // NZ,CHATHAM,Pacific/Chatham
            country = parts[0];
            if (parts[1].length() > 0) {
                state = parts[1];
            }
            id = parts[2];
        }
        else {
            // country code, timezoneId, country name(,country name)*
            // UK,WET,UNITED KINGDOM,ENGLAND
            // PT,WET,PORTUGAL
            country = parts[0].toUpperCase();
            id = parts[1];
            altCountryNames(parts, 2);
        }
    }

    private void altCountryNames(String[] parts, int firstAltIndex) {
        int altCountryCount = parts.length - firstAltIndex;
        altCountry = new String[altCountryCount];
        if (altCountryCount > 0) {
            for (int x = 0; x < altCountryCount; x++) {
                altCountry[x] = parts[x + firstAltIndex];
            }
        }
    }

    private void altStates(String[] parts, int firstAltIndex) {
        int altStateCount = parts.length - firstAltIndex;
        altState = new String[altStateCount];
        if (altStateCount > 0) {
            for (int x = 0; x < altStateCount; x++) {
                altState[x] = parts[x + firstAltIndex];
            }
        }
    }

    private static String safeUpper(String s) {
        return s.length() == 0 ? null : s.toUpperCase();
    }

    public String getId() {
        return id;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(id);
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public List<String> getLocationKeys() {
        List<String> keys = new ArrayList<String>();
        if (country.equals(US) || country.equals(AU)) {
            if (cityName == null) {
                keys.add(toLocationKey(country, state, null));
                keys.add(toLocationKey(country, stateName, null));
            }
            else {
                keys.add(toLocationKey(country, state, cityName));
                keys.add(toLocationKey(country, stateName, cityName));
            }
        }
        else if (country.equals(CA)) {
            keys.add(toLocationKey(country, state, null));
            keys.add(toLocationKey(country, stateName, null));
            for (String alt : altState) {
                keys.add(toLocationKey(country, alt, null));
            }
        }
        else if (country.equals(NZ)) {
            keys.add(toLocationKey(country, state, null));
        }
        else {
            keys.add(toLocationKey(country, null, null));
            for (String alt : altCountry) {
                keys.add(toLocationKey(alt, null, null));
            }
        }
        return keys;
    }

    public static String toLocationKey(String country, String state, String city) {
        country = prepare(country);
        state = prepare(state);
        city = prepare(city);
        return (country + COMMA + state + COMMA + city).toUpperCase();
    }

    private static String prepare(String s) {
        if (s == null) {
            return EMTPY;
        }
        s = s.trim();
        return s.length() == 0 ? EMTPY : s.toUpperCase();
    }

    @Override
    public String toString() {
        return "LocationZone{" +
                "country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", stateName='" + stateName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public String toConstructorString() {
        StringBuilder sb = new StringBuilder();
        sb.append(country);

        if (country.equals(US) || country.equals(AU) || country.equals(CA)) {
            sb.append(COMMA);
            if (state != null) {
                sb.append(state);
            }

            sb.append(COMMA);
            sb.append(stateName);

            if (!country.equals(CA)) {
                sb.append(COMMA);
                if (cityName != null) {
                    sb.append(cityName);
                }
            }
        }
        else if (country.equals(NZ)) {
            sb.append(COMMA);
            if (state != null) {
                sb.append(state);
            }
        }
        sb.append(COMMA);
        sb.append(id);

        if (altState != null) {
            for (String alt : altState) {
                sb.append(COMMA);
                sb.append(alt);
            }
        }

        if (altCountry != null) {
            for (String alt : altCountry) {
                sb.append(COMMA);
                sb.append(alt);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationZone that = (LocationZone) o;

        if (!Arrays.equals(altCountry, that.altCountry)) return false;
        if (!Arrays.equals(altState, that.altState)) return false;
        if (cityName != null ? !cityName.equals(that.cityName) : that.cityName != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (stateName != null ? !stateName.equals(that.stateName) : that.stateName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (stateName != null ? stateName.hashCode() : 0);
        result = 31 * result + (cityName != null ? cityName.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (altCountry != null ? Arrays.hashCode(altCountry) : 0);
        result = 31 * result + (altState != null ? Arrays.hashCode(altState) : 0);
        return result;
    }
}
