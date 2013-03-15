package controllers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.classloading.enhancers.LVEnhancer;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Scope;
import play.templates.BaseTemplate;
import play.templates.Template;
import play.templates.TemplateLoader;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;

/**
 * Member: Freewind
 * Date: 12-12-16
 * Time: 下午12:08
 */
public class ExtendingController extends Controller {

    public static BaseTemplate.RawData renderRaw(String filePath, String safeFormatter, Object... args) throws IOException {
        StringWriter strWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(strWriter);

        Stack<LVEnhancer.MethodExecution> calls = LVEnhancer.LVEnhancerRuntime.getCurrentMethodParams();
        for (int i = 0; i < calls.size(); i++) {
            LVEnhancer.MethodExecution current = calls.get(i);
            LVEnhancer.MethodExecution nested = current.getCurrentNestedMethodCall();
            if (current != null) {
                System.out.println("### (" + i + ") current " + current.getMethod() + ", call vars: " + StringUtils.join(current.getVarargsNames()));
            }
            if (nested != null) {
                System.out.println("### (" + i + ") nested " + nested.getMethod() + ", call vars: " + StringUtils.join(nested.getVarargsNames()));
            }
        }

        Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        String[] varNames = calls.get(calls.size() - 2).getCurrentNestedMethodCall().getVarargsNames();
        for (int i = 0; i < args.length; i++) {
            templateBinding.put(varNames[i], args[i]);
        }

        templateBinding.put("session", Scope.Session.current());
        templateBinding.put("request", Http.Request.current());
        templateBinding.put("flash", Scope.Flash.current());
        templateBinding.put("params", Scope.Params.current());
        templateBinding.put("errors", Validation.errors());
        templateBinding.put("out", writer);

        String source = FileUtils.readFileToString(Play.getFile("app/views/" + filePath), "UTF-8");
        if (StringUtils.isNotBlank(safeFormatter)) {
            source = "#{safeFormatter '" + safeFormatter + "'}" + source + "#{/safeFormatter}";
        }
        Template template = TemplateLoader.loadString(source);
        template.render(templateBinding.data);
        writer.close();

        return new BaseTemplate.RawData(strWriter.toString());
    }

}
