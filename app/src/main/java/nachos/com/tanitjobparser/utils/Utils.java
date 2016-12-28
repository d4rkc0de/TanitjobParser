package nachos.com.tanitjobparser.utils;

/**
 * Created by lichiheb on 28/12/16.
 */

public class Utils {
    public static String encodeUrl(String url) {
        return url.replaceAll(" ","%20");
    }

}
