package controllers;

import commons.AppCache;
import commons.Can;
import commons.Check;
import commons.Helper;
import commons.NoAccessException;
import commons.ViewHelper;
import controllers.traits.RequestLoggingTrait;
import models.CrashReport;
import models.Member;
import models.User;
import play.data.validation.Error;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.With;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 12-10-8
 * Time: 下午2:58
 */
@With(RequestLoggingTrait.class)
public abstract class BaseController extends ExtendingController {

    protected static final String KEY_MEMBER_ID = "member.id";
    protected static final String KEY_USER_ID = "user.id";

    @Before(priority = 1)
    static void prepareHelper() {
        renderArgs.put("h", new ViewHelper());
    }

    @Before(priority = 3)
    static void printParams() {
        System.out.println("### url: " + request.url);
        System.out.println("### queryString: " + request.querystring);
        System.out.println("### params: " + params.allSimple());
    }


    @Before
    static void validateParams() {
        if (validation.hasErrors()) {
            CrashReport report = new CrashReport();
            report.STACK_TRACE = "url: " + request.url + "\n"
                                 + "------ errors ------\n" + getValidationErrors() + "\n"
                                 + "------ raw params -----\n" + getRawParams();
            report.save();
        }
    }

    private static String getRawParams() {
        return "TODO"; //TODO
    }


    protected static String getValidationErrors() {
        StringBuilder sb = new StringBuilder();
        Map<String, List<Error>> map = validation.errorsMap();
        for (String key : map.keySet()) {
            List<Error> errors = map.get(key);
            sb.append(key).append("[");
            for (int i = 0; i < errors.size(); i++) {
                Error error = errors.get(i);
                sb.append(error.message());
                if (i < errors.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]").append("\n");
        }
        return sb.toString();
    }

    @Before
    static void trimParams() {
        Map<String, String[]> all = params.all();
        for (Map.Entry<String, String[]> entry : all.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (value != null) {
                    values[i] = value.trim();
                }
            }
            all.put(key.trim(), values);
        }
    }


    @Before
    static void preparePrivilegeCodes() {
        renderArgs.put("userCan", new Can(currentUser()));
        renderArgs.put("memberCan", new Can(currentMember()));
    }

    @Before
    static void checkAccess() {
        Check check = getActionAnnotation(Check.class);
        if (check != null) {
            check(check);
            return;
        }
        check = getControllerInheritedAnnotation(Check.class);
        if (check != null) {
            check(check);
        }
    }

    private static void check(Check check) {
        String[] roles = check.value();
        for (int i = 0; i < roles.length; i++) {
            String role = roles[i];
            if (isBlank(role)) {
                return;
            } else if (role.equals("user")) {
                if (currentUser() != null) {
                    return;
                }
            } else if (role.equals("member")) {
                if (currentMember() != null) {
                    return;
                }
            } else {
                throw new RuntimeException("Unknown role: " + role);
            }
        }
        String firstRole = roles[0];
        if (firstRole.equals("user")) {
            Users.loginPage();
        } else {
            Members.loginPage();
        }
    }

    protected static Member currentMember() {
        String memberId = session.get(KEY_MEMBER_ID);
        if (memberId != null) {
            try {
                return AppCache.getLoggedMember(memberId);
            } catch (Exception e) {
                session.remove(KEY_MEMBER_ID);
            }
        }
        return null;
    }

    protected static User currentUser() {
        String userId = session.get(KEY_USER_ID);
        if (userId != null) {
            try {
                return AppCache.getLoggedUser(userId);
            } catch (Exception e) {
                session.remove(KEY_USER_ID);
            }
        }
        return null;
    }

    @Catch
    protected static void handleException(Exception e) throws Exception {
        if (e instanceof NoAccessException) {
            forbidden();
        } else {
            CrashReport report = new CrashReport();
            report.STACK_TRACE = "url: " + request.url + "\n" + Helper.getStackTrace(e);
            report.save();
            throw e;
        }
    }


    protected static int getPageSize() {
        String value = params.get("page_size");
        return isBlank(value) ? 20 : Integer.parseInt(value);
    }

    protected static int getPage() {
        String value = params.get("page");
        return isBlank(value) ? 1 : Integer.parseInt(value);
    }

}
