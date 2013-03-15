package models;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Member: Freewind
 * Date: 12-12-19
 * Time: 下午12:02
 */
@MappedSuperclass
public class SeqIdModel extends BaseModel<Long> {

    @Id
    public Long id;

    @Override
    public Long getId() {
        return id;
    }

}
