package controllers;

import models.InternalCodeValue;
import play.modules.ebean.Pager;

/**
 * Member: Freewind
 * Date: 12-12-19
 * Time: 下午3:30
 */
public class InternalCodeValues extends BaseAdminController {

    public static void list() {
        request.format = "json";
        Pager<InternalCodeValue> rows = new Pager(InternalCodeValue.find.orderBy("code"), getPage(), getPageSize());
        render(rows);
    }

}
