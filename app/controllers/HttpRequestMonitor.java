package controllers;

import commons.Helper;
import models.HttpRequestLog;
import play.modules.ebean.Pager;

/**
 * Member: Freewind
 * Date: 12-12-18
 * Time: 下午12:11
 */
public class HttpRequestMonitor extends BaseAdminController {

    public static void index() {
        request.format = "html";
        render();
    }

    public static void list() {
        Pager<HttpRequestLog> rows = new Pager<HttpRequestLog>(HttpRequestLog.find.order("createdAt desc"), getPage(), getPageSize());
        renderJSON(Helper.toJson(rows));
    }

}
