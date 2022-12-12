package kz.kacd.sso.util;

public class ValueUtils {

    public static String defaultedPeriod(Boolean active, String source, String defaultValue) {
        if (!active) {
            return "-1";
        }

        return duration(defaulted(source, defaultValue)).toString();
    }

    public static <T> T defaulted(T source, T defaultValue) {
        if (source == null) {
            return defaultValue;
        }

        return source;
    }

    public static Long duration(String source) {
        if (source == null) {
            return -1L;
        }

        switch (source.charAt(source.length() - 1)) {
            case 's':
                return toLongExceptLast(source);
            case 'm':
                return toLongExceptLast(source) * 60;
            case 'h':
                return toLongExceptLast(source) * 60 * 60;
            case 'd':
                return toLongExceptLast(source) * 24 * 60 * 60;
            default:
                throw new IllegalArgumentException("Invalid duration type!");
        }
    }

    private static Long toLongExceptLast(String s) {
        return Long.parseLong(s.substring(0, s.length() - 1));
    }
}
