package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: freewind
 * Date: 13-2-17
 * Time: 下午5:07
 */
@Entity
@Table(name = "crash_reports")
public class CrashReport extends UuidIdModel {


    @Column(columnDefinition = "TEXT")
    public String STACK_TRACE;

}
