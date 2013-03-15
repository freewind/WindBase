package controllers;

import play.Play;
import play.vfs.VirtualFile;

import java.io.File;

/**
 * Member: Freewind
 * Date: 13-1-24
 * Time: 下午12:47
 */
public class Templates extends ExtendingController {

    private static final String PREFIX = "templates/";

    public static void index() {
        renderBinary(Play.getFile(PREFIX + "admin.html"));
    }

    public static void get(String path) {
        File file = Play.getFile(PREFIX + path);
        if (file.exists()) {
            renderBinary(file);
        } else {
            for (String key : Play.modules.keySet()) {
                VirtualFile virtualFile = Play.modules.get(key);
                file = new File(virtualFile.getRealFile(), PREFIX + path);
                System.out.println(file);
                if (file.exists()) {
                    renderBinary(file);
                }
            }
            notFound(path);
        }
    }

}
