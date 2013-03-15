package commons;

import org.codehaus.groovy.runtime.NullObject;
import play.templates.BaseTemplate;
import play.templates.JavaExtensions;

/**
 * Member: Freewind
 * Date: 12-12-20
 * Time: 下午12:23
 */
public class MyJavaExtensions extends JavaExtensions {

    public static BaseTemplate.RawData toJson(Object obj) {
        if (obj instanceof NullObject) obj = null;
        return raw(Helper.toJson(obj));
    }

}
