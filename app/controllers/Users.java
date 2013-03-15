package controllers;


import com.avaje.ebean.ExpressionList;
import commons.Check;
import commons.Helper;
import models.User;
import models.UserRole;
import play.modules.ebean.Pager;

import static org.apache.commons.lang.StringUtils.*;

public class Users extends BaseAdminController {

    public static void index() {
        request.format = "html";
        render();
    }

    public static void add() {
    }

    public static void create(String email, String username, String password) throws IllegalAccessException {
        User user = new User();
        user.username = username;
        user.email = email;
        user.resetPassword(password);
        user.save();
        render("@single", user);
    }

    public static void list() {
        request.format = "json";
        Pager<User> users = new Pager<User>(User.find.orderBy("createdAt desc"), getPage(), getPageSize());
        render(users);
    }

    public static void show(String id) {
        request.format = "html";
        User user = User.find.byId(id);
        render(user);
    }

    public static void edit(String id) {
        request.format = "html";
        User user = User.find.byId(id);
        render(user);
    }

    public static void update(String id, String email, String username) {
        User user = User.find.byId(id);
        user.email = email;
        user.username = username;
        user.save();
        renderJSON(Helper.toJson(user));
    }

    public static void remove(String[] ids) {
        for (String id : ids) {
            User user = User.find.byId(id);
            user.delete();
        }
        ok();
    }

    public static void listForRole(String roleId) {
        UserRole role = UserRole.find.byId(roleId);
        Pager<User> users = new Pager<User>(role.queryUsers(), getPage(), getPageSize());
        render("@list", users);
    }

    public static void search(String email, String username, String name) {
        ExpressionList<User> query = User.find.where();
        if (isNotBlank(email)) query = query.ilike("email", "%" + email + "%");
        if (isNotBlank(username)) query = query.ilike("username", "%" + username + "%");
        if (isNotBlank(name)) query = query.ilike("name", "%" + name + "%");
        Pager<User> users = new Pager(query.query(), getPage(), getPageSize());
        render("@list", users);
    }

    public static void setStatus(String[] ids, boolean status) {
        for (String id : ids) {
            User user = User.find.byId(id);
            user.disabled = !status;
            user.save();
        }
        ok();
    }


    @Check("")
    public static void loginPage() {
        request.format = "html";
        render();
    }

    @Check("")
    public static void login(String account, String password) {
        User current = User.findByAccount(account);
        if (current != null && current.checkPassword(password)) {
            session.put(KEY_USER_ID, current.id);
            renderTemplate("Admins/current.json", current);
        }
        badRequest();
    }

    public static void logout() {
        session.clear();
        ok();
    }

    public static void changePassword(String id, String password) {
        if (isNotBlank(password)) {
            User user = User.find.byId(id);
            user.resetPassword(password);
            user.save();
        }
        ok();
    }


}