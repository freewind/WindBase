package jobs;

import commons.Helper;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.templates.GroovyTemplate;
import play.templates.SafeFormatter;
import play.templates.Template;

/**
 * Member: Freewind
 * Date: 13-1-14
 * Time: 上午11:25
 */
@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() throws Exception {
        GroovyTemplate.registerFormatter("json", new SafeJSONFormatter());
    }

    private static class SafeJSONFormatter implements SafeFormatter {
        public String format(Template template, Object value) {
            if (value != null) {
                return Helper.toJson(value);
            }
            return "null";
        }
    }

}
