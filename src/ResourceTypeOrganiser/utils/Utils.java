package ResourceTypeOrganiser.utils;

/**
 * Created by zone2 on 1/5/17.
 */
public class Utils {

    public static String standardizeId(String id) {
        return id.replace("@+id/", "").replace("@id/", "");
    }
}
