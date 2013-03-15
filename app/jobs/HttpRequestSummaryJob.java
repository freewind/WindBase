package jobs;

import commons.App;
import models.HttpRequestLog;
import models.HttpRequestSummary;
import models.InternalCodeValue;
import org.joda.time.DateTime;
import play.jobs.Every;
import play.jobs.Job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static commons.Helper.eq;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 12-12-18
 * Time: 下午2:16
 */
@Every("10s")
public class HttpRequestSummaryJob extends Job {

    @Override
    public void doJob() throws Exception {
        InternalCodeValue prop = InternalCodeValue.findProceedRequestLogId();

        Long lastId = Long.parseLong(prop.value);

        // 每次统计前一分钟的，和当前一分钟的
        List<HttpRequestLog> list = HttpRequestLog.findFromId(lastId);

        List<Map<DateTime, HttpRequestSummary>> maps = new ArrayList();
        for (int i = 0; i < 6; i++) {
            maps.add(new HashMap<DateTime, HttpRequestSummary>());
        }


        for (HttpRequestLog log : list) {
            calc(getOrCreateSummary(HttpRequestSummary.TimeUnit.minute, log.createdAt, maps.get(0)), log);
            calc(getOrCreateSummary(HttpRequestSummary.TimeUnit.hour, log.createdAt, maps.get(1)), log);
            calc(getOrCreateSummary(HttpRequestSummary.TimeUnit.day, log.createdAt, maps.get(2)), log);
            calc(getOrCreateSummary(HttpRequestSummary.TimeUnit.week, log.createdAt, maps.get(3)), log);
            calc(getOrCreateSummary(HttpRequestSummary.TimeUnit.month, log.createdAt, maps.get(4)), log);
            calc(getOrCreateSummary(HttpRequestSummary.TimeUnit.year, log.createdAt, maps.get(5)), log);
        }

        for (Map map : maps) {
            saveMap(map);
        }
    }

    private void saveMap(Map<DateTime, HttpRequestSummary> map) {
        for (HttpRequestSummary summary : map.values()) {
            summary.save();
        }
    }

    private HttpRequestSummary getOrCreateSummary(HttpRequestSummary.TimeUnit unit, Date createdAt, Map<DateTime, HttpRequestSummary> map) {
        DateTime timeStart = HttpRequestSummary.trimDateTime(unit, createdAt);
        HttpRequestSummary summary = map.get(timeStart);
        if (summary == null) {
            summary = HttpRequestSummary.findByUnitAndStart(unit, timeStart.toDate());
            if (summary != null) map.put(timeStart, summary);
        }
        if (summary == null) {
            summary = new HttpRequestSummary(unit);
            summary.timeStart = timeStart.toDate();
            map.put(timeStart, summary);
        }
        return summary;
    }

    private int getIndex(HttpRequestSummary.TimeUnit unit, DateTime time) {
        switch (unit) {
            case minute:
                return time.getMinuteOfHour();
            case hour:
                return time.getHourOfDay();
            case day:
                return time.getDayOfMonth();
            case week:
                return time.getWeekOfWeekyear();
            case month:
                return time.getMonthOfYear();
            case year:
                return time.getYear();
            default:
                return -1;
        }
    }

    private void calc(HttpRequestSummary summary, HttpRequestLog log) {
        summary.totalCount++;
        if (log.ajax) summary.ajaxCount++;
        if (eq(log.method, "get")) summary.getCount++;
        else if (eq(log.method, "post")) summary.postCount++;
        if (isBlank(log.resourceType)) summary.pageCount++;
        else if (eq(log.resourceType, "js")) summary.jsCount++;
        else if (eq(log.resourceType, "css")) summary.cssCount++;
        else if (eq(log.resourceType, "png")
                 || eq(log.resourceType, "gif")
                 || eq(log.resourceType, "jpg")
                 || eq(log.resourceType, "jpeg")
                 || eq(log.resourceType, "bmp")) summary.imageCount++;
        else summary.otherResourceCount++;
    }

    public static void main(String[] args) {
        DateTime now = DateTime.now();
        DateTime preMinute = now.minusMinutes(1).minusSeconds(now.secondOfMinute().get());
        System.out.println("### now: " + now);
        System.out.println("### preMinute: " + preMinute);
        System.out.println("### id: " + App.newId().length());
    }

}
