package controllers;

import models.Member;
import models.MemberRole;

import java.util.List;

/**
 * Member: Freewind
 * Date: 13-2-4
 * Time: 下午1:33
 */
public class MemberRoles extends BaseAdminController {

    public static void index() {
        request.format = "html";
        render();
    }

    public static void list() {
        request.format = "html";
        List<MemberRole> roles = MemberRole.find.all();
        render(roles);
    }

    public static void get(String id) {
        MemberRole role = MemberRole.find.byId(id);
        render("@single", role);
    }

    public static void create(String name) {
        MemberRole role = new MemberRole(name);
        role.save();
        render("@single", role);
    }

    public static void addUserToRole(String userId, String roleId) {
        MemberRole role = MemberRole.find.byId(roleId);
        Member user = Member.find.byId(userId);
        if (!role.members.contains(user)) {
            role.members.add(user);
            role.saveManyToManyAssociations("users");
        }
        ok();
    }

    public static void removeUsersFromRole(String roleId, String[] userIds) {
        MemberRole role = MemberRole.find.byId(roleId);
        for (String userId : userIds) {
            Member user = Member.find.byId(userId);
            role.members.remove(user);
        }
        role.saveManyToManyAssociations("users");
        ok();
    }

    public static void update(String roleId, String name) {
        MemberRole role = MemberRole.find.byId(roleId);
        role.name = name;
        role.save();
        ok();
    }

}
