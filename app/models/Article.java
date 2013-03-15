package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Member: Freewind
 * Date: 13-1-17
 * Time: 下午12:07
 */
@Entity
@Table(name = "articles")
public class Article extends UuidIdModel {

    @Column(columnDefinition = "TEXT", nullable = false)
    public String title;

    @Column(columnDefinition = "TEXT")
    public String content;

    @ManyToOne
    public Category category;

    public static final Finder<String, Article> find = new Finder<String, Article>(String.class, Article.class);

}
