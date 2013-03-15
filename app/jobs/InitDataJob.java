package jobs;

import commons.PrivilegeButtonManager;
import models.Article;
import models.Category;
import models.User;
import models.UserRole;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.util.Set;

/**
 * User: freewind
 * Date: 13-2-17
 * Time: 下午8:56
 */
@OnApplicationStart
public class InitDataJob extends Job {

    @Override
    public void doJob() throws Exception {
        if (User.find.findRowCount() > 0) return;

        User user = new User("admin", "admin");
        user.email = "admin@admin.com";
        user.save();

        UserRole superRole = new UserRole("super");
        Set<String> privilegeCodes = new PrivilegeButtonManager().getAllPrivilegeCodes();
        superRole.setPrivilegeCodes(privilegeCodes);
        superRole.save();

        user.roles.add(superRole);
        user.saveManyToManyAssociations("roles");

        // category and articles
        Category category = new Category("新闻");
        category.save();

        Article article = new Article();
        article.category = category;
        article.title = "Hello";
        article.content = "world";
        article.save();

    }

}
