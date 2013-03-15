package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import commons.GridOptionsManager;
import commons.Helper;

import java.io.IOException;

/**
 * Member: Freewind
 * Date: 13-2-4
 * Time: 下午9:12
 */
public class Directives extends BaseAdminController {

    public static void configureGrid(String dataName) {
        request.format = "html";
        render();
    }

    public static void saveGridOptions(String body) throws IOException {
        GridOptionsManager.GridOptions options = Helper.createObjectMapper().readValue(body, new TypeReference<GridOptionsManager.GridOptions>() {
        });
        GridOptionsManager manager = new GridOptionsManager();
        manager.addOrUpdate(options);
        manager.save();
        ok();
    }

    public static void getGridOptions(String id) throws IOException {
        GridOptionsManager manager = new GridOptionsManager();
        GridOptionsManager.GridOptions gridOptions = manager.get(id);
        if (gridOptions == null) {
            renderJSON("{}");
        } else {
            renderJSON(Helper.toJson(gridOptions));
        }
    }


    public static void grid() {
        request.format = "html";
        render();
    }

}
