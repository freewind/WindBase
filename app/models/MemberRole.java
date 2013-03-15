package models;

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

@Entity
@Table(name = "member_roles")
public class MemberRole extends UuidIdModel {

    @Column(columnDefinition = "TEXT", unique = true, nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String privilegeCodes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "member_role_r")
    public List<Member> members;

    public static final Finder<String, MemberRole> find = new Finder<String, MemberRole>(String.class, MemberRole.class);

    public MemberRole(String name) {
        this.name = name;
    }

    public void setPrivilegeCodes(Collection<String> codes) {
        this.privilegeCodes = StringUtils.join(codes, ",");
    }

    public static MemberRole findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    public Set<String> getPrivilegeCodeAsSet() {
        if (StringUtils.isBlank(privilegeCodes)) return new HashSet<String>();
        return new HashSet<String>(Arrays.asList(privilegeCodes.split(",")));
    }

}
