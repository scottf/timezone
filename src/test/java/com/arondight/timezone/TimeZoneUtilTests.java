package com.arondight.timezone;

import org.junit.jupiter.api.Test;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class TimeZoneUtilTests {

    private static final String US_EASTERN = "US/Eastern";
    private static final String US_MOUNTAIN = "US/Mountain";
    private static final String US_PACIFIC = "US/Pacific";
    private static final String US_CENTRAL = "US/Central";
    private static final String AMERICA_NEW_YORK = "America/New_York";
    private static final String GMT_07_00 = "GMT-07:00";
    private static final String US_ARIZONA = "US/Arizona";
    private static final String CANADA_PACIFIC = "Canada/Pacific";
    private static final String CANADA_EASTERN = "Canada/Eastern";
    private static final String CANADA_MOUNTAIN = "Canada/Mountain";
    private static final String CANADA_ATLANTIC = "Canada/Atlantic";
    private static final String CANADA_CENTRAL = "Canada/Central";

    protected abstract TimeZoneUtil getTimeZoneUtil() throws TzException;

    @Test
    public void byUsLocation() throws TzException {
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getByUsLocation("123 Main, Jackpot, NV").getID());
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getByUsLocation("123 Main, West Wendover, NV").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByUsLocation("123 Main, NV").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByUsLocation("NV").getID());

        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getByUsLocation("123 Main, Jackpot, NV, USA").getID());
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getByUsLocation("123 Main, West Wendover, NV, US").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByUsLocation("123 Main, NV, United States").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByUsLocation("NV, US").getID());

        assertEquals(US_EASTERN, getTimeZoneUtil().getByUsLocation("XX", true).getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByUsLocation("USA", true).getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByUsLocation("XX, USA", true).getID());
    }

    @Test
    public void byZip() throws TzException {
        assertEquals(US_EASTERN, getTimeZoneUtil().getByUsZip(43016).getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByUsZip(92121).getID());
        assertEquals(US_ARIZONA, getTimeZoneUtil().getByUsZip(85032).getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByUsZip("00523").getID());
        assertEquals("America/Puerto_Rico", getTimeZoneUtil().getByUsZip("00623").getID());
        assertEquals("America/Virgin", getTimeZoneUtil().getByUsZip("00811").getID());

        assertEquals(US_EASTERN, getTimeZoneUtil().getByUsZip("430").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByUsZip("921").getID());
        assertEquals(US_ARIZONA, getTimeZoneUtil().getByUsZip("850").getID());
    }

    @Test
    public void byId() throws TzException {
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getById("GMT-7").getID());
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getById("GMT-7", true).getID());
        assertEquals(GMT_07_00,   getTimeZoneUtil().getById("GMT-7", false).getID());
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getById("GMT-07").getID());
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getById("GMT-07", true).getID());
        assertEquals(GMT_07_00, getTimeZoneUtil().getById("GMT-07", false).getID());
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getById("GMT-07:00").getID());
        assertEquals(GMT_07_00, getTimeZoneUtil().getById("GMT-07:00", false).getID());
        assertEquals(AMERICA_NEW_YORK, getTimeZoneUtil().getById(AMERICA_NEW_YORK).getID());
        assertEquals(AMERICA_NEW_YORK, getTimeZoneUtil().getById(AMERICA_NEW_YORK, true).getID());
        assertEquals(AMERICA_NEW_YORK, getTimeZoneUtil().getById(AMERICA_NEW_YORK, false).getID());
    }

    @Test
    public void byOffset() throws TzException {
        TimeZone tz = getTimeZoneUtil().getByOffset(-300);
        assertEquals(US_EASTERN, tz.getID());
        tz = getTimeZoneUtil().getByOffset(-300, true);
        assertEquals(US_EASTERN, tz.getID());
        tz = getTimeZoneUtil().getByOffset(-300, false);
        assertEquals("GMT-05:00", tz.getID());
    }

    @Test
    public void errorChecks() throws TzException {
        assertNull(getTimeZoneUtil().toStandardizedGmtString("blah"));
        assertNull(getTimeZoneUtil().getById(null));
        assertNull(getTimeZoneUtil().getById(""));
        assertNull(getTimeZoneUtil().getById("GMTxx"));
        assertNull(getTimeZoneUtil().getById("bogus"));
        assertNull(getTimeZoneUtil().getByLocation(null));
        assertNull(getTimeZoneUtil().getByLocation(""));
        assertNull(getTimeZoneUtil().getByLocation("San Diego, , USA"));
    }

    @Test
    public void byIdGmtString() throws TzException {
        assertEquals("GMT",        getTimeZoneUtil().getById("GMT").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("GMT+1").getID());
        assertEquals("WET",        getTimeZoneUtil().getById("GMT-00").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("GMT+01").getID());
        assertEquals("US/Samoa",   getTimeZoneUtil().getById("GMT-11").getID());
        assertEquals("Hongkong",   getTimeZoneUtil().getById("GMT+800").getID());
        assertEquals("GMT-08:30",  getTimeZoneUtil().getById("GMT-830").getID());
        assertEquals("SST",        getTimeZoneUtil().getById("GMT+1100").getID());
        assertEquals("GMT-11:30",  getTimeZoneUtil().getById("GMT-1130").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("GMT+1:00").getID());
        assertEquals("WET",        getTimeZoneUtil().getById("GMT-00:00").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("GMT+01:00").getID());
        assertEquals("US/Samoa",   getTimeZoneUtil().getById("GMT-11:00").getID());
        assertEquals("GMT-11:30",  getTimeZoneUtil().getById("GMT-11:30").getID());
        assertEquals("EET",        getTimeZoneUtil().getById("GMT+2:0").getID());
        assertEquals("US/Central", getTimeZoneUtil().getById("GMT-06:0").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("+1").getID());
        assertEquals("WET",        getTimeZoneUtil().getById("-00").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("+01").getID());
        assertEquals("US/Samoa",   getTimeZoneUtil().getById("-11").getID());
        assertEquals("Hongkong",   getTimeZoneUtil().getById("+800").getID());
        assertEquals("GMT-08:30",  getTimeZoneUtil().getById("-830").getID());
        assertEquals("SST",        getTimeZoneUtil().getById("+1100").getID());
        assertEquals("GMT-11:30",  getTimeZoneUtil().getById("-1130").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("+1:00").getID());
        assertEquals("WET",        getTimeZoneUtil().getById("-00:00").getID());
        assertEquals("CET",        getTimeZoneUtil().getById("+01:00").getID());
        assertEquals("US/Samoa",   getTimeZoneUtil().getById("-11:00").getID());
        assertEquals("GMT-11:30",  getTimeZoneUtil().getById("-11:30").getID());
        assertEquals("EET",        getTimeZoneUtil().getById("+2:0").getID());
        assertEquals("US/Central", getTimeZoneUtil().getById("-06:0").getID());
    }

    @Test
    public void multipleTimezoneCountriesLocation() throws TzException {
        assertEquals("Australia/Victoria", getTimeZoneUtil().getByLocation("Melbourne VIC, Australia").getID());
        assertEquals("Australia/South", getTimeZoneUtil().getByLocation("Adelaide SA 5000, Australia").getID());

        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Mont-Saint-Hilaire, PQ, CA").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Mont-Saint-Hilaire, PQ, Canada").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Mont-Saint-Hilaire, QC, Canada").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Mont-Saint-Hilaire, Quebec, Canada").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Mont-Saint-Hilaire, Quebec, CA").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Ottawa, ON, CA").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Ottawa, ON, Canada").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Whitby, ON, Canada").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Toronto, ON, Canada").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Thornhill, Vaughan, ON, Canada").getID());
        assertEquals(CANADA_EASTERN, getTimeZoneUtil().getByLocation("Whitby, Ontario, Canada").getID());
        assertEquals(CANADA_MOUNTAIN, getTimeZoneUtil().getByLocation("Edmonton, AB, Canada").getID());
        assertEquals(CANADA_MOUNTAIN, getTimeZoneUtil().getByLocation("Edmonton, Alberta, Canada").getID());
        assertEquals(CANADA_PACIFIC, getTimeZoneUtil().getByLocation("Vancouver, BC, Canada").getID());
        assertEquals(CANADA_PACIFIC, getTimeZoneUtil().getByLocation("Vancouver, British Columbia, Canada").getID());
        assertEquals(CANADA_CENTRAL, getTimeZoneUtil().getByLocation("MB, Canada").getID());
        assertEquals(CANADA_CENTRAL, getTimeZoneUtil().getByLocation("Manitoba, Canada").getID());
        assertEquals(CANADA_ATLANTIC, getTimeZoneUtil().getByLocation("NB, Canada").getID());
        assertEquals(CANADA_ATLANTIC, getTimeZoneUtil().getByLocation("New Brunswick, Canada").getID());
        assertEquals(CANADA_MOUNTAIN, getTimeZoneUtil().getByLocation("NT, Canada").getID());
        assertEquals(CANADA_MOUNTAIN, getTimeZoneUtil().getByLocation("Northwest Territories, Canada").getID());
        assertEquals(CANADA_ATLANTIC, getTimeZoneUtil().getByLocation("NS, Canada").getID());
        assertEquals(CANADA_ATLANTIC, getTimeZoneUtil().getByLocation("Nova Scotia, Canada").getID());
        assertEquals(CANADA_CENTRAL, getTimeZoneUtil().getByLocation("SK, Canada").getID());
        assertEquals(CANADA_CENTRAL, getTimeZoneUtil().getByLocation("Saskatchewan, Canada").getID());
        assertEquals(CANADA_PACIFIC, getTimeZoneUtil().getByLocation("YT, Canada").getID());
        assertEquals(CANADA_PACIFIC, getTimeZoneUtil().getByLocation("Yukon Territory, Canada").getID());

        assertEquals(US_PACIFIC, getTimeZoneUtil().getByLocation("San Diego, CA, USA").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByLocation("San Diego, CA 92122, USA").getID());
        assertEquals(US_CENTRAL, getTimeZoneUtil().getByLocation("Fooville, MN, US").getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByLocation("New York, NY, US").getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByLocation("FL,US").getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByLocation("FLoRida,  USA").getID());
        assertEquals(US_ARIZONA, getTimeZoneUtil().getByLocation("Top View Ln, Williamson, AZ 86305, USA").getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByLocation("Old Greenwich, Greenwich, CT, USA").getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByLocation("South Carolina, USA").getID());

        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getByLocation("123 Main, Jackpot, NV, USA").getID());
        assertEquals(US_MOUNTAIN, getTimeZoneUtil().getByLocation("123 Main, West Wendover, NV, US").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByLocation("123 Main, NV, United States").getID());
        assertEquals(US_PACIFIC, getTimeZoneUtil().getByLocation("NV, US").getID());

        assertEquals("Pacific/Auckland", getTimeZoneUtil().getByLocation("New Zealand").getID());
        assertEquals("Pacific/Auckland", getTimeZoneUtil().getByLocation("Christchurch, New Zealand").getID());
        assertEquals("Pacific/Chatham", getTimeZoneUtil().getByLocation("Chatham, New Zealand").getID());
        assertEquals("Pacific/Chatham", getTimeZoneUtil().getByLocation("CHaThAm, New Zealand").getID());
    }

    @Test
    public void multipleTimezoneCountriesLocationGuess() throws TzException {
        assertEquals("Australia/NSW", getTimeZoneUtil().getByLocation("Australia", true).getID());
        assertEquals(US_EASTERN, getTimeZoneUtil().getByLocation("US", true).getID());
        assertEquals("Canada/Eastern", getTimeZoneUtil().getByLocation("cAnAdA", true).getID());
    }

    @Test
    public void multipleTimezoneCountriesTemporarilyCodedAsOneTimezone() throws TzException {
        assertEquals("America/Sao_Paulo", getTimeZoneUtil().getByLocation("S�o Paulo, Brazil").getID());
        assertEquals("America/Sao_Paulo", getTimeZoneUtil().getByLocation("Vila Velha - Espirito Santo, Brazil").getID());
        assertEquals("America/Sao_Paulo", getTimeZoneUtil().getByLocation("Curitiba - Paran�, Brazil").getID());
        assertEquals("America/Sao_Paulo", getTimeZoneUtil().getByLocation("Rio de Janeiro, Brazil").getID());
        assertEquals("America/Sao_Paulo", getTimeZoneUtil().getByLocation("Brazil").getID());
        assertEquals("America/Sao_Paulo", getTimeZoneUtil().getByLocation("BR").getID());

        assertEquals("Europe/Moscow", getTimeZoneUtil().getByLocation("Moscow, Russia", true).getID());
        assertEquals("Europe/Moscow", getTimeZoneUtil().getByLocation("Russia", true).getID());
        assertEquals("Europe/Moscow", getTimeZoneUtil().getByLocation("RU", true).getID());

        assertEquals("America/Mexico_City", getTimeZoneUtil().getByLocation("Somewhere, Mexico").getID());

        assertEquals("GMT", getTimeZoneUtil().getByLocation("Antarctica").getID());
    }

    @Test
    public void oneTimezoneCountriesLocationManual() throws TzException {
        assertEquals("Asia/Dubai", getTimeZoneUtil().getByLocation("Dubai,United Arab Emirates").getID());
        assertEquals("Asia/Dubai", getTimeZoneUtil().getByLocation("United Arab Emirates").getID());

        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("Cardiff, UK").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("Chepstow, Monmouthshire NP16, UK").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("Hertfordshire, UK").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("Leeds, West Yorkshire LS16, UK").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("London, UK").getID());

        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("United Kingdom").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("Rochester, Medway, UK").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("Sheffield, South Yorkshire, UK").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("York, UK").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("York, EnglanD").getID());

        assertEquals("Europe/Paris", getTimeZoneUtil().getByLocation("Asni?res-sur-V?gre, France").getID());
        assertEquals("Europe/Paris", getTimeZoneUtil().getByLocation("L'Ha?-les-Roses, France").getID());
        assertEquals("Europe/Paris", getTimeZoneUtil().getByLocation("L'Ha?-les-Roses, FR").getID());
        assertEquals("Europe/Berlin", getTimeZoneUtil().getByLocation("Cologne, Germany").getID());
        assertEquals("Europe/Berlin", getTimeZoneUtil().getByLocation("Munich, Germany").getID());
        assertEquals("Europe/Berlin", getTimeZoneUtil().getByLocation("Munich, DE").getID());
        assertEquals("Europe/Copenhagen", getTimeZoneUtil().getByLocation("Copenhagen, Denmark").getID());
        assertEquals("Europe/Copenhagen", getTimeZoneUtil().getByLocation("Copenhagen, DK").getID());
        assertEquals("Europe/Warsaw", getTimeZoneUtil().getByLocation("Poland").getID());
        assertEquals("Europe/Warsaw", getTimeZoneUtil().getByLocation("PL").getID());
        assertEquals("Europe/Warsaw", getTimeZoneUtil().getByLocation("Warsaw, Poland").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("Stockholm County, Sweden").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("Stockholm, Sweden").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("Hantverkargatan 1, 112 21 Stockholm, Sweden").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("Hytlebruk, Sweden").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("Lund, Sweden").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("Malm?, Sweden").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("Lund, SE").getID());
        assertEquals("Europe/Amsterdam", getTimeZoneUtil().getByLocation("Rotterdam, The Netherlands").getID());
        assertEquals("Europe/Amsterdam", getTimeZoneUtil().getByLocation("Rotterdam, Netherlands").getID());
        assertEquals("Europe/Amsterdam", getTimeZoneUtil().getByLocation("Rotterdam, NL").getID());
        assertEquals("Europe/Oslo", getTimeZoneUtil().getByLocation("Nittedal, Norway").getID());
        assertEquals("Europe/Oslo", getTimeZoneUtil().getByLocation("Nittedal, NO").getID());

        assertEquals("Asia/Tel_Aviv", getTimeZoneUtil().getByLocation("Tel Aviv, Israel").getID());
        assertEquals("Asia/Tel_Aviv", getTimeZoneUtil().getByLocation("Tel Aviv, IL").getID());

        assertEquals("Asia/Hong_Kong", getTimeZoneUtil().getByLocation("Hong Kong").getID());
        assertEquals("Asia/Hong_Kong", getTimeZoneUtil().getByLocation("HK").getID());
        assertEquals("Asia/Taipei", getTimeZoneUtil().getByLocation("Taipei City, Taiwan").getID());
        assertEquals("Asia/Taipei", getTimeZoneUtil().getByLocation("Taipei City, TW").getID());

        assertEquals("Asia/Manila", getTimeZoneUtil().getByLocation("San Pablo City, Philippines").getID());
        assertEquals("Asia/Manila", getTimeZoneUtil().getByLocation("San Pablo City, PH").getID());

        assertEquals("Europe/Madrid", getTimeZoneUtil().getByLocation("Colmenar Viejo, Plaza del Pueblo, 1, 28770 Colmenar Viejo, Spain").getID());
        assertEquals("Europe/Madrid", getTimeZoneUtil().getByLocation("Madrid, Spain").getID());
        assertEquals("Europe/Madrid", getTimeZoneUtil().getByLocation("Madrid, ES").getID());

        assertEquals("Europe/Lisbon", getTimeZoneUtil().getByLocation("Portugal").getID());
        assertEquals("Europe/Lisbon", getTimeZoneUtil().getByLocation("PT").getID());

        assertEquals("Europe/Brussels", getTimeZoneUtil().getByLocation("BELGIUM").getID());
        assertEquals("Europe/Brussels", getTimeZoneUtil().getByLocation("BE").getID());
        assertEquals("Europe/Luxembourg", getTimeZoneUtil().getByLocation("LUXEMBOURG").getID());
        assertEquals("Europe/Luxembourg", getTimeZoneUtil().getByLocation("LU").getID());

        assertEquals("Africa/Johannesburg", getTimeZoneUtil().getByLocation("Johannesburg, South Africa").getID());
    }

    @Test
    public void oneTimezoneCountriesLocationAuto() throws TzException {
        assertEquals("Asia/Kabul", getTimeZoneUtil().getByLocation("AFGHANISTAN").getID());
        assertEquals("Europe/Tirane", getTimeZoneUtil().getByLocation("ALBANIA").getID());
        assertEquals("Africa/Algiers", getTimeZoneUtil().getByLocation("ALGERIA").getID());
        assertEquals("Pacific/Pago_Pago", getTimeZoneUtil().getByLocation("AMERICAN SAMOA").getID());
        assertEquals("Europe/Andorra", getTimeZoneUtil().getByLocation("ANDORRA").getID());
        assertEquals("Africa/Lagos", getTimeZoneUtil().getByLocation("ANGOLA").getID());
        assertEquals("America/Anguilla", getTimeZoneUtil().getByLocation("ANGUILLA").getID());
        assertEquals("America/Antigua", getTimeZoneUtil().getByLocation("ANTIGUA AND BARBUDA").getID());
        assertEquals("AGT", getTimeZoneUtil().getByLocation("ARGENTINA").getID());
        assertEquals("Asia/Yerevan", getTimeZoneUtil().getByLocation("ARMENIA").getID());
        assertEquals("America/Aruba", getTimeZoneUtil().getByLocation("ARUBA").getID());
        assertEquals("Australia/West", getTimeZoneUtil().getByLocation("ASHMORE AND CARTIER ISLANDS").getID());
        assertEquals("Europe/Vienna", getTimeZoneUtil().getByLocation("AUSTRIA").getID());
        assertEquals("Asia/Baku", getTimeZoneUtil().getByLocation("AZERBAIJAN").getID());
        assertEquals("America/Nassau", getTimeZoneUtil().getByLocation("BAHAMAS").getID());
        assertEquals("Asia/Bahrain", getTimeZoneUtil().getByLocation("BAHRAIN").getID());
        assertEquals("Etc/GMT+12", getTimeZoneUtil().getByLocation("BAKER ISLAND").getID());
        assertEquals("Asia/Dhaka", getTimeZoneUtil().getByLocation("BANGLADESH").getID());
        assertEquals("America/Barbados", getTimeZoneUtil().getByLocation("BARBADOS").getID());
        assertEquals("EAT", getTimeZoneUtil().getByLocation("BASSAS DA INDIA").getID());
        assertEquals("Europe/Minsk", getTimeZoneUtil().getByLocation("BELARUS").getID());
        assertEquals("Europe/Brussels", getTimeZoneUtil().getByLocation("BELGIUM").getID());
        assertEquals("America/Belize", getTimeZoneUtil().getByLocation("BELIZE").getID());
        assertEquals("Africa/Porto-Novo", getTimeZoneUtil().getByLocation("BENIN").getID());
        assertEquals("Atlantic/Bermuda", getTimeZoneUtil().getByLocation("BERMUDA").getID());
        assertEquals("Asia/Thimphu", getTimeZoneUtil().getByLocation("BHUTAN").getID());
        assertEquals("America/La_Paz", getTimeZoneUtil().getByLocation("BOLIVIA").getID());
        assertEquals("Europe/Belgrade", getTimeZoneUtil().getByLocation("BOSNIA AND HERZEGOVINA").getID());
        assertEquals("Africa/Gaborone", getTimeZoneUtil().getByLocation("BOTSWANA").getID());
        assertEquals("GMT", getTimeZoneUtil().getByLocation("BOUVET ISLAND").getID());
        assertEquals("Indian/Chagos", getTimeZoneUtil().getByLocation("BRITISH INDIAN OCEAN TERRITORY").getID());
        assertEquals("Asia/Brunei", getTimeZoneUtil().getByLocation("BRUNEI").getID());
        assertEquals("Europe/Sofia", getTimeZoneUtil().getByLocation("BULGARIA").getID());
        assertEquals("Africa/Ouagadougou", getTimeZoneUtil().getByLocation("BURKINA FASO").getID());
        assertEquals("Africa/Bujumbura", getTimeZoneUtil().getByLocation("BURUNDI").getID());
        assertEquals("Asia/Phnom_Penh", getTimeZoneUtil().getByLocation("CAMBODIA").getID());
        assertEquals("Africa/Douala", getTimeZoneUtil().getByLocation("CAMEROON").getID());
        assertEquals("Atlantic/Cape_Verde", getTimeZoneUtil().getByLocation("CAPE VERDE").getID());
        assertEquals("America/Cayman", getTimeZoneUtil().getByLocation("CAYMAN ISLANDS").getID());
        assertEquals("Africa/Bangui", getTimeZoneUtil().getByLocation("CENTRAL AFRICAN REPUBLIC").getID());
        assertEquals("Africa/Ndjamena", getTimeZoneUtil().getByLocation("CHAD").getID());
        assertEquals("America/Santiago", getTimeZoneUtil().getByLocation("CHILE").getID());
        assertEquals("Asia/Shanghai", getTimeZoneUtil().getByLocation("CHINA").getID());
        assertEquals("Indian/Christmas", getTimeZoneUtil().getByLocation("CHRISTMAS ISLAND").getID());
        assertEquals("SystemV/PST8", getTimeZoneUtil().getByLocation("CLIPPERTON ISLAND").getID());
        assertEquals("Indian/Cocos", getTimeZoneUtil().getByLocation("COCOS ISLANDS").getID());
        assertEquals("Indian/Cocos", getTimeZoneUtil().getByLocation("KEELING ISLANDS").getID());
        assertEquals("America/Bogota", getTimeZoneUtil().getByLocation("COLOMBIA").getID());
        assertEquals("Indian/Comoro", getTimeZoneUtil().getByLocation("COMOROS").getID());
        assertEquals("Africa/Brazzaville", getTimeZoneUtil().getByLocation("CONGO").getID());
        assertEquals("Africa/Kinshasa", getTimeZoneUtil().getByLocation("DEMOCRATIC REPUBLIC OF THE CONGO").getID());
        assertEquals("Pacific/Rarotonga", getTimeZoneUtil().getByLocation("COOK ISLANDS").getID());
        assertEquals("Australia/Brisbane", getTimeZoneUtil().getByLocation("CORAL SEA ISLANDS").getID());
        assertEquals("America/Costa_Rica", getTimeZoneUtil().getByLocation("COSTA RICA").getID());
        assertEquals("Africa/Abidjan", getTimeZoneUtil().getByLocation("COTE DIVOIRE").getID());
        assertEquals("Europe/Zagreb", getTimeZoneUtil().getByLocation("CROATIA").getID());
        assertEquals("Cuba", getTimeZoneUtil().getByLocation("CUBA").getID());
        assertEquals("Europe/Nicosia", getTimeZoneUtil().getByLocation("CYPRUS").getID());
        assertEquals("Europe/Prague", getTimeZoneUtil().getByLocation("CZECH REPUBLIC").getID());
        assertEquals("Europe/Copenhagen", getTimeZoneUtil().getByLocation("DENMARK").getID());
        assertEquals("Africa/Djibouti", getTimeZoneUtil().getByLocation("DJIBOUTI").getID());
        assertEquals("America/Dominica", getTimeZoneUtil().getByLocation("DOMINICA").getID());
        assertEquals("America/Santo_Domingo", getTimeZoneUtil().getByLocation("DOMINICAN REPUBLIC").getID());
        assertEquals("Asia/Dili", getTimeZoneUtil().getByLocation("EAST TIMOR").getID());
        assertEquals("Egypt", getTimeZoneUtil().getByLocation("EGYPT").getID());
        assertEquals("America/El_Salvador", getTimeZoneUtil().getByLocation("EL SALVADOR").getID());
        assertEquals("Africa/Malabo", getTimeZoneUtil().getByLocation("EQUATORIAL GUINEA").getID());
        assertEquals("EAT", getTimeZoneUtil().getByLocation("ERITREA").getID());
        assertEquals("Europe/Tallinn", getTimeZoneUtil().getByLocation("ESTONIA").getID());
        assertEquals("EAT", getTimeZoneUtil().getByLocation("ETHIOPIA").getID());
        assertEquals("EAT", getTimeZoneUtil().getByLocation("EUROPA ISLAND").getID());
        assertEquals("Atlantic/Stanley", getTimeZoneUtil().getByLocation("FALKLAND ISLANDS").getID());
        assertEquals("Atlantic/Stanley", getTimeZoneUtil().getByLocation("MALVINAS").getID());
        assertEquals("Atlantic/Faroe", getTimeZoneUtil().getByLocation("FAROE ISLANDS").getID());
        assertEquals("Pacific/Fiji", getTimeZoneUtil().getByLocation("FIJI").getID());
        assertEquals("Europe/Helsinki", getTimeZoneUtil().getByLocation("FINLAND").getID());
        assertEquals("Europe/Paris", getTimeZoneUtil().getByLocation("FRANCE").getID());
        assertEquals("America/Cayenne", getTimeZoneUtil().getByLocation("FRENCH GUIANA").getID());
        assertEquals("Indian/Kerguelen", getTimeZoneUtil().getByLocation("FRENCH SOUTHERN TERRITORIES").getID());
        assertEquals("Africa/Libreville", getTimeZoneUtil().getByLocation("GABON").getID());
        assertEquals("Africa/Banjul", getTimeZoneUtil().getByLocation("GAMBIA").getID());
        assertEquals("EET", getTimeZoneUtil().getByLocation("GAZA STRIP").getID());
        assertEquals("Asia/Tbilisi", getTimeZoneUtil().getByLocation("GEORGIA").getID());
        assertEquals("Europe/Berlin", getTimeZoneUtil().getByLocation("GERMANY").getID());
        assertEquals("Africa/Accra", getTimeZoneUtil().getByLocation("GHANA").getID());
        assertEquals("Europe/Gibraltar", getTimeZoneUtil().getByLocation("GIBRALTAR").getID());
        assertEquals("Etc/GMT-4", getTimeZoneUtil().getByLocation("GLORIOSO ISLANDS").getID());
        assertEquals("Europe/Athens", getTimeZoneUtil().getByLocation("GREECE").getID());
        assertEquals("America/Grenada", getTimeZoneUtil().getByLocation("GRENADA").getID());
        assertEquals("America/Guadeloupe", getTimeZoneUtil().getByLocation("GUADELOUPE").getID());
        assertEquals("Pacific/Guam", getTimeZoneUtil().getByLocation("GUAM").getID());
        assertEquals("America/Guatemala", getTimeZoneUtil().getByLocation("GUATEMALA").getID());
        assertEquals("Europe/Guernsey", getTimeZoneUtil().getByLocation("GUERNSEY").getID());
        assertEquals("Africa/Conakry", getTimeZoneUtil().getByLocation("GUINEA").getID());
        assertEquals("Africa/Bissau", getTimeZoneUtil().getByLocation("GUINEA-BISSAU").getID());
        assertEquals("America/Guyana", getTimeZoneUtil().getByLocation("GUYANA").getID());
        assertEquals("America/Port-au-Prince", getTimeZoneUtil().getByLocation("HAITI").getID());
        assertEquals("Antarctica/Mawson", getTimeZoneUtil().getByLocation("HEARD AND MC DONALD ISLANDS").getID());
        assertEquals("Europe/Vatican", getTimeZoneUtil().getByLocation("HOLY SEE").getID());
        assertEquals("Europe/Vatican", getTimeZoneUtil().getByLocation("VATICAN CITY").getID());
        assertEquals("America/Tegucigalpa", getTimeZoneUtil().getByLocation("HONDURAS").getID());
        assertEquals("Asia/Hong_Kong", getTimeZoneUtil().getByLocation("HONG KONG").getID());
        assertEquals("Etc/GMT+12", getTimeZoneUtil().getByLocation("HOWLAND ISLAND").getID());
        assertEquals("Europe/Budapest", getTimeZoneUtil().getByLocation("HUNGARY").getID());
        assertEquals("Atlantic/Reykjavik", getTimeZoneUtil().getByLocation("ICELAND").getID());
        assertEquals("IST", getTimeZoneUtil().getByLocation("INDIA").getID());
        assertEquals("Asia/Tehran", getTimeZoneUtil().getByLocation("IRAN").getID());
        assertEquals("Asia/Baghdad", getTimeZoneUtil().getByLocation("IRAQ").getID());
        assertEquals("Europe/Dublin", getTimeZoneUtil().getByLocation("IRELAND").getID());
        assertEquals("Asia/Tel_Aviv", getTimeZoneUtil().getByLocation("ISRAEL").getID());
        assertEquals("Europe/Rome", getTimeZoneUtil().getByLocation("ITALY").getID());
        assertEquals("America/Jamaica", getTimeZoneUtil().getByLocation("JAMAICA").getID());
        assertEquals("CET", getTimeZoneUtil().getByLocation("JAN MAYEN").getID());
        assertEquals("Asia/Tokyo", getTimeZoneUtil().getByLocation("JAPAN").getID());
        assertEquals("Etc/GMT+11", getTimeZoneUtil().getByLocation("JARVIS ISLAND").getID());
        assertEquals("Europe/Jersey", getTimeZoneUtil().getByLocation("JERSEY").getID());
        assertEquals("Pacific/Johnston", getTimeZoneUtil().getByLocation("JOHNSTON ATOLL").getID());
        assertEquals("Asia/Amman", getTimeZoneUtil().getByLocation("JORDAN").getID());
        assertEquals("EAT", getTimeZoneUtil().getByLocation("JUAN DE NOVA ISLAND").getID());
        assertEquals("Africa/Nairobi", getTimeZoneUtil().getByLocation("KENYA").getID());
        assertEquals("Etc/GMT+11", getTimeZoneUtil().getByLocation("KINGMAN REEF").getID());
        assertEquals("Asia/Kuwait", getTimeZoneUtil().getByLocation("KUWAIT").getID());
        assertEquals("Asia/Bishkek", getTimeZoneUtil().getByLocation("KYRGYZSTAN").getID());
        assertEquals("Asia/Vientiane", getTimeZoneUtil().getByLocation("LAOS").getID());
        assertEquals("EET", getTimeZoneUtil().getByLocation("LATVIA").getID());
        assertEquals("Asia/Beirut", getTimeZoneUtil().getByLocation("LEBANON").getID());
        assertEquals("Africa/Maseru", getTimeZoneUtil().getByLocation("LESOTHO").getID());
        assertEquals("Africa/Monrovia", getTimeZoneUtil().getByLocation("LIBERIA").getID());
        assertEquals("Africa/Tripoli", getTimeZoneUtil().getByLocation("LIBYA").getID());
        assertEquals("Europe/Vaduz", getTimeZoneUtil().getByLocation("LIECHTENSTEIN").getID());
        assertEquals("Europe/Vilnius", getTimeZoneUtil().getByLocation("LITHUANIA").getID());
        assertEquals("Europe/Luxembourg", getTimeZoneUtil().getByLocation("LUXEMBOURG").getID());
        assertEquals("Asia/Macau", getTimeZoneUtil().getByLocation("MACAU").getID());
        assertEquals("Europe/Skopje", getTimeZoneUtil().getByLocation("MACEDONIA").getID());
        assertEquals("EAT", getTimeZoneUtil().getByLocation("MADAGASCAR").getID());
        assertEquals("CAT", getTimeZoneUtil().getByLocation("MALAWI").getID());
        assertEquals("Asia/Kuala_Lumpur", getTimeZoneUtil().getByLocation("MALAYSIA").getID());
        assertEquals("Indian/Maldives", getTimeZoneUtil().getByLocation("MALDIVES").getID());
        assertEquals("Africa/Bamako", getTimeZoneUtil().getByLocation("MALI").getID());
        assertEquals("Europe/Malta", getTimeZoneUtil().getByLocation("MALTA").getID());
        assertEquals("Europe/Isle_of_Man", getTimeZoneUtil().getByLocation("ISLE OF MAN").getID());
        assertEquals("Pacific/Majuro", getTimeZoneUtil().getByLocation("MARSHALL ISLANDS").getID());
        assertEquals("America/Martinique", getTimeZoneUtil().getByLocation("MARTINIQUE").getID());
        assertEquals("Africa/Nouakchott", getTimeZoneUtil().getByLocation("MAURITANIA").getID());
        assertEquals("Indian/Mauritius", getTimeZoneUtil().getByLocation("MAURITIUS").getID());
        assertEquals("Indian/Mayotte", getTimeZoneUtil().getByLocation("MAYOTTE").getID());
        assertEquals("Pacific/Midway", getTimeZoneUtil().getByLocation("MIDWAY ISLANDS").getID());
        assertEquals("Europe/Chisinau", getTimeZoneUtil().getByLocation("REPUBLIC OF MOLDOVA").getID());
        assertEquals("Europe/Monaco", getTimeZoneUtil().getByLocation("MONACO").getID());
        assertEquals("America/Montserrat", getTimeZoneUtil().getByLocation("MONTSERRAT").getID());
        assertEquals("Africa/Casablanca", getTimeZoneUtil().getByLocation("MOROCCO").getID());
        assertEquals("Africa/Maputo", getTimeZoneUtil().getByLocation("MOZAMBIQUE").getID());
        assertEquals("Asia/Rangoon", getTimeZoneUtil().getByLocation("MYANMAR").getID());
        assertEquals("Africa/Windhoek", getTimeZoneUtil().getByLocation("NAMIBIA").getID());
        assertEquals("Pacific/Nauru", getTimeZoneUtil().getByLocation("NAURU").getID());
        assertEquals("SystemV/EST5", getTimeZoneUtil().getByLocation("NAVASSA ISLAND").getID());
        assertEquals("Asia/Katmandu", getTimeZoneUtil().getByLocation("NEPAL").getID());
        assertEquals("Europe/Amsterdam", getTimeZoneUtil().getByLocation("THE NETHERLANDS").getID());
        assertEquals("Europe/Amsterdam", getTimeZoneUtil().getByLocation("NETHERLANDS").getID());
        assertEquals("SystemV/AST4", getTimeZoneUtil().getByLocation("NETHERLANDS ANTILLES").getID());
        assertEquals("Pacific/Noumea", getTimeZoneUtil().getByLocation("NEW CALEDONIA").getID());
        assertEquals("America/Managua", getTimeZoneUtil().getByLocation("NICARAGUA").getID());
        assertEquals("Africa/Niamey", getTimeZoneUtil().getByLocation("NIGER").getID());
        assertEquals("Africa/Lagos", getTimeZoneUtil().getByLocation("NIGERIA").getID());
        assertEquals("Pacific/Niue", getTimeZoneUtil().getByLocation("NIUE").getID());
        assertEquals("Pacific/Norfolk", getTimeZoneUtil().getByLocation("NORFOLK ISLAND").getID());
        assertEquals("Asia/Pyongyang", getTimeZoneUtil().getByLocation("NORTH KOREA").getID());
        assertEquals("Pacific/Saipan", getTimeZoneUtil().getByLocation("NORTHERN MARIANA ISLANDS").getID());
        assertEquals("Europe/Oslo", getTimeZoneUtil().getByLocation("NORWAY").getID());
        assertEquals("Asia/Muscat", getTimeZoneUtil().getByLocation("OMAN").getID());
        assertEquals("Asia/Karachi", getTimeZoneUtil().getByLocation("PAKISTAN").getID());
        assertEquals("Pacific/Palau", getTimeZoneUtil().getByLocation("PALAU").getID());
        assertEquals("US/Samoa", getTimeZoneUtil().getByLocation("PALMYRA ATOLL").getID());
        assertEquals("America/Panama", getTimeZoneUtil().getByLocation("PANAMA").getID());
        assertEquals("Pacific/Port_Moresby", getTimeZoneUtil().getByLocation("PAPUA NEW GUINEA").getID());
        assertEquals("Etc/GMT-8", getTimeZoneUtil().getByLocation("PARACEL ISLANDS").getID());
        assertEquals("America/Asuncion", getTimeZoneUtil().getByLocation("PARAGUAY").getID());
        assertEquals("America/Lima", getTimeZoneUtil().getByLocation("PERU").getID());
        assertEquals("Asia/Manila", getTimeZoneUtil().getByLocation("PHILIPPINES").getID());
        assertEquals("Pacific/Pitcairn", getTimeZoneUtil().getByLocation("PITCAIRN").getID());
        assertEquals("Europe/Warsaw", getTimeZoneUtil().getByLocation("POLAND").getID());
        assertEquals("Europe/Lisbon", getTimeZoneUtil().getByLocation("PORTUGAL").getID());
        assertEquals("America/Puerto_Rico", getTimeZoneUtil().getByLocation("PUERTO RICO").getID());
        assertEquals("Asia/Qatar", getTimeZoneUtil().getByLocation("QATAR").getID());
        assertEquals("Indian/Reunion", getTimeZoneUtil().getByLocation("REUNION").getID());
        assertEquals("Europe/Bucharest", getTimeZoneUtil().getByLocation("ROMANIA").getID());
        assertEquals("Africa/Kigali", getTimeZoneUtil().getByLocation("RWANDA").getID());
        assertEquals("Atlantic/St_Helena", getTimeZoneUtil().getByLocation("SAINT HELENA").getID());
        assertEquals("America/St_Kitts", getTimeZoneUtil().getByLocation("SAINT KITTS AND NEVIS").getID());
        assertEquals("America/St_Lucia", getTimeZoneUtil().getByLocation("SAINT LUCIA").getID());
        assertEquals("BET", getTimeZoneUtil().getByLocation("SAINT PIERRE AND MIQUELON").getID());
        assertEquals("America/St_Vincent", getTimeZoneUtil().getByLocation("SAINT VINCENT AND THE GRENADINES").getID());
        assertEquals("Europe/San_Marino", getTimeZoneUtil().getByLocation("SAN MARINO").getID());
        assertEquals("Africa/Sao_Tome", getTimeZoneUtil().getByLocation("SAO TOME AND PRINCIPE").getID());
        assertEquals("Asia/Riyadh", getTimeZoneUtil().getByLocation("SAUDI ARABIA").getID());
        assertEquals("Africa/Dakar", getTimeZoneUtil().getByLocation("SENEGAL").getID());
        assertEquals("Europe/Belgrade", getTimeZoneUtil().getByLocation("SERBIA AND MONTENEGRO").getID());
        assertEquals("Indian/Mahe", getTimeZoneUtil().getByLocation("SEYCHELLES").getID());
        assertEquals("Africa/Freetown", getTimeZoneUtil().getByLocation("SIERRA LEONE").getID());
        assertEquals("Asia/Singapore", getTimeZoneUtil().getByLocation("SINGAPORE").getID());
        assertEquals("Europe/Bratislava", getTimeZoneUtil().getByLocation("SLOVAKIA").getID());
        assertEquals("Europe/Ljubljana", getTimeZoneUtil().getByLocation("SLOVENIA").getID());
        assertEquals("SST", getTimeZoneUtil().getByLocation("SOLOMON ISLANDS").getID());
        assertEquals("Africa/Mogadishu", getTimeZoneUtil().getByLocation("SOMALIA").getID());
        assertEquals("Africa/Johannesburg", getTimeZoneUtil().getByLocation("SOUTH AFRICA").getID());
        assertEquals("Atlantic/South_Georgia", getTimeZoneUtil().getByLocation("SOUTH GEORGIA").getID());
        assertEquals("Asia/Seoul", getTimeZoneUtil().getByLocation("SOUTH KOREA").getID());
        assertEquals("Europe/Madrid", getTimeZoneUtil().getByLocation("SPAIN").getID());
        assertEquals("Asia/Colombo", getTimeZoneUtil().getByLocation("SRI LANKA").getID());
        assertEquals("Africa/Khartoum", getTimeZoneUtil().getByLocation("SUDAN").getID());
        assertEquals("America/Paramaribo", getTimeZoneUtil().getByLocation("SURINAME").getID());
        assertEquals("Atlantic/Jan_Mayen", getTimeZoneUtil().getByLocation("SVALBARD AND JAN MAYEN ISLANDS").getID());
        assertEquals("Africa/Mbabane", getTimeZoneUtil().getByLocation("SWAZILAND").getID());
        assertEquals("Europe/Stockholm", getTimeZoneUtil().getByLocation("SWEDEN").getID());
        assertEquals("Europe/Zurich", getTimeZoneUtil().getByLocation("SWITZERLAND").getID());
        assertEquals("Asia/Damascus", getTimeZoneUtil().getByLocation("SYRIA").getID());
        assertEquals("Asia/Taipei", getTimeZoneUtil().getByLocation("TAIWAN").getID());
        assertEquals("Asia/Dushanbe", getTimeZoneUtil().getByLocation("TAJIKISTAN").getID());
        assertEquals("Africa/Dar_es_Salaam", getTimeZoneUtil().getByLocation("TANZANIA").getID());
        assertEquals("Asia/Bangkok", getTimeZoneUtil().getByLocation("THAILAND").getID());
        assertEquals("Africa/Lome", getTimeZoneUtil().getByLocation("TOGO").getID());
        assertEquals("Etc/GMT-13", getTimeZoneUtil().getByLocation("TOKELAU").getID());
        assertEquals("Pacific/Tongatapu", getTimeZoneUtil().getByLocation("TONGA").getID());
        assertEquals("America/Port_of_Spain", getTimeZoneUtil().getByLocation("TRINIDAD AND TOBAGO").getID());
        assertEquals("Etc/GMT-4", getTimeZoneUtil().getByLocation("TROMELIN ISLAND").getID());
        assertEquals("Africa/Tunis", getTimeZoneUtil().getByLocation("TUNISIA").getID());
        assertEquals("Asia/Istanbul", getTimeZoneUtil().getByLocation("TURKEY").getID());
        assertEquals("Asia/Ashgabat", getTimeZoneUtil().getByLocation("TURKMENISTAN").getID());
        assertEquals("America/Grand_Turk", getTimeZoneUtil().getByLocation("TURKS AND CAICOS ISLANDS").getID());
        assertEquals("Pacific/Funafuti", getTimeZoneUtil().getByLocation("TUVALU").getID());
        assertEquals("Africa/Kampala", getTimeZoneUtil().getByLocation("UGANDA").getID());
        assertEquals("Europe/Kiev", getTimeZoneUtil().getByLocation("UKRAINE").getID());
        assertEquals("Asia/Dubai", getTimeZoneUtil().getByLocation("UNITED ARAB EMIRATES").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("UNITED KINGDOM").getID());
        assertEquals("Europe/London", getTimeZoneUtil().getByLocation("ENGLAND").getID());
        assertEquals("America/Montevideo", getTimeZoneUtil().getByLocation("URUGUAY").getID());
        assertEquals("Asia/Tashkent", getTimeZoneUtil().getByLocation("UZBEKISTAN").getID());
        assertEquals("SST", getTimeZoneUtil().getByLocation("VANUATU").getID());
        assertEquals("America/Caracas", getTimeZoneUtil().getByLocation("VENEZUELA").getID());
        assertEquals("Asia/Ho_Chi_Minh", getTimeZoneUtil().getByLocation("VIETNAM").getID());
        assertEquals("America/Tortola", getTimeZoneUtil().getByLocation("BRITISH VIRGIN ISLANDS").getID());
        assertEquals("America/St_Thomas", getTimeZoneUtil().getByLocation("U.S. VIRGIN ISLANDS").getID());
        assertEquals("Pacific/Wake", getTimeZoneUtil().getByLocation("WAKE ISLAND").getID());
        assertEquals("Pacific/Wallis", getTimeZoneUtil().getByLocation("WALLIS AND FUTUNA ISLANDS").getID());
        assertEquals("Asia/Gaza", getTimeZoneUtil().getByLocation("WEST BANK").getID());
        assertEquals("Africa/El_Aaiun", getTimeZoneUtil().getByLocation("WESTERN SAHARA").getID());
        assertEquals("Pacific/Apia", getTimeZoneUtil().getByLocation("WESTERN SAMOA").getID());
        assertEquals("Asia/Aden", getTimeZoneUtil().getByLocation("YEMEN").getID());
        assertEquals("Africa/Lusaka", getTimeZoneUtil().getByLocation("ZAMBIA").getID());
        assertEquals("Africa/Harare", getTimeZoneUtil().getByLocation("ZIMBABWE").getID());
    }

    @Test
    public void toStandardizedGmtString() throws TzException {
        assertEquals( "GMT", getTimeZoneUtil().toStandardizedGmtString("GMT-00:00") );
        assertEquals( "GMT", getTimeZoneUtil().toStandardizedGmtString("GMT-00") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("GMT+1") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("GMT+01") );
        assertEquals( "GMT-11:00", getTimeZoneUtil().toStandardizedGmtString("GMT-11") );
        assertEquals( "GMT+08:00", getTimeZoneUtil().toStandardizedGmtString("GMT+800") );
        assertEquals( "GMT-08:30", getTimeZoneUtil().toStandardizedGmtString("GMT-830") );
        assertEquals( "GMT+11:00", getTimeZoneUtil().toStandardizedGmtString("GMT+1100") );
        assertEquals( "GMT-11:30", getTimeZoneUtil().toStandardizedGmtString("GMT-1130") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("GMT+1:00") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("GMT+01:00") );
        assertEquals( "GMT-11:00", getTimeZoneUtil().toStandardizedGmtString("GMT-11:00") );
        assertEquals( "GMT-11:30", getTimeZoneUtil().toStandardizedGmtString("GMT-11:30") );
        assertEquals( "GMT+02:00", getTimeZoneUtil().toStandardizedGmtString("GMT+2:0") );
        assertEquals( "GMT-06:00", getTimeZoneUtil().toStandardizedGmtString("GMT-06:0") );

        assertEquals( "GMT", getTimeZoneUtil().toStandardizedGmtString("") );
        assertEquals( "GMT", getTimeZoneUtil().toStandardizedGmtString("-00:00") );
        assertEquals( "GMT", getTimeZoneUtil().toStandardizedGmtString("-00") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("+1") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("+01") );
        assertEquals( "GMT-11:00", getTimeZoneUtil().toStandardizedGmtString("-11") );
        assertEquals( "GMT+08:00", getTimeZoneUtil().toStandardizedGmtString("+800") );
        assertEquals( "GMT-08:30", getTimeZoneUtil().toStandardizedGmtString("-830") );
        assertEquals( "GMT+11:00", getTimeZoneUtil().toStandardizedGmtString("+1100") );
        assertEquals( "GMT-11:30", getTimeZoneUtil().toStandardizedGmtString("-1130") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("+1:00") );
        assertEquals( "GMT+01:00", getTimeZoneUtil().toStandardizedGmtString("+01:00") );
        assertEquals( "GMT-11:00", getTimeZoneUtil().toStandardizedGmtString("-11:00") );
        assertEquals( "GMT-11:30", getTimeZoneUtil().toStandardizedGmtString("-11:30") );
        assertEquals( "GMT+02:00", getTimeZoneUtil().toStandardizedGmtString("+2:0") );
        assertEquals( "GMT-06:00", getTimeZoneUtil().toStandardizedGmtString("-06:0") );
    }
}
