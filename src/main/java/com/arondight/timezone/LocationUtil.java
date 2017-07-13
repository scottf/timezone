package com.arondight.timezone;

import com.arondight.timezone.data.LocationZone;
import com.arondight.timezone.data.ZoneDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.arondight.timezone.TzConstants.*;

/**
 * The LocationUtil deals directly with address location. Some {@link TimeZoneUtil} methods simply delegate directly to this utility.
 * <a id="lsinput"/><h4>Location String Input</h4>
 *     A locations string is really an address or part of an address for anywhere in the world.
 *     The most important part of the address is the country or country abbreviation as 95% of all
 *     countries reside in 1 timezone. For other countries, like the US, Canada, Australia and others,
 *     more information like a state name or abbreviation or postal / zip code are required.
 *     <p>
 *     This utility will do it's best to parse an address and find a TimeZone.
 *     If the utility cannot determine the TimeZone, the result of the function will be null.
 *
 * <h4>Multiple TimeZone Countries</h4>
 *     The following countries have multiple TimeZones where if only the country can be recognized versus some smaller more specific
 *     area such as a region, state or a postal code, the function will return null if <code>defaultIfUnresolvedInMultiTzCountry</code>
 *     is set to false, or the following if <code>defaultIfUnresolvedInMultiTzCountry</code> is set to true:
 *     <blockquote>
 *     <table border=0 cellspacing=3 cellpadding=3>
 *         <tr bgcolor="#ccccff"><th align=left>Country</th><th align=left>Default TimeZone ID</th></tr>
 *         <tr><td>United States</td><td>America/New_York</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Canada</td><td>Canada/Eastern</td></tr>
 *         <tr><td>Australia</td><td>Australia/NSW</td></tr>
 *     </table>
 *     </blockquote>
 *
 *     The following countries have multiple TimeZones, but are not currently supported, meaning they will always return a default TimeZone
 *     <blockquote>
 *     <table border=0 cellspacing=3 cellpadding=3>
 *         <tr bgcolor="#ccccff"><th align=left>Country</th><th align=left>Default TimeZone ID</th></tr>
 *         <tr><td>Brazil</td><td>America/Sao_Paulo</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Russia</td><td>Europe/Moscow</td></tr>
 *         <tr><td>Mexico</td><td>America/Mexico_City</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Antarctica</td><td>GMT</td></tr>
 *     </table>
 *     </blockquote>
 *
 * <h4>Example Address Locations</h4>
 *     Here are some example addresses and the TimeZone ID that they represent. The parsing algorithm looks essentially from right to left,
 *     so building number and street are not important. Country is required unless you are using the US specific methods.
 *     The alogrithim is pretty forgiving, but the address should be in a relatively standard format.
 *     <blockquote>
 *     <table border=0 cellspacing=3 cellpadding=3>
 *         <tr bgcolor="#ccccff"><th align=left>Address</th><th align=left>TimeZone ID</th></tr>
 *         <tr><td>123 Main St, Jackpot, NV, USA</td><td>US/Mountain</td></tr>
 *         <tr bgcolor="#eeeeff"><td>123 Main St, West Wendover, NV, US</td><td>US/Mountain</td></tr>
 *         <tr><td>123 Main St, Reno, NV, United States</td><td>US/Pacific</td></tr>
 *         <tr bgcolor="#eeeeff"><td>NV, USA</td><td>US/Pacific</td></tr>
 *         <tr><td>Melbourne VIC, Australia</td><td>Australia/Victoria</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Adelaide SA 5000, Australia</td><td>Australia/South</td></tr>
 *         <tr><td>Mont-Saint-Hilaire, QC, Canada</td><td>Canada/Eastern</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Mont-Saint-Hilaire, Quebec, Canada</td><td>Canada/Eastern</td></tr>
 *         <tr><td>Mont-Saint-Hilaire, Quebec, CA</td><td>Canada/Eastern</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Edmonton, AB, Canada</td><td>Canada/Mountain</td></tr>
 *         <tr><td>San Diego, CA, USA</td><td>US/Pacific</td></tr>
 *         <tr bgcolor="#eeeeff"><td>San Diego, CA 92122, USA</td><td>US/Pacific</td></tr>
 *         <tr><td>Fooville, MN, US</td><td>US/Central</td></tr>
 *         <tr bgcolor="#eeeeff"><td>New York, NY, US</td><td>US/Eastern</td></tr>
 *         <tr><td>FL,US</td><td>US/Eastern</td></tr>
 *         <tr bgcolor="#eeeeff"><td>FLoRida,  USA</td><td>US/Eastern</td></tr>
 *         <tr><td>Top View Ln, Williamson, AZ 86305, USA</td><td>US/Arizona</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Old Greenwich, Greenwich, CT, USA</td><td>US/Eastern</td></tr>
 *         <tr><td>South Carolina, USA</td><td>US/Eastern</td></tr>
 *         <tr bgcolor="#eeeeff"><td>New Zealand</td><td>Pacific/Auckland</td></tr>
 *         <tr><td>Chatham, New Zealand</td><td>Pacific/Chatham</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Melbourne VIC, Australia</td><td>Australia/Victoria</td></tr>
 *         <tr><td>Cardiff, UK</td><td>Europe/London</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Chepstow, Monmouthshire NP16, UK</td><td>Europe/London</td></tr>
 *         <tr><td>Hertfordshire, UK</td><td>Europe/London</td></tr>
 *         <tr bgcolor="#eeeeff"><td>United Kingdom</td><td>Europe/London</td></tr>
 *         <tr><td>L'Ha?-les-Roses, France</td><td>Europe/Paris</td></tr>
 *         <tr bgcolor="#eeeeff"><td>L'Ha?-les-Roses, FR</td><td>Europe/Paris</td></tr>
 *         <tr><td>Munich, Germany</td><td>Europe/Berlin</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Munich, DE</td><td>Europe/Berlin</td></tr>
 *         <tr><td>Copenhagen, Denmark</td><td>Europe/Copenhagen</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Copenhagen, DK</td><td>Europe/Copenhagen</td></tr>
 *         <tr><td>Poland</td><td>Europe/Warsaw</td></tr>
 *         <tr bgcolor="#eeeeff"><td>PL</td><td>Europe/Warsaw</td></tr>
 *         <tr><td>Warsaw, Poland</td><td>Europe/Warsaw</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Stockholm County, Sweden</td><td>Europe/Stockholm</td></tr>
 *         <tr><td>Stockholm, Sweden</td><td>Europe/Stockholm</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Hantverkargatan 1, 112 21 Stockholm, Sweden</td><td>Europe/Stockholm</td></tr>
 *         <tr><td>Lund, SE</td><td>Europe/Stockholm</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Rotterdam, The Netherlands</td><td>Europe/Amsterdam</td></tr>
 *         <tr><td>Rotterdam, Netherlands</td><td>Europe/Amsterdam</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Rotterdam, NL</td><td>Europe/Amsterdam</td></tr>
 *         <tr><td>Nittedal, Norway</td><td>Europe/Oslo</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Nittedal, NO</td><td>Europe/Oslo</td></tr>
 *         <tr><td>Tel Aviv, Israel</td><td>Asia/Tel_Aviv</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Tel Aviv, IL</td><td>Asia/Tel_Aviv</td></tr>
 *         <tr><td>Hong Kong</td><td>Asia/Hong_Kong</td></tr>
 *         <tr bgcolor="#eeeeff"><td>HK</td><td>Asia/Hong_Kong</td></tr>
 *         <tr><td>Taipei City, Taiwan</td><td>Asia/Taipei</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Taipei City, TW</td><td>Asia/Taipei</td></tr>
 *         <tr><td>San Pablo City, Philippines</td><td>Asia/Manila</td></tr>
 *         <tr bgcolor="#eeeeff"><td>San Pablo City, PH</td><td>Asia/Manila</td></tr>
 *         <tr><td>Colmenar Viejo, Plaza del Pueblo, 1, 28770 Colmenar Viejo, Spain</td><td>Europe/Madrid</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Madrid, Spain</td><td>Europe/Madrid</td></tr>
 *         <tr><td>Madrid, ES</td><td>Europe/Madrid</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Portugal</td><td>Europe/Lisbon</td></tr>
 *         <tr><td>PT</td><td>Europe/Lisbon</td></tr>
 *         <tr bgcolor="#eeeeff"><td>BELGIUM</td><td>Europe/Brussels</td></tr>
 *         <tr><td>BE</td><td>Europe/Brussels</td></tr>
 *         <tr bgcolor="#eeeeff"><td>LUXEMBOURG</td><td>Europe/Luxembourg</td></tr>
 *         <tr><td>LU</td><td>Europe/Luxembourg</td></tr>
 *         <tr bgcolor="#eeeeff"><td>Johannesburg, South Africa</td><td>Africa/Johannesburg</td></tr>
 *     </table>
 *     </blockquote>
 *
 * <a id="zipcode"/><h4>US Zipcode</h4>
 *     Every 5 digit zipcode in the United States is in a specific TimeZone and this utility contains the data to determine the TimeZone from the zipcode.
 *     The majority of the time, the TimeZone can be determined by the first 3 digits of the zipcode!
 */
