package models;

import commons.App;
import commons.Helper;
import commons.WithPrivilegeCodes;
import org.apache.commons.lang.reflect.FieldUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: freewind
 * Date: 13-2-17
 * Time: 下午3:55
 */
@Entity
@Table(name = "users")
public class User extends UuidIdModel implements WithPrivilegeCodes {

    public static final Field ACCOUNT_FIELD;

    static {
        ACCOUNT_FIELD = FieldUtils.getField(User.class, "username", true);
    }

    @Column(columnDefinition = "TEXT", unique = true, nullable = false)
    public String username;

    @Column(columnDefinition = "TEXT")
    public String name;

    @Column(columnDefinition = "TEXT")
    public String salt;

    @Column(columnDefinition = "TEXT")
    public String password;

    @Column(columnDefinition = "TEXT")
    public String email;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role_r")
    public List<UserRole> roles;

    public boolean disabled;

    @Temporal(TemporalType.TIMESTAMP)
    public Date lastLoginAt;

    @Column(columnDefinition = "TEXT")
    public String lastLoginIp;

    public static final Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    public User() {
    }

    public User(String account, String password) throws IllegalAccessException {
        ACCOUNT_FIELD.set(this, account);
        resetPassword(password);
    }

    public void resetPassword(String password) {
        App.notBlank(password, "password");
        this.salt = App.newSalt();
        this.password = App.crypt(this.salt, password);
    }

    public String getAccount() throws IllegalAccessException {
        return (String) ACCOUNT_FIELD.get(this);
    }

    public Set<String> getPrivilegeCodes() {
        Set<String> privileges = new HashSet<String>();
        for (UserRole role : roles) {
            privileges.addAll(role.getPrivilegeCodeAsSet());
        }
        return privileges;
    }

    public static User findByAccount(String account) {
        return User.find.where().eq(ACCOUNT_FIELD.getName(), account).findUnique();
    }

    public boolean checkPassword(String password) {
        return Helper.eq(App.crypt(this.salt, password), this.password);
    }

}
