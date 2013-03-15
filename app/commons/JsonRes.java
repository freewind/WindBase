package commons;

import java.util.HashMap;
import java.util.Map;

/**
 * Member: Freewind
 * Date: 12-10-23
 * Time: 下午7:54
 */
public class JsonRes {

    public final boolean ok;
    public final String message;

    public Map<String, Object> data = new HashMap<String, Object>();

    public JsonRes(boolean ok) {
        this.ok = ok;
        message = null;
    }

    public JsonRes(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public String toJsonString() {
        data.put("ok", ok);
        data.put("message", message);
        return Helper.toJson(data);
    }

    public JsonRes put(String key, Object value) {
        data.put(key, value);
        return this;
    }

}