public class LocationUtil extends BaseUtil {

    /**
     * Get a TimeZone object by a location address string. Do not "defaultIfUnresolvedInMultiTzCountry"
     * @param location a string representing some world location
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByLocation(String location) {
        return getByLocation(location, false);
    }

    /**
     * Get a TimeZone object by a location address string.
     * @param location a string representing some world location
     * @param defaultIfUnresolvedInMultiTzCountry If true, if the string can resolve the country, but not any further, use the default TimeZone for that country
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByLocation(String location, boolean defaultIfUnresolvedInMultiTzCountry) {
        location = validateInput(location);
        if (location != null) {
            return _getByLocationPreValidated(location, defaultIfUnresolvedInMultiTzCountry);
        }

        return null;
    }

    private TimeZone _getByLocationPreValidated(String location, boolean defaultIfUnresolvedInMultiTzCountry) {
        String[] parts = location.split(COMMA);
        if (parts.length > 0) {
            String country = prep(parts[parts.length - 1]);
            if (country != null) {
                country = getCountryCode(country);
                if (country.equals(US)) {
                    return doUnitedStates(parts, defaultIfUnresolvedInMultiTzCountry);
                }
                if (country.equals(CA)) {
                    return doCanada(parts, defaultIfUnresolvedInMultiTzCountry);
                }
                if (country.equals(NZ)) {
                    return doNewZealand(parts);
                }
                if (country.equals(AU)) {
                    return doAustralia(parts, defaultIfUnresolvedInMultiTzCountry);
                }

                LocationZone locationZone = LOCATION_MAP.get(LocationZone.toLocationKey(country, null, null));
                if (locationZone != null) {
                    return locationZone.getTimeZone();
                }
            }
        }
        return null;
    }

    /**
     * Gets a TimeZone object by a location US address string. Do not "defaultIfUnresolvedInMultiTzCountry"
     * @param location a string representing some world location
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsLocation(String location) {
        return getByUsLocation(location, false);
    }

    /**
     * Gets a TimeZone object by a location US address string.
     * @param location a string representing some world location
     * @param defaultIfUnresolvedInMultiTzCountry If true, if the string is not empty but can't resolve to a TimeZone, use the default TimeZone for the US
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsLocation(String location, boolean defaultIfUnresolvedInMultiTzCountry) {
        location = validateInput(location);
        if (location != null) {
            String upper = location.toUpperCase();
            if (    upper.endsWith(US) ||
                    upper.endsWith(USA) ||
                    upper.endsWith(UNITED_STATES)
               ) {
                return _getByLocationPreValidated(location, defaultIfUnresolvedInMultiTzCountry);
            }
            return _getByLocationPreValidated(location + COMMA + US, defaultIfUnresolvedInMultiTzCountry);
        }
        return null;
    }

    /**
     * Get a TimeZone object by a US zipcode (number)
     * @param usZip a number representing a US 5 digit zipcode
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsZip(int usZip) {
        return getByUsZip(Integer.toString(usZip));
    }

    /**
     * Get a TimeZone object by a US zipcode (String)
     * @param usZip a string representing a US 5 digit zipcode
     * @return the TimeZone or null if it could not be resolved
     */
    public TimeZone getByUsZip(String usZip) {
        String state = null;
        if (usZip.length() > 1) {
            String key = usZip.substring(0, 2);
            state = ZIP_TO_US_STATES.get(key);
            if (state == null && usZip.length() > 2) {
                key = usZip.substring(0, 3);
                state = ZIP_TO_US_STATES.get(key);
            }
        }

        if (state != null) {
            LocationZone locationZone = LOCATION_MAP.get(LocationZone.toLocationKey(US, state, null));
            return locationZone == null ? null : locationZone.getTimeZone();
        }

        return null;
    }

