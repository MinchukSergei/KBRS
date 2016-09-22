package util;

import java.util.ResourceBundle;

/**
 * Created by USER on 21.09.2016.
 */
public class ResourceBundleManager {
    private static String resourceName = "server_settings";
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceName);

    public static String getByName(String name) {
        return resourceBundle.getString(name);
    }
}
