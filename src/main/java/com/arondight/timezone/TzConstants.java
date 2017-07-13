package com.arondight.timezone;

public interface TzConstants {
    String SEP_REGEX = "\\Q|\\E";
    String PIPE = "|";
    String YES = "yes";
    String NO = "no";
    String PRIMARY = "primary";
    String ALTERNATIVE = "alternative";
    String FALLBACK = "fallback";
    String NOT = "not";
    String GMT = "GMT";
    char MINUS = '-';
    char PLUS = '+';
    char COLON = ':';
    char ZERO = '0';
    String SPACE = " ";
    String EMTPY = "";
    String COMMA = ",";

    String US = "US";
    String USA = "USA";
    String UNITED_STATES = "UNITED STATES";
    String NY = "NY"; // no state default option US

    String CA = "CA";
    String CANADA = "CANADA";
    String PQ = "PQ"; // no state default option Canada

    String NZ = "NZ";
    String NEW_ZEALAND = "NEW ZEALAND";

    String AU = "AU";
    String AUSTRALIA = "AUSTRALIA";
    String NSW = "NSW";  // no state default option Australia

    String DATA_COMMENT_SLASHES = "//";
    String DATA_COMMENT_POUND = "#";
    String DATA_COMMENT_EXCLAIM = "!";
}
