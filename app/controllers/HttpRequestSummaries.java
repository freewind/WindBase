package controllers;

import models.HttpRequestSummary;
import play.modules.ebean.Pager;

import java.util.Collections;
import java.util.List;

/**
 * Member: Freewind
 * Date: 12-12-18
 * Time: 下午1:52
 */
public class HttpRequestSummaries extends BaseAdminController {

    public static void index() {
        request.format = "html";
        Pager<HttpRequestSummary> summaries = new Pager<HttpRequestSummary>(HttpRequestSummary.find, getPage(), getPageSize());
        render(summaries);
    }

    public static void list() {
        request.format = "json";
        Pager<HttpRequestSummary> summaries = new Pager<HttpRequestSummary>(HttpRequestSummary.find.order("timeStart desc"), getPage(), getPageSize());
        render(summaries);
    }

    public static void getChartData(String timeUnit) {
        request.format = "json";
        HttpRequestSummary.TimeUnit unit = HttpRequestSummary.TimeUnit.valueOf(timeUnit);
        List<HttpRequestSummary> rows = new Pager<HttpRequestSummary>(HttpRequestSummary.find.where().eq("timeUnit", timeUnit).order("timeStart desc"), getPage(), getPageSize()).getList();
        Collections.reverse(rows);
        renderTemplate("@chart", timeUnit, rows);
    }

}
