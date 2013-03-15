package models;

import play.modules.ebean.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Member: Freewind
 * Date: 12-12-19
 * Time: 下午12:02
 */
@MappedSuperclass
public class UuidIdModel extends BaseModel<String> {

    @Id
    @UUID
    @Column(columnDefinition = "TEXT")
    public String id;

    @Override
    public String getId() {
        return id;
    }

}
