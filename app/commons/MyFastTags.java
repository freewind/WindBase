package commons;

import groovy.lang.Closure;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import play.templates.FastTags;
import play.templates.GroovyTemplate;
import play.templates.JavaExtensions;

import java.io.PrintWriter;
import java.util.Map;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Member: Freewind
 * Date: 12-12-21
 * Time: 上午11:16
 */
public class MyFastTags extends FastTags {

    public static void _escape(Map<?, ?> args, Closure body, PrintWriter out, GroovyTemplate.ExecutableTemplate template, int fromLine) {
        String content = JavaExtensions.toString(body);
        String type = String.valueOf(args.get("arg"));
        if (isBlank(type) || type.equals("null") || type.trim().equalsIgnoreCase("html")) {
            out.print(StringEscapeUtils.escapeHtml(content));
        } else {
            out.print(content);
        }
    }

    public static void _debug(Map<?, ?> args, Closure body, PrintWriter out, GroovyTemplate.ExecutableTemplate template, int fromLine) {
        String filePath = template.template.getName();
        // e.g. {module:xxx}/app/views/...
        if (filePath.contains("}")) {
            filePath = StringUtils.substringAfterLast(filePath, "}");
        }

        // e.g. {module:wind_articles}/app/views/wind_articles/Categories/index.html
        String type = String.valueOf(args.get("arg")).trim();
        if (isBlank(type) || type.equals("null") || type.equalsIgnoreCase("html")) {
            String content = "<span class='debug debug-hide'>\n"
                             + "<button class='btn btn-warning' data-file='"
                             + filePath
                             + "' data-line='"
                             + fromLine
                             + "'"
                             + " onclick=\"$.get('http://localhost:8091/?message=" + filePath + ":" + fromLine + "')\">edit</button>\n"
                             + "</span>";
            out.print(content);
        } else if (type.equals("js")) {
            String content = ("if($scope.$$debug_mode$$) {\n"
                              + "    jQuery.get(\"http://localhost:8091/?message=${message}\");\n"
                              + "}").replace("${message}", filePath + ":" + fromLine);
            out.print(content);
        }
    }

    public static void _mark(Map<?, ?> args, Closure body, PrintWriter out, GroovyTemplate.ExecutableTemplate template, int fromLine) {
        // do nothing, just for idea outline
    }

}
