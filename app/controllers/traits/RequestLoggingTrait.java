package controllers.traits;

import models.HttpRequestLog;
import org.apache.commons.lang.StringUtils;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

import java.util.Map;

/**
 * Member: Freewind
 * Date: 12-12-18
 * Time: 下午12:34
 */
public abstract class RequestLoggingTrait extends Controller {

    @Before(priority = 2)
    static void logRequestParams() throws Exception {
        HttpRequestLog log = new HttpRequestLog();
        log.url = request.url;
        log.queryString = request.querystring;
        log.headers = getRawHeaders();
        log.method = request.method.toLowerCase();
        log.resourceType = getSuffix(request.url);
        log.remoteIp = request.remoteAddress;
        log.params = getRawParams();
        log.referer = getReferer();
        log.ajax = request.isAjax();
        log.save();
    }

    private static String getSuffix(String url) {
        if (url.contains("?")) {
            url = StringUtils.substringBefore(url, "?");
        }
        System.out.println("### url : " + url);
        String[] items = url.split("[/]");
        if (items.length == 0) return null;

        String last = items[items.length - 1];
        int dotIndex = last.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        } else {
            return last.substring(dotIndex + 1).toLowerCase();
        }
    }

    private static String getRawParams() {
        StringBuilder sb = new StringBuilder();
        Map<String, String[]> all = request.params.all();
        for (String key : all.keySet()) {
            String[] values = all.get(key);
            sb.append(key).append("=");
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    sb.append(value);
                    if (i < values.length - 1) {
                        sb.append(",");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String getRawHeaders() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Http.Header> entry : request.headers.entrySet()) {
            String name = entry.getKey();
            Http.Header header = entry.getValue();
            sb.append(name).append("=").append(header.values);
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String getReferer() {
        Http.Header header = request.headers.get("referer");
        return header == null ? null : header.value();
    }

}
