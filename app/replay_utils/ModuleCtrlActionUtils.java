package replay_utils;

import play.Play;
import play.classloading.ApplicationClasses;
import play.mvc.Controller;
import play.vfs.VirtualFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Member: Freewind
 * Date: 13-1-16
 * Time: 下午10:45
 */
public class ModuleCtrlActionUtils {

    public static List<ModuleCtrlAction> get(boolean includeModules) throws IOException {
        List<ModuleCtrlAction> list = new ArrayList<ModuleCtrlAction>();
        for (ApplicationClasses.ApplicationClass appCls : Play.classes.all()) {
            Class cls = appCls.javaClass;
            int clsModifiers = cls.getModifiers();
            if (Controller.class.isAssignableFrom(cls)
                && Modifier.isPublic(clsModifiers) && !Modifier.isAbstract(clsModifiers)) {
                for (Method method : cls.getDeclaredMethods()) {
                    int modifiers = method.getModifiers();
                    if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                        String module = null;
                        for (String moduleName : Play.modules.keySet()) {
                            VirtualFile moduleRoot = Play.modules.get(moduleName);
                            if (appCls.javaFile.getRealFile().getAbsolutePath().startsWith(moduleRoot.getRealFile().getAbsolutePath())) {
                                module = moduleName;
                            }
                        }
                        if (!includeModules && module != null) continue;

                        ModuleCtrlAction item = new ModuleCtrlAction(module, cls.getSimpleName(), method.getName());
                        list.add(item);
                    }
                }
            }
        }

        return list;
    }


}
