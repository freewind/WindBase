package models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.modules.ebean.Model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static commons.Helper.eq;

/**
 * Member: Freewind
 * Date: 12-8-18
 * Time: 下午5:47
 */
@MappedSuperclass
public abstract class BaseModel<T> extends Model {

    public abstract T getId();

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt = new Date();

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date updatedAt;

    @Override
    public boolean equals(Object other) {
        if (other == null || !eq(other.getClass(), this.getClass())) {
            return false;
        }
        return eq(this.getId(), ((BaseModel) other).getId());
    }

    @Override
    public int hashCode() {
        return getId() == null ? super.hashCode() : getId().hashCode();
    }

    @Transient
    @JsonIgnore
    public transient Map<String, Object> map;

    @JsonAnySetter
    public <T> T setAttrIfNone(String key, T value) {
        if (this.map == null) {
            this.map = new HashMap<String, Object>();
        }
        Object oriValue = this.map.get(key);
        if (oriValue == null) {
            if (value == null)
                return null;
            this.map.put(key, value);
            return value;
        } else {
            return (T) oriValue;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

}
