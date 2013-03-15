package controllers;

import com.avaje.ebean.Query;
import models.Article;
import models.Category;
import play.data.validation.Required;
import play.modules.ebean.Pager;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 13-1-17
 * Time: 下午12:08
 */
public class Articles extends BaseAdminController {

    public static void list(String categoryId) {
        final Query<Article> query;
        if (isBlank(categoryId)) {
            query = Article.find.where().order("createdAt desc").setMaxRows(getPageSize());
        } else {
            query = Article.find.where().eq("category.id", categoryId).order("createdAt desc").setMaxRows(getPageSize());
        }
        Pager articles = new Pager<Article>(query, getPage(), getPageSize());
        render(articles);
    }

    public static void create(@Required String categoryId, String title, String content) {
        Category category = Category.find.byId(categoryId);
        Article article = new Article();
        article.category = category;
        article.title = title;
        article.content = content;
        article.save();
        render("@single", article);
    }

    public static void update(String id, String title, String content) {
        Article article = Article.find.byId(id);
        article.title = title;
        article.content = content;
        article.save();
        render("@single", article);
    }

    public static void remove(String[] ids) {
        System.out.println("### ids: " + ids);
        for (String id : ids) {
            Article article = Article.find.byId(id);
            article.delete();
            System.out.println("### delete: " + id);
        }
        ok();
    }

    public static void show(String id) {
        Article article = Article.find.byId(id);
        render("@single", article);
    }

}