    private String getCountryCode(String country) {
        country = country.toUpperCase();
        if (country.equals(US)) return US;
        if (country.equals(USA)) return US;
        if (country.equals(UNITED_STATES)) return US;
        if (country.equals(CA)) return CA;
        if (country.equals(CANADA)) return CA;
        if (country.equals(NZ)) return NZ;
        if (country.equals(NEW_ZEALAND)) return NZ;
        if (country.equals(AU)) return AU;
        if (country.equals(AUSTRALIA)) return AU;
        return country;
    }

    private TimeZone doUnitedStates(String[] parts, boolean defaultIfUnresolvedInMultiTzCountry) {
        return _doWithStates(parts, defaultIfUnresolvedInMultiTzCountry, US, US_STATES_NAME_OR_ABBR, US_NO_STATE_LOCATION_ZONE);
    }

    private TimeZone doAustralia(String[] parts, boolean defaultIfUnresolvedInMultiTzCountry) {
        return _doWithStates(parts, defaultIfUnresolvedInMultiTzCountry, AU, AU_STATES_NAME_OR_ABBR, AU_NO_STATE_LOCATION_ZONE);
    }

    private TimeZone _doWithStates(String[] parts, boolean defaultIfUnresolvedInMultiTzCountry, String country, Map<String, String> statesMap, LocationZone noStateLocationZone) {
        if (parts.length > 1) {
            String stateRaw = prep(parts[parts.length-2]);
            if (stateRaw != null) {
                stateRaw = stateRaw.toUpperCase();
                // if we can find it in the map, it's good
                String state = statesMap.get(stateRaw);
                // state field is sometimes no really state, try each word
                if (state == null) {
                    String[] split = stateRaw.split(SPACE);
                    int splitX = -1;
                    while (state == null && ++splitX < split.length) {
                        state = split[splitX];
                        state = statesMap.get(state);
                    }
                }

                if (state != null) {
                    // city is only important in few states
                    String city = null;
                    if (parts.length > 2) {
                        city = parts[parts.length-3];
                    }

                    LocationZone locationZone = LOCATION_MAP.get(LocationZone.toLocationKey(country, state, city));
                    if (locationZone == null && city != null) {
                        locationZone = LOCATION_MAP.get(LocationZone.toLocationKey(country, state, null));
                    }

                    if (locationZone != null) {
                        return locationZone.getTimeZone();
                    }
                }
            }
        }
        return defaultIfUnresolvedInMultiTzCountry ? noStateLocationZone.getTimeZone() : null;
    }

