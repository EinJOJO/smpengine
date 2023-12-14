package it.einjojo.smpengine.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class SQLUtil {

    private final static Pattern sqlComments = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private final static Pattern sqlNewlines = Pattern.compile("\n", Pattern.DOTALL);
    private final static Pattern sqlWhitespace = Pattern.compile("\\s+", Pattern.DOTALL);

    public static String sanitize(String sql) {
        sql = sqlComments.matcher(sql).replaceAll("");
        sql = sqlNewlines.matcher(sql).replaceAll(" ");
        sql = sqlWhitespace.matcher(sql).replaceAll(" ");
        return sql;
    }

}

