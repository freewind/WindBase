package commons;

import play.Play;

import java.io.File;

/**
 * Member: Freewind
 * Date: 12-12-22
 * Time: 下午10:30
 */
public class ViewHelper {

    public static File getView(String actionPath) {
        return Play.getFile("app/views/" + actionPath.replace(".", "/") + ".html");
    }

    public static boolean hasView(String actionPath) {
        return getView(actionPath).exists();
    }

}
