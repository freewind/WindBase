package models;

import com.avaje.ebean.Query;
import org.apache.commons.lang.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: freewind
 * Date: 13-2-17
 * Time: 下午4:02
 */
@Entity
@Table(name = "user_roles")
public class UserRole extends UuidIdModel {

    @Column(columnDefinition = "TEXT", unique = true, nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String privilegeCodes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role_r")
    public List<User> users;

    public static final Finder<String, UserRole> find = new Finder<String, UserRole>(String.class, UserRole.class);

    public UserRole(String name) {
        this.name = name;
    }

    public void setPrivilegeCodes(Collection<String> codes) {
        this.privilegeCodes = StringUtils.join(codes, ",");
    }

    public static UserRole findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    public Set<String> getPrivilegeCodeAsSet() {
        if (StringUtils.isBlank(privilegeCodes)) return new HashSet<String>();
        return new HashSet<String>(Arrays.asList(privilegeCodes.split(",")));
    }

    public Query<User> queryUsers() {
        return User.find.where().eq("roles.id", this.id).query();
    }

}
