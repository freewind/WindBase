package commons;

import models.Member;
import models.User;
import play.cache.Cache;

/**
 * User: freewind
 * Date: 13-2-17
 * Time: 下午4:59
 */
public class AppCache {

    public static User getLoggedUser(String userId) {
        User user = (User) Cache.get(keyLoggedUser(userId));
        if (user == null) {
            user = User.find.byId(userId);
            setLoggedUser(user);
        }
        return user;
    }

    public static void setLoggedUser(User user) {
        Cache.set(keyLoggedUser(user.id), user);
    }

    public static void deleteLoggedUser(User user) {
        if (user != null) {
            Cache.delete(keyLoggedUser(user.id));
        }
    }

    public static Member getLoggedMember(String memberId) {
        Member member = (Member) Cache.get(keyLoggedMember(memberId));
        if (member == null) {
            member = Member.find.byId(memberId);
            setLoggedMember(member);
        }
        return member;
    }

    public static void setLoggedMember(Member member) {
        Cache.set(keyLoggedMember(member.id), member);
    }

    public static void deleteLoggedMember(Member member) {
        Cache.delete(keyLoggedMember(member.id));
    }

    private static String keyLoggedUser(String userId) {
        return "user." + userId + ".logged";
    }

    private static String keyLoggedMember(String memberId) {
        return "member." + memberId + ".logged";
    }


}
