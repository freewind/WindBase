package commons;

import org.apache.commons.lang.StringUtils;
import replay_utils.ModuleCtrlAction;
import replay_utils.ModuleCtrlActionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsRoutesGenerator {

    public static Map<String, Map<String, List<ModuleCtrlAction>>> get() throws IOException {
        return groupActions(ModuleCtrlActionUtils.get(true));
    }

    private static Map<String, Map<String, List<ModuleCtrlAction>>> groupActions(List<ModuleCtrlAction> list) {
        Map<String, Map<String, List<ModuleCtrlAction>>> map = new LinkedHashMap<String, Map<String, List<ModuleCtrlAction>>>();
        for (ModuleCtrlAction route : list) {
            String module = StringUtils.trimToEmpty(route.module);

            Map<String, List<ModuleCtrlAction>> moduleMap = map.get(module);
            if (moduleMap == null) {
                moduleMap = new LinkedHashMap<String, List<ModuleCtrlAction>>();
                map.put(module, moduleMap);
            }

            String controller = route.controller;
            List<ModuleCtrlAction> values = moduleMap.get(controller);
            if (values == null) {
                values = new ArrayList<ModuleCtrlAction>();
                moduleMap.put(controller, values);
            }
            values.add(route);
        }
        return map;
    }

}


