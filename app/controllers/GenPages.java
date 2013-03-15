package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import commons.Gen;
import commons.Helper;
import commons.ViewHelper;
import org.apache.commons.lang.ArrayUtils;
import play.Play;
import play.classloading.ApplicationClasses;
import play.mvc.Before;

import javax.persistence.Entity;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static commons.Helper.eq;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 12-12-22
 * Time: 下午10:17
 */
public class GenPages extends BaseAdminController {

    @Before
    static void prepareGenHelper() {
        renderArgs.put("gh", new GenHelper());
    }


    public static void getModels() {
        List<String> list = new ArrayList<String>();
        for (ApplicationClasses.ApplicationClass cls : Play.classes.all()) {
            if (cls.javaClass.isAnnotationPresent(Entity.class)) {
                list.add(cls.name);
            }
        }
        renderJSON(Helper.toJson(list));
    }

    private static List<FieldConfig> getFieldConfigs(Class<?> modelCls) {
        List<FieldConfig> list = new ArrayList<FieldConfig>();
        Field[] fields = modelCls.getFields();
        FieldConfig idField = null;
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
                FieldConfig fieldConfig = new FieldConfig(f);
                if (eq(f.getName(), "id")) {
                    idField = fieldConfig;
                } else {
                    list.add(fieldConfig);
                }
            }
        }
        // 把id调到最前面
        list.add(0, idField);
        return list;
    }

    private static FieldConfig findFieldConfigByName(List<FieldConfig> list, String fieldName) {
        for (FieldConfig f : list) if (eq(f.name, fieldName)) return f;
        return null;
    }

    public static void checkActionView(String path) {
        renderText(ViewHelper.hasView(path));
    }

    public static void configure(String modelClsName) throws Exception {
        request.format = "html";
        Class<?> modelCls = Class.forName(modelClsName);
        List<FieldConfig> modelFields = getFieldConfigs(modelCls);
        render(modelClsName, modelFields);
    }

    public static void getModelFields(String modelClsName) throws Exception {
        Class<?> modelCls = Class.forName(modelClsName);
        List<FieldConfig> modelFields = getFieldConfigs(modelCls);
        renderJSON(Helper.toJson(modelFields));
    }


    public static class FieldConfig {
        public String name;
        public String label;
        public String type;
        public int width;
        public boolean enable;

        public FieldConfig(String name) {
            this.name = name;
        }

        public FieldConfig(Field field) {
            this.name = field.getName();
            this.type = field.getType().getSimpleName();

            Gen.Label labelAnno = null;
            for (Annotation anno : field.getDeclaredAnnotations()) {
                if (anno.annotationType() == Gen.Label.class) {
                    labelAnno = (Gen.Label) anno;
                    break;
                }
            }
            this.label = labelAnno == null ? this.name : labelAnno.value();
        }

        @Override
        public String toString() {
            return name + ", " + label + ", " + width + ", " + enable;
        }
    }

    public static class JsonData {
        public String selectedAction;
        public String selectedModel;
        public List<FieldConfig> modelFields;
    }


    public static void generateTable(String modelClsName, int maxLineHeight, String modelFieldsJson,
            boolean showCheckBoxes) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        render(modelClsName, modelFields, maxLineHeight, showCheckBoxes);
    }

    private static List<FieldConfig> getEnabledFieldConfigs(String json) throws IOException {
        List<FieldConfig> _fieldConfigs = Helper.createObjectMapper().readValue(json, new TypeReference<List<FieldConfig>>() {
        });
        List<FieldConfig> modelFields = new ArrayList<FieldConfig>();
        for (FieldConfig f : _fieldConfigs) {
            if (f.enable) {
                modelFields.add(f);
            }
        }
        return modelFields;
    }

    public static void generateCRUD(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        modelFields = removeFieldByNames(modelFields, new String[]{"id", "createdAt", "updatedAt"});
        render(modelClsName, modelFields);
    }

    private static List<FieldConfig> removeFieldByNames(List<FieldConfig> modelFields, String[] names) {
        List<FieldConfig> list = new ArrayList<FieldConfig>();
        for (FieldConfig f : modelFields) {
            if (ArrayUtils.contains(names, f.name)) continue;
            list.add(f);
        }
        return list;
    }

    public static void generateExData(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        render(modelClsName, modelFields);
    }

    public static void generateAdd(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        modelFields = removeFieldByNames(modelFields, new String[]{"id", "createdAt", "updatedAt"});
        render(modelClsName, modelFields);
    }

    public static void generateShow(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        render(modelClsName, modelFields);
    }

    public static void generateEdit(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        render(modelClsName, modelFields);
    }

    public static void generateTree(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        render(modelClsName, modelFields);
    }

    public static void generateSingleJson(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        render(modelClsName, modelFields);
    }

    public static void generateListJson(String modelClsName, String modelFieldsJson) throws IOException {
        request.format = "txt";
        List<FieldConfig> modelFields = getEnabledFieldConfigs(modelFieldsJson);
        render(modelClsName, modelFields);
    }

    private static class GenHelper extends ViewHelper {
        public String getSimpleName(String clsName) {
            return substringAfterLast(clsName, ".");
        }

        public String getInstanceName(String clsName) {
            if (clsName.contains(".")) {
                clsName = getSimpleName(clsName);
            }
            return clsName.substring(0, 1).toLowerCase() + clsName.substring(1);
        }

        public String getParameters(List<FieldConfig> modelFields) {
            StringBuilder sb = new StringBuilder();
            for (FieldConfig f : modelFields) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(f.type).append(" ").append(f.name);
            }
            return sb.toString();
        }
    }

}
