package commons;

import com.avaje.ebean.Ebean;

import java.util.Date;

/**
 * Member: Freewind
 * Date: 12-12-2
 * Time: 下午9:55
 */
public class OrderIdGenerator {

    private static final String SEQ_NAME = "order_id_generator_seq";

    public static String today() {
        return Helper.format(new Date(), "yyyyMMdd");
    }

    public static synchronized long next() {
        Ebean.beginTransaction();
        try {
            long seq = selectNextVal();
            if (!String.valueOf(seq).startsWith(today())) {
                String sql = "alter sequence " + SEQ_NAME + " restart with " + today() + "00000000";
                Ebean.createSqlUpdate(sql).execute();
                seq = selectNextVal();
            }
            Ebean.commitTransaction();
            return seq;
        } finally {
            Ebean.endTransaction();
        }
    }

    private static long selectNextVal() {
        String sql = "select nextval('" + SEQ_NAME + "') as v";
        return Ebean.createSqlQuery(sql).findUnique().getLong("v");
    }


}
