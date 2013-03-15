package models;

import commons.App;
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

import static commons.Helper.eq;

@Entity
@Table(name = "members")
public class Member extends UuidIdModel implements WithPrivilegeCodes {

    private static final Field ACCOUNT_FIELD;

    static {
        ACCOUNT_FIELD = FieldUtils.getField(Member.class, "email", true);
    }

    @Column(columnDefinition = "TEXT")
    public String email;

    @Column(columnDefinition = "TEXT")
    public String username;

    @Column(columnDefinition = "TEXT")
    public String name;

    @Column(columnDefinition = "TEXT")
    public String salt;

    @Column(columnDefinition = "TEXT")
    public String password;

    @Temporal(TemporalType.TIMESTAMP)
    public Date lastLoginAt;

    @Column(columnDefinition = "TEXT")
    public String lastLoginIp;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "member_role_r")
    public List<MemberRole> roles;

    public boolean disabled;

    public static final Finder<String, Member> find = new Finder<String, Member>(String.class, Member.class);

    public Member(String account, String password) throws IllegalAccessException {
        ACCOUNT_FIELD.set(this, account);
        this.resetPassword(password);
    }

    public Member(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public void resetPassword(String password) {
        App.notBlank(password, "password");
        this.salt = App.newSalt();
        this.password = App.crypt(this.salt, password);
    }

    public boolean checkPassword(String password) {
        App.notNull(password, "password");
        return eq(this.password, App.crypt(this.salt, password));
    }

    public Set<String> getPrivilegeCodes() {
        Set<String> privileges = new HashSet<String>();
        for (MemberRole role : roles) {
            privileges.addAll(role.getPrivilegeCodeAsSet());
        }
        return privileges;
    }

    public static Member findByAccount(String account) {
        return find.where().eq(ACCOUNT_FIELD.getName(), account).findUnique();
    }

    public String getAccount() throws IllegalAccessException {
        return (String) ACCOUNT_FIELD.get(this);
    }
}
