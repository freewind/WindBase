package models;

import com.avaje.ebean.annotation.EnumValue;
import commons.Helper;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Member: Freewind
 * Date: 12-12-18
 * Time: 下午2:11
 */
@Entity
@Table(name = "http_request_summaries")
public class HttpRequestSummary extends UuidIdModel {

    public static enum TimeUnit {
        @EnumValue("minute")minute,
        @EnumValue("hour")hour,
        @EnumValue("day")day,
        @EnumValue("week")week,
        @EnumValue("month")month,
        @EnumValue("year")year
    }

    @Column(columnDefinition = "TEXT")
    public TimeUnit timeUnit;

    /**
     * 分钟： 0 ~ 59
     * 小时: 0 ~ 23
     * 天： 1 ~31
     * 星期: 1 ~ 52
     * 月： 1 ~ 12
     * 年： 年份
     */
    public int index;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date timeStart;

    public int totalCount;

    // by method
    public int getCount;
    public int postCount;

    // by resource type
    public int pageCount;
    public int jsCount;
    public int cssCount;
    public int imageCount;
    public int otherResourceCount;

    // ajax
    public int ajaxCount;

    public static final Finder<String, HttpRequestSummary> find = new Finder<String, HttpRequestSummary>(String.class, HttpRequestSummary.class);

    public HttpRequestSummary() {
    }

    public HttpRequestSummary(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setTimeStart(Date date) {
        this.timeStart = trimDateTime(timeUnit, date).toDate();
        this.index = getIndex(timeUnit, timeStart);
    }

    public static HttpRequestSummary findByUnitAndStart(TimeUnit unit, Date timeStart) {
        return HttpRequestSummary.find.where().eq("timeUnit", unit).eq("timeStart", trimDateTime(unit, timeStart)).findUnique();
    }

    private static int getIndex(TimeUnit unit, Date date) {
        DateTime time = new DateTime(date);
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

    public static DateTime trimDateTime(TimeUnit unit, Date date) {
        Helper.notNull(unit, "unit");
        Helper.notNull(date, "date");

        DateTime dateTime = new DateTime(date);
        DateTime minute = dateTime.withMillisOfSecond(0).withSecondOfMinute(0);
        if (unit == TimeUnit.minute) return minute;
        DateTime hour = minute.withMinuteOfHour(0);
        if (unit == TimeUnit.hour) return hour;
        DateTime day = hour.withHourOfDay(0);
        if (unit == TimeUnit.day) return day;
        DateTime week = day.withDayOfWeek(1);
        if (unit == TimeUnit.week) return week;
        DateTime month = week.withDayOfMonth(1);
        if (unit == TimeUnit.month) return month;
        DateTime year = month.withMonthOfYear(1);
        if (unit == TimeUnit.year) return year;

        throw new IllegalArgumentException("Unhandled TimeUnit: " + unit);
    }

}
