package models;

import commons.Gen;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * Member: Freewind
 * Date: 12-12-18
 * Time: 下午12:14
 */
@Entity
@Table(name = "http_request_log")
public class HttpRequestLog extends SeqIdModel {

    @Column(columnDefinition = "TEXT", nullable = false)
    public String url;

    /**
     * get/post/...
     */
    @Column(columnDefinition = "TEXT")
    public String method;

    /**
     * html, js, css, image
     */
    @Gen.Label("资源类型")
    @Column(columnDefinition = "TEXT")
    public String resourceType;

    @Column(columnDefinition = "TEXT")
    public String queryString;

    @Column(columnDefinition = "TEXT")
    public String params;

    @Column(columnDefinition = "TEXT")
    public String headers;

    @Gen.Label("请求者IP")
    @Column(columnDefinition = "TEXT")
    public String remoteIp;

    @Column(columnDefinition = "TEXT")
    public String referer;

    @Column
    @Gen.Label("通过AJAX访问")
    public boolean ajax;

    public static final Finder<String, HttpRequestLog> find = new Finder<String, HttpRequestLog>(String.class, HttpRequestLog.class);

    public static List<HttpRequestLog> findFromId(Long lastId) {
        return find.where().select("url,method,resourceType,ajax,remoteIp")
                .findList();
    }

}
