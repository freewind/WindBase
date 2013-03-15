package controllers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 13-1-26
 * Time: 下午1:50
 */
public class TypeScriptCompiler extends BaseController {

    private static final File ROOT = Play.getFile("typescripts");
    private static long lastModifiedAt = 0;
    private static List<File> tsFiles;

    public static void get(String path) throws IOException, InterruptedException {

        //        String   cmd = "tsc E:\\WORKSPACE\\WindBase\\typescripts\\directives\\directives.ts";
        //        Process p = Runtime.getRuntime().exec(cmd);
        //        String s = IOUtils.toString(p.getErrorStream());
        //        renderText(s);

        long start = System.nanoTime();
        tsFiles = getAllTsFiles();
        start = cost(start, "get all ts files");
        long last = getLastModified();
        start = cost(start, "get last modified files");
        if (last > lastModifiedAt) {
            lastModifiedAt = last;
            clearJsFiles();
            start = cost(start, "clear js files");
        }
        File tsFile = new File(ROOT, path);
        if (!tsFile.isFile()) notFound(tsFile.getAbsolutePath());

        File jsFile = new File(ROOT, path.replaceFirst("[.]ts$", ".js"));
        if (!jsFile.exists()) {
            compileTsFile(tsFile, jsFile);
            start = cost(start, "compile ts files");
        }

        renderBinary(jsFile);
    }

    private static long cost(long start, String message) {
        long now = System.nanoTime();
        System.out.println("### [" + message + "] cost: " + (now - start) / 1000000.0 + " ms");
        return now;
    }

    private static void compileTsFile(File tsFile, File jsFile) throws IOException, InterruptedException {
        Locale.setDefault(Locale.US);
        final String cmd;
        if (System.getProperty("os.name").startsWith("Windows")) {
            cmd = "tsc.exe " + tsFile.getAbsolutePath();
        } else {
            cmd = "tsc " + tsFile.getAbsolutePath();
        }

        //        ProcessBuilder pb = new ProcessBuilder(cmd);
        //        //merge error output with the standard output
        //        pb.redirectErrorStream(true);
        //
        //        Process p = pb.start();
        //        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("UTF-8")));
        //        String error = "";
        //        String line;
        //        while ((line = reader.readLine()) != null) {
        //            error += line;
        //        }
        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
        String out = IOUtils.toString(p.getInputStream());
        String error = IOUtils.toString(p.getErrorStream());
        System.out.println("### out: " + out);
        System.out.println("### err: " + error);
        if (isNotBlank(error)) {
            if (jsFile.exists()) FileUtils.forceDelete(jsFile);
            error(error);
        }
    }

    private static List<File> getAllTsFiles() {
        Iterator<File> files = FileUtils.iterateFiles(ROOT, new String[]{"ts"}, true);
        List<File> list = new ArrayList<File>();
        while (files.hasNext()) {
            list.add(files.next());
        }
        return list;
    }


    private static void clearJsFiles() throws IOException {
        Iterator<File> files = FileUtils.iterateFiles(ROOT, new String[]{"js"}, true);
        while (files.hasNext()) {
            FileUtils.forceDelete(files.next());
        }
    }

    private static long getLastModified() {
        long last = 0;
        for (File file : tsFiles) {
            long l = file.lastModified();
            if (l > last) {
                last = l;
            }
        }
        return last;
    }


}
