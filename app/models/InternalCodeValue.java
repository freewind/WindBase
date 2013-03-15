package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Member: Freewind
 * Date: 12-12-19
 * Time: 上午12:39
 */
@Entity
@Table(name = "internal_code_values")
public class InternalCodeValue extends UuidIdModel {

    @Column(columnDefinition = "TEXT")
    public String code;

    @Column(columnDefinition = "TEXT")
    public String value;

    public static final Finder<Long, InternalCodeValue> find = new Finder<Long, InternalCodeValue>(Long.class, InternalCodeValue.class);

    public static InternalCodeValue findProceedRequestLogId() {
        InternalCodeValue prop = find.where().eq("code", "proceed_request_log_id").findUnique();
        if (prop == null) {
            prop = new InternalCodeValue();
            prop.code = "proceed_request_log_id";
            prop.value = "-1";
            prop.save();
        }
        return prop;
    }

}
