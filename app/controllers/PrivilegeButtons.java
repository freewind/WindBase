package controllers;

import commons.App;
import commons.PrivilegeButton;
import commons.PrivilegeButtonManager;
import models.UserRole;
import play.cache.Cache;
import play.data.binding.As;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * User: Freewind
 * Date: 12-12-25
 * Time: 上午12:03
 */
public class PrivilegeButtons extends BaseAdminController {

    public static void index() throws IOException {
        request.format = "html";
        render();
    }

    public static void getForRole(String roleId) throws IOException {
        UserRole role = UserRole.find.byId(roleId);
        renderText(role.privilegeCodes);
    }

    public static void createButton(String menuCode, String name, String icon) throws IOException {
        PrivilegeButtonManager manager = new PrivilegeButtonManager();
        PrivilegeButton button = new PrivilegeButton();
        button.menuCode = menuCode;
        button.code = App.newId();
        button.name = name;
        button.icon = icon;

        List<PrivilegeButton> buttons = manager.findOrCreateButtons(menuCode);
        buttons.add(button);
        manager.save();
        renderJSON(button);
    }

    public static void updateButton(String code, String name, String icon) throws IOException {
        PrivilegeButtonManager manager = new PrivilegeButtonManager();
        PrivilegeButton button = manager.findButton(code);
        button.name = name;
        button.icon = icon;
        manager.save();
        renderJSON(button);
    }

    public static void removeButton(String code) throws IOException {
        PrivilegeButtonManager manager = new PrivilegeButtonManager();
        manager.removeButton(code);
        manager.save();
        ok();
    }

    public static void toggle(String roleId, String buttonCode) {
        UserRole role = UserRole.find.byId(roleId);
        Set<String> codes = role.getPrivilegeCodeAsSet();
        if (codes.contains(buttonCode)) {
            codes.remove(buttonCode);
        } else {
            codes.add(buttonCode);
        }
        role.setPrivilegeCodes(codes);
        role.save();

        Cache.clear();

        ok();
    }

    public static void setMany(String roleId, @As(",") List<String> buttonCodes, boolean selected) {
        UserRole role = UserRole.find.byId(roleId);
        Set<String> set = role.getPrivilegeCodeAsSet();
        if (selected) {
            set.addAll(buttonCodes);
        } else {
            set.removeAll(buttonCodes);
        }
        role.setPrivilegeCodes(set);
        role.save();

        Cache.clear();
        ok();
    }

}
