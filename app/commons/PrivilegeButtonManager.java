package commons;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.FileUtils;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static commons.Helper.eq;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 13-1-17
 * Time: 下午2:54
 */
public class PrivilegeButtonManager {

    private static final File CONFIG_FILE = Play.getFile("conf/privilege_buttons.json");

    public final Map<String, List<PrivilegeButton>> map = new HashMap<String, List<PrivilegeButton>>();

    public PrivilegeButtonManager() throws IOException {
        String content = FileUtils.readFileToString(CONFIG_FILE, "UTF-8");
        if (isNotBlank(content)) {
            Map<String, List<PrivilegeButton>> existing = Helper.createObjectMapper().readValue(content, new TypeReference<Map<String, List<PrivilegeButton>>>() {
            });
            map.putAll(existing);
        }
    }

    public List<PrivilegeButton> findOrCreateButtons(String menuCode) {
        List<PrivilegeButton> buttons = map.get(menuCode);
        if (buttons == null) {
            buttons = new ArrayList<PrivilegeButton>();
            map.put(menuCode, buttons);
        }
        return buttons;
    }

    public void save() throws IOException {
        FileUtils.writeStringToFile(CONFIG_FILE, Helper.toJson(this.map, true), "UTF-8");
    }

    public String toJson() {
        return Helper.toJson(this.map);
    }

    public PrivilegeButton findButton(String code) {
        for (List<PrivilegeButton> buttons : this.map.values()) {
            for (PrivilegeButton button : buttons) {
                if (eq(button.code, code)) {
                    return button;
                }
            }
        }
        return null;
    }

    public void removeButton(String code) throws IOException {
        PrivilegeButton button = findButton(code);
        for (String key : map.keySet()) {
            List<PrivilegeButton> buttons = map.get(key);
            if (buttons.remove(button)) {
                if (buttons.isEmpty()) {
                    map.remove(key);
                }
                return;
            }
        }
    }

    public List<PrivilegeButton> findButtons(String menuCode) {
        return map.get(menuCode);
    }

    public Set<String> getAllPrivilegeCodes() {
        Set<String> codes = new HashSet<String>();
        for (List<PrivilegeButton> buttons : this.map.values()) {
            for (PrivilegeButton button : buttons) {
                codes.add(button.code);
            }
        }
        return codes;
    }


}