    private TimeZone doCanada(String[] parts, boolean defaultIfUnresolvedInMultiTzCountry) {
        if (parts.length > 1) {
            String state = prep(parts[parts.length-2]);
            if (state != null) {
                state = state.toUpperCase();
                LocationZone locationZone = LOCATION_MAP.get(LocationZone.toLocationKey(CA, state, null));
                if (locationZone != null) {
                    return locationZone.getTimeZone();
                }
            }
        }
        return defaultIfUnresolvedInMultiTzCountry ? CA_NO_STATE_LOCATION_ZONE.getTimeZone() : null;
    }

    private TimeZone doNewZealand(String[] parts) {
        LocationZone locationZone = null;

        if (parts.length > 1) {
            String state = prep(parts[parts.length-2]);
            if (state != null) {
                state = state.toUpperCase();
                locationZone = LOCATION_MAP.get(LocationZone.toLocationKey(NZ, state, null));
            }
            if (locationZone == null) {
                locationZone = NZ_LOCATION_ZONE;
            }
        }
        return locationZone == null ? NZ_LOCATION_ZONE.getTimeZone() : locationZone.getTimeZone();
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

    private String prep(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        return s.length() == 0 ? null : s;
    }

    private Map<String, LocationZone> LOCATION_MAP;
    private Map<String, String> US_STATES_NAME_OR_ABBR;
    private Map<String, String> AU_STATES_NAME_OR_ABBR;
    private Map<String, String> ZIP_TO_US_STATES;
    private LocationZone US_NO_STATE_LOCATION_ZONE;
    private LocationZone CA_NO_STATE_LOCATION_ZONE;
    private LocationZone AU_NO_STATE_LOCATION_ZONE;
    private LocationZone NZ_LOCATION_ZONE;

    public LocationUtil() throws TzException {
        super();
    }

    public LocationUtil(ZoneDataSource zoneDataSource) throws TzException {
        super();
        setZoneDataSource(zoneDataSource);
    }

    @Override
    void _reload() throws TzException {
        ZoneDataSource zds = getZoneDataSource();

        Map<String, LocationZone> TEMP_LOCATION_MAP = new HashMap<String, LocationZone>();
        List<LocationZone> locationZones = zds.getLocationZoneData();
        for (LocationZone lz : locationZones) {
            List<String> keys = lz.getLocationKeys();
            for (String key : keys) {
                TEMP_LOCATION_MAP.put(key, lz);
            }
        }

        LocationZone TEMP_US_NO_STATE_LOCATION_ZONE = TEMP_LOCATION_MAP.get(LocationZone.toLocationKey(US, NY, null));
        LocationZone TEMP_CA_NO_STATE_LOCATION_ZONE = TEMP_LOCATION_MAP.get(LocationZone.toLocationKey(CA, PQ, null));
        LocationZone TEMP_AU_NO_STATE_LOCATION_ZONE = TEMP_LOCATION_MAP.get(LocationZone.toLocationKey(AU, NSW, null));
        LocationZone TEMP_NZ_LOCATION_ZONE = TEMP_LOCATION_MAP.get(LocationZone.toLocationKey(NZ, null, null));

        Map<String, String> TEMP_US_STATES_NAME_OR_ABBR = zds.getUsStatesMap();
        Map<String, String> TEMP_AU_STATES_NAME_OR_ABBR = zds.getAuStatesMap();
        Map<String, String> TEMP_ZIP_TO_US_STATES = zds.getZipToUsMap();

        // if it gets here, there was no TzException it's safe to copy the temp
        // otherwise I didn't want to overwrite what was already loaded
        LOCATION_MAP = TEMP_LOCATION_MAP;

        US_NO_STATE_LOCATION_ZONE = TEMP_US_NO_STATE_LOCATION_ZONE;
        CA_NO_STATE_LOCATION_ZONE = TEMP_CA_NO_STATE_LOCATION_ZONE;
        AU_NO_STATE_LOCATION_ZONE = TEMP_AU_NO_STATE_LOCATION_ZONE;
        NZ_LOCATION_ZONE = TEMP_NZ_LOCATION_ZONE;

        US_STATES_NAME_OR_ABBR = TEMP_US_STATES_NAME_OR_ABBR;
        AU_STATES_NAME_OR_ABBR = TEMP_AU_STATES_NAME_OR_ABBR;
        ZIP_TO_US_STATES = TEMP_ZIP_TO_US_STATES;
    }
}
