package commons;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.BaseModel;
import models.Member;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.libs.Crypto;
import play.mvc.Http;
import play.mvc.Http.Request;

import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.*;

public class App {

    public static Member findUserFromAccessCode(String accessCode) {
        String userId = StringUtils.substringBefore(accessCode, "-");
        String check = StringUtils.substringAfter(accessCode, "-");
        Member user = Member.find.byId(userId);
        if (user != null) {
            if (Helper.eq(App.generateAccessCode(user), accessCode)) {
                return user;
            }
        }
        return null;
    }

    public static String subPath(File file, File root) {
        return StringUtils.removeStart(file.getAbsolutePath(), root.getAbsolutePath()).replace("\\", "/");
    }

    public static class Path {
        public static final File SITE_FILES_ROOT = Play.getFile("site_files");
        // 存放管理员在后台添加的图片
        public static final File CARD_TEMPLATE_IMAGES_DIR = new File(SITE_FILES_ROOT, "card_template_images");
        // 存放生成的pdf
        public static final File PDF_FILES_DIR = new File(SITE_FILES_ROOT, "pdf");
        // 存放图片缩略图，客户端第一次访问某图片时临时生成
        public static final File IMAGE_THUMBNAILS_DIR = new File(SITE_FILES_ROOT, "image_thumbnails");
        public static final File SOLUTION_IMAGES_DIR = new File(SITE_FILES_ROOT, "solution_images");
        public static final File USER_HEAD_IMAGES_DIR = new File(SITE_FILES_ROOT, "user_head_images");
        public static final File MESSAGE_FILE = Play.getFile("conf/good_messages.txt");
        public static final File SITE_CONFIG_FILE = Play.getFile("conf/site.conf");
        public static final File UPLOADS_DIR = new File(SITE_FILES_ROOT, "uploads");
    }

    public static String newId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getSuffix(File file) {
        return getSuffix(file.getName());
    }

    public static String getSuffix(String path) {
        return StringUtils.substringAfterLast(path, ".");
    }

    public static String newSalt() {
        return RandomStringUtils.random(16, true, true);
    }

    public static String generateAccessCode(Member user) {
        return user.id + "-" + Crypto.sign(user.id, user.salt.getBytes());
    }

    public static Map<String, Object> parseJson2Map(String input) throws IOException {
        if (isBlank(input)) return null;
        ObjectMapper mapper = Helper.createObjectMapper();
        TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
        };
        JsonFactory factory = createJsonFactory();
        JsonParser jp = factory.createJsonParser(input);
        return mapper.readValue(jp, typeRef);
    }

    private static JsonFactory createJsonFactory() {
        JsonFactory factory = new JsonFactory();
        factory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        factory.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return factory;
    }


    public static JsonNode parseJson(String input) throws IOException {
        if (isBlank(input)) return null;
        ObjectMapper mapper = Helper.createObjectMapper();
        JsonFactory factory = createJsonFactory();
        JsonParser jp = factory.createJsonParser(input);
        return mapper.readTree(jp);
    }

    public static String crypt(String salt, String password) {
        String result = password;
        for (int i = 0; i < 17; i++) {
            result = Crypto.sign(result, salt.getBytes());
            if (result.length() > 32) {
                result = result.substring(0, 32);
            }
        }
        return result;
    }

    public static <T> T notNull(T obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " should not be null");
        }
        return obj;
    }

    public static String checkNotBlank(String str, String fieldName) {
        notNull(str, fieldName);
        if (str.trim().length() == 0) {
            throw new IllegalArgumentException(fieldName + " should not be blank");
        }
        return str;
    }

    public static boolean containsItem(String[] items, String target) {
        if (target.endsWith(".*")) {
            String prefix = target.substring(0, target.length() - 2);
            boolean ok = ArrayUtils.contains(items, prefix);
            if (ok) {
                return ok;
            }
            for (String item : items) {
                if (item.startsWith(prefix + ".")) {
                    return true;
                }
            }
            return false;
        } else {
            return ArrayUtils.contains(items, target);
        }
    }

    public static String md2html(String html) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        if (StringUtils.isBlank(html))
            return "";

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        File functionscript = new File("public/lib/marked.js");
        Reader reader = new FileReader(functionscript);
        engine.eval(reader);

        Invocable invocableEngine = (Invocable) engine;
        Object marked = engine.get("marked");
        Object lexer = invocableEngine.invokeMethod(marked, "lexer", html);
        Object result = invocableEngine.invokeMethod(marked, "parser", lexer);
        return result.toString();
    }

    public static <T> T first(List<T> list) {
        if (list == null || list.isEmpty())
            return null;
        return list.get(0);
    }

    public static <T> Set<T> toSet(T[] arr) {
        Set<T> set = new HashSet<T>();
        for (T t : arr) {
            set.add(t);
        }
        return set;
    }

    public static <T extends BaseModel> List<T> sortBy(List<T> list, final List<String> ids) {
        Collections.sort(list, new Comparator<BaseModel>() {
            @Override
            public int compare(BaseModel m1, BaseModel m2) {
                return ids.indexOf(m1.getId().toString()) - ids.indexOf(m2.getId().toString());
            }
        });
        return list;
    }

    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Object arg : args) {
            classes.add(arg.getClass());
        }
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, classes.toArray(new Class[0]));
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new WrapException(e);
        }
    }

    public static Boolean toBoolean(String str) {
        if (isBlank(str))
            return null;
        return Boolean.valueOf(str);
    }

    public static Boolean toBoolean(String str, boolean defaultValue) {
        if (isBlank(str))
            return defaultValue;
        return Boolean.valueOf(str);
    }

    public static Integer toInteger(String str) {
        if (isBlank(str))
            return null;
        return Integer.parseInt(str);
    }

    public static <T> void reorder(List<T> list, T obj, int order) {
        int index = list.indexOf(obj);
        if (index == -1)
            return;
        int targetIndex = index + order;
        if (targetIndex < 0)
            targetIndex = 0;
        if (targetIndex >= list.size())
            targetIndex = list.size() - 1;
        if (targetIndex == index)
            return;

        list.set(index, list.get(targetIndex));
        list.set(targetIndex, obj);
    }

    public static String first(String[] arr) {
        if (arr == null || arr.length == 0)
            return null;
        return arr[0];
    }

    public static String suffix(String filename) {
        int index = filename.lastIndexOf(".");
        if (index >= 0) {
            return filename.substring(index);
        }
        return "";
    }

    public static boolean isXhr(Request req) {
        Http.Header header = req.headers.get("X-Requested-With");
        return header != null && Helper.eq(header.value(), "XMLHttpRequest");
    }

    public static String or(String... strs) {
        if (strs == null)
            return null;
        for (String s : strs) {
            if (isNotBlank(s)) {
                return s;
            }
        }
        return null;
    }

    public static <T> List<T> containOrAdd(List<T> list, T t) {
        if (list.contains(t))
            return list;
        list.add(t);
        return list;
    }

    public static Map<String, Object> map(Object... items) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < items.length; i += 2) {
            map.put(items[i].toString(), items[1]);
        }
        return map;
    }

    public static void main(String[] args) throws Exception {
        // Play.current().getFile
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            md2html("**hello**j");
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start));
    }

    public static String notBlank(String value, String fieldName) {
        notNull(value, fieldName);
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException(fieldName + " should not be blank");
        }
        return value;
    }

    public static String decodeUrl(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new WrapException(e);
        }
    }

    public static boolean isHorizontal(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        return image.getWidth() >= image.getHeight();
    }

    public static File getImage(File file, String size, String id) throws IOException {
        File target = file;
        if (isNotBlank(size)) {
            int width = getWidth(size);
            int height = getHeight(size);
            if (width >= 0 || height >= 0) {
                File thumbnailFile = new File(Path.IMAGE_THUMBNAILS_DIR, size + "/" + id + "." + App.getSuffix(file));
                thumbnailFile.getParentFile().mkdirs();
                if (!thumbnailFile.exists()) {
                    Thumbnails.Builder<File> builder = Thumbnails.of(file);
                    if (width > 0 && height > 0) {
                        builder = builder.size(width, height);
                    } else {
                        if (width > 0) {
                            builder = builder.width(width);
                        } else {
                            builder = builder.height(height);
                        }
                    }
                    builder.toFile(thumbnailFile);
                }
                target = thumbnailFile;
            }
        }
        return target;
    }

    public static int getWidth(String size) {
        String width = StringUtils.substringBefore(size, "x");
        if (isBlank(width)) return 0;
        return Integer.parseInt(width);
    }

    public static int getHeight(String size) {
        String height = StringUtils.substringAfter(size, "x");
        if (isBlank(height)) return 0;
        return Integer.parseInt(height);
    }

    //    public static Image file2image(File file, File imageRootDir) throws IOException {
    //        if (file == null || !file.exists()) {
    //            return null;
    //        }
    //        Image image = new Image();
    //        image.save();
    //
    //        String filename = image.id + "." + App.getSuffix(file);
    //        String path = App.format(new Date(), "yyyyMMdd") + "/" + filename;
    //        File target = new File(imageRootDir, path);
    //        target.getParentFile().mkdirs();
    //        FileUtils.copyFile(file, target);
    //
    //        image.path = App.subPath(target, Path.SITE_FILES_ROOT);
    //        image.filename = filename;
    //        image.update();
    //        return image;
    //    }
    //
    //    public static SiteFile pdf2SiteFile(File file) throws IOException {
    //        if (file == null || !file.exists()) {
    //            throw new IllegalArgumentException("file is null or not exist: " + file);
    //        }
    //        SiteFile siteFile = new SiteFile();
    //        siteFile.save();
    //
    //        String path = App.format(new Date(), "yyyyMMdd") + "/" + siteFile.id + "." + App.getSuffix(file);
    //        File target = new File(Path.PDF_FILES_DIR, path);
    //        target.getParentFile().mkdirs();
    //        FileUtils.copyFile(file, target);
    //
    //        siteFile.path = path;
    //        siteFile.update();
    //        return siteFile;
    //    }

    public static Properties loadProperties(File file) {
        Properties p = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            p.load(reader);
            return p;
        } catch (IOException e) {
            throw new WrapException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public static Date parseDate(String dateStr, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(dateStr);
    }

    public static String formatNumber(double number) {
        return new DecimalFormat("0.00").format(number);
    }

    public static String size2str(long size) {
        if (size <= 0) return "0";
        if (size <= 1024) return "1K";
        else if (size < 1024 * 1024) return formatNumber(size / 1024.0) + "K";
        else if (size < 1024 * 1024 * 1024) return formatNumber(size / (1024.0 * 1024.0)) + "M";
        else return formatNumber(size / (1024.0 * 1024.0 * 1024.0)) + "G";
    }

}
