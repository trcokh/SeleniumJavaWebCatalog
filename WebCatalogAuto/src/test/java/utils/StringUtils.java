package utils;

public class StringUtils {
    public static String normalizeUrl(String url){
        if (url == null){
            return "";
        }
        return url.replaceFirst("^https?://", "");
    }
}
