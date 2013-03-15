package commons;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.FileUtils;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Member: Freewind
 * Date: 13-2-6
 * Time: 下午2:17
 */
public class GridOptionsManager {

    private static final File GRID_OPTIONS_FILE = Play.getFile("conf/grid_options.json");
    private final List<GridOptions> all;

    public GridOptionsManager() throws IOException {
        String content = FileUtils.readFileToString(GRID_OPTIONS_FILE, "utf-8");
        if (isBlank(content)) {
            all = new ArrayList<GridOptions>();
        } else {
            all = Helper.createObjectMapper().readValue(content, new TypeReference<List<GridOptions>>() {
            });
        }
    }

    public GridOptions get(String id) {
        for (GridOptions gridOptions : all) {
            if (Helper.eq(gridOptions.id, id)) {
                return gridOptions;
            }
        }
        return null;
    }

    public void addOrUpdate(GridOptions gridOptions) {
        GridOptions ori = get(gridOptions.id);
        if (ori != null) {
            all.remove(ori);
        }
        all.add(gridOptions);
    }

    public void save() throws IOException {
        FileUtils.writeStringToFile(GRID_OPTIONS_FILE, Helper.toJson(all, true));
    }

    public List<GridOptions> getAll() {
        return this.all;
    }

    public static class GridOptions {
        public String id;
        public String data;
        public Map<String, Object> cellStyle;
        public List<ColumnDef> columnDefs;
        public String onSelectPage;
    }

    public static class ColumnDef {
        public String field;
        public String displayName;
        public String cellTemplate;
        public int width;
        public Map<String, Object> style;
    }
}
