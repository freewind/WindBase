package controllers.plugin_manager;

import play.Play;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Member: Freewind
 * Date: 13-1-16
 * Time: 10:18 PM
 */
public class Module extends Controller {

    public static void index() {
        List<String> modules = new ArrayList<String>(Play.modules.keySet());
        render(modules);
    }

}
