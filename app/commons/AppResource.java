package commons;

import org.apache.commons.io.FileUtils;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 13-1-14
 * Time: 下午10:02
 */
public class AppResource {

    private static final File ICON_FILE = Play.getFile("conf/menu_icons.txt");

    public static List<String> getMenuIcons() throws IOException {
        List<String> icons = new ArrayList<String>();
        for (String line : FileUtils.readLines(ICON_FILE, "UTF-8")) {
            line = line.trim();
            if (!line.startsWith("#") && isNotBlank(line)) {
                icons.add(line);
            }
        }
        return icons;
    }

}
