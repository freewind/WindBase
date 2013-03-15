package controllers;

import commons.AppCache;
import models.User;
import models.UserRole;

import java.util.List;

/**
 * User: Freewind
 * Date: 13-2-4
 * Time: 下午1:33
 */
public class UserRoles extends BaseAdminController {

    public static void index() {
        request.format = "html";
        render();
    }

    public static void list() {
        List<UserRole> roles = UserRole.find.all();
        render(roles);
    }

    public static void get(String id) {
        UserRole role = UserRole.find.byId(id);
        render("@single", role);
    }

    public static void create(String name) {
        UserRole role = new UserRole(name);
        role.save();
        render("@single", role);
    }

    public static void addUserToRole(String userId, String roleId) {
        UserRole role = UserRole.find.byId(roleId);
        User user = User.find.byId(userId);
        if (!role.users.contains(user)) {
            role.users.add(user);
            role.saveManyToManyAssociations("users");
        }
        AppCache.deleteLoggedUser(user);
        ok();
    }

    public static void removeUsersFromRole(String roleId, String[] userIds) {
        UserRole role = UserRole.find.byId(roleId);
        for (String userId : userIds) {
            User user = User.find.byId(userId);
            role.users.remove(user);
            AppCache.deleteLoggedUser(user);
        }
        role.saveManyToManyAssociations("users");
        ok();
    }

    public static void update(String roleId, String name) {
        UserRole role = UserRole.find.byId(roleId);
        role.name = name;
        role.save();
        ok();
    }

}
