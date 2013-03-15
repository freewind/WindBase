package models;

/**
 * Member: Freewind
 * Date: 12-12-17
 * Time: 下午11:04
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category extends UuidIdModel {

    @Column(columnDefinition = "TEXT", nullable = false)
    public String name;

    @ManyToOne
    public Category parent;

    @OneToMany(mappedBy = "parent")
    public List<Category> children;

    @OneToMany(mappedBy = "category")
    public List<Article> articles;

    public long displayOrder = System.currentTimeMillis();

    public static final Finder<String, Category> find = new Finder<String, Category>(String.class, Category.class);

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

}
