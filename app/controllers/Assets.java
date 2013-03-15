package controllers;

import commons.JsRoutesGenerator;
import replay_utils.ModuleCtrlAction;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Member: Freewind
 * Date: 12-12-15
 * Time: 下午9:47
 */
public class Assets extends BaseController {

    public static void jsRoutes() throws IOException {
        response.setContentTypeIfNotSet("application/javascript");
        String prefix = "/api";
        Map<String, Map<String, List<ModuleCtrlAction>>> actions = JsRoutesGenerator.get();
        renderTemplate("Assets/jsRoutes.angularjs.js", prefix, actions);
    }

}
