package commons;

import models.Member;
import models.User;

import java.util.Set;

public class Can {

    private final WithPrivilegeCodes user;

    public Can(WithPrivilegeCodes user) {
        this.user = user;
    }

    public boolean access(String privilegeCode, Object... params) {
        App.notBlank(privilegeCode, "privilegeCode");
        Set<String> codes = getCodes();
        return codes.contains(privilegeCode);
    }

    private Set<String> getCodes() {
        if (user instanceof User) {
            return ((User) user).getPrivilegeCodes();
        } else {
            return ((Member) user).getPrivilegeCodes();
        }
    }

}
