package controllers;

import models.HttpRequestSummary;
import org.apache.commons.io.FileUtils;
import play.Play;
import play.templates.BaseTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Application extends BaseAdminController {

    public static void index() {
        request.format = "html";
        List<String> list = new ArrayList<String>();
        list.add("Jeff");
        list.add("lily");
        render(list);
    }

    public static void showViewSource(String path) throws IOException {
        File file = Play.getFile("app/views/" + path.replace(".", "/") + ".html");
        if (file.exists()) {
            renderText(FileUtils.readFileToString(file, "UTF-8"));
        } else {
            notFound("View: " + path);
        }
    }

    public static void toAction(String path) {
        redirect(path);
    }

    public static BaseTemplate.RawData test() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("aaa", "111");
        map.put("bbb", "222");
        return renderRaw("Application/test.json", "json", map);
    }

    public static void test2() {
        request.format = "html";
        render();
    }

    public static void fix() {
        for (int i = 0; i < 30; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now + new Random().nextInt(300000));
            HttpRequestSummary s = HttpRequestSummary.findByUnitAndStart(HttpRequestSummary.TimeUnit.minute, date);
            if (s != null) continue;
            s = new HttpRequestSummary(HttpRequestSummary.TimeUnit.minute);
            s.timeStart = date;
            s.totalCount = new Random().nextInt(1000);
            s.save();
        }

        for (int i = 0; i < 30; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now + new Random().nextInt(3000000));
            HttpRequestSummary s = HttpRequestSummary.findByUnitAndStart(HttpRequestSummary.TimeUnit.hour, date);
            if (s != null) continue;
            s = new HttpRequestSummary(HttpRequestSummary.TimeUnit.hour);
            s.timeStart = date;
            s.totalCount = new Random().nextInt(1000);
            s.save();
        }

        for (int i = 0; i < 30; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now + new Random().nextInt(30000000));
            HttpRequestSummary s = HttpRequestSummary.findByUnitAndStart(HttpRequestSummary.TimeUnit.day, date);
            if (s != null) continue;
            s = new HttpRequestSummary(HttpRequestSummary.TimeUnit.day);
            s.timeStart = date;
            s.totalCount = new Random().nextInt(1000);
            s.save();
        }

        for (int i = 0; i < 30; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now + new Random().nextInt(300000000));
            HttpRequestSummary s = HttpRequestSummary.findByUnitAndStart(HttpRequestSummary.TimeUnit.week, date);
            if (s != null) continue;
            s = new HttpRequestSummary(HttpRequestSummary.TimeUnit.week);
            s.timeStart = date;
            s.totalCount = new Random().nextInt(1000);
            s.save();
        }

        for (int i = 0; i < 30; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now + new Random().nextInt(300000000));
            HttpRequestSummary s = HttpRequestSummary.findByUnitAndStart(HttpRequestSummary.TimeUnit.month, date);
            if (s != null) continue;
            s = new HttpRequestSummary(HttpRequestSummary.TimeUnit.month);
            s.timeStart = date;
            s.totalCount = new Random().nextInt(1000);
            s.save();
        }

        for (int i = 0; i < 30; i++) {
            long now = System.currentTimeMillis();
            Date date = new Date(now + new Random().nextInt(300000000));
            HttpRequestSummary s = HttpRequestSummary.findByUnitAndStart(HttpRequestSummary.TimeUnit.year, date);
            if (s != null) continue;
            s = new HttpRequestSummary(HttpRequestSummary.TimeUnit.year);
            s.timeStart = date;
            s.totalCount = new Random().nextInt(1000);
            s.save();
        }
        ok();
    }


}