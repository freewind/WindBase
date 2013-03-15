package livereload;

import commons.App;
import commons.Helper;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRProtocol {

        public String hello() {
            LinkedList<String> protocols = new LinkedList<String>();
            protocols.add("http://livereload.com/protocols/official-7");

            LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
            obj.put("command", "hello");
            obj.put("protocols", protocols);
            obj.put("serverName", "livereload-jvm");
            return Helper.toJson(obj);
        }

        public String alert(String msg) throws Exception {
            LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
            obj.put("command", "alert");
            obj.put("message", msg);
            return Helper.toJson(obj);
        }

        public String reload(String path) throws Exception {
            LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
            obj.put("command", "reload");
            obj.put("path", path);
            obj.put("liveCSS", true);
            return Helper.toJson(obj);
        }

        @SuppressWarnings("unchecked")
        public boolean isHello(String data) throws Exception {
            Object obj = App.parseJson(data);
            boolean back = obj instanceof Map;
            back = back && "hello".equals(((Map<Object, Object>) obj).get("command"));
            return back;
        }

    }