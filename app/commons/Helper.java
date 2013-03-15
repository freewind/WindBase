package commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 12-12-15
 * Time: 下午6:54
 */
public class Helper {

    public static boolean eq(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    public static String notBlank(String obj, String fieldName) {
        if (isBlank(obj)) {
            throw new IllegalArgumentException(fieldName + " should not be blank");
        }
        return obj;
    }

    public static <T> T notNull(T obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " should not be null");
        }
        return obj;
    }

    public static String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String toJson(Object obj) {
        return toJson(obj, false);
    }

    public static String toJson(Object obj, boolean prettyPrint) {
        String s = toJson(obj, null, prettyPrint);
        return isBlank(s) ? null : s;
    }

    public static String toJson(Object obj, Class<?> view, boolean prettyPrint) {
        if (obj == null) {
            return null;
        }
        ObjectMapper mapper = createObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
        try {
            if (view != null) {
                return mapper.writerWithView(view).writeValueAsString(obj);
            } else {
                return mapper.writeValueAsString(obj);
            }
        } catch (IOException e) {
            throw new WrapException(e);
        }
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    public static String getStackTrace(Exception e) throws UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        e.printStackTrace(ps);
        ps.close();
        return out.toString("UTF-8");
    }

}
