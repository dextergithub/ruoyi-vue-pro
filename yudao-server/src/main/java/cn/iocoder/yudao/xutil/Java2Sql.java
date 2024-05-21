package cn.iocoder.yudao.xutil;


import com.sun.source.doctree.CommentTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;


import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.DocumentationTool;
import javax.tools.JavaFileObject;
import java.io.File;

import java.util.*;
import java.util.function.Consumer;


/**
 * @author admin
 */
public class Java2Sql {
    public static void main(final String... args) throws Exception {
        String[] RootDirs = new String[]{
                "D:\\github\\ruoyi-vue-pro\\yudao-module-mp\\yudao-module-mp-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-bpm\\yudao-module-bpm-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-crm\\yudao-module-crm-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-erp\\yudao-module-erp-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-mall\\yudao-module-product-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-mall\\yudao-module-promotion-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-mall\\yudao-module-statistics-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-mall\\yudao-module-trade-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-member\\yudao-module-member-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-pay\\yudao-module-pay-biz\\src\\main\\java",
                "D:\\github\\ruoyi-vue-pro\\yudao-module-report\\yudao-module-report-biz\\src\\main\\java"
        };


            src2Sql(RootDirs);

    }

    public static void src2Sql( String[]  rootDir) throws Exception {
        List<String> options = new ArrayList<>();
//        options.add("-sourcepath");
//        options.add(RootDir);

        List<String> modelFiles = new ArrayList<>();
        //List<String> rootDir = Arrays.asList(RootDir);
        for (String dir : rootDir) {
            File file = new File(dir);
            showJavaFiles(file.listFiles(), f -> {
                if (f.getName().endsWith("DO.java") && f.getPath().contains("dataobject")) {
                    modelFiles.add(f.getPath());
                }
            });
        }

        modelFiles.forEach(x -> {
            options.add(x);
        });

        ServiceLoader<DocumentationTool> docTool = ServiceLoader.load(DocumentationTool.class);

        DocumentationTool tool = docTool.findFirst().get();
        tool.getTask(null, null, new DiagnosticListener() {

            @Override
            public void report(Diagnostic diagnostic) {
                System.out.println(diagnostic);
            }
        }, MyDoclet.class, options, null).call();

    }

    public static void showJavaFiles(File[] files, Consumer<File> consumer) {
        for (File file : files) {
            if (file.isDirectory()) {
                //System.out.println("Directory: " + file.getAbsolutePath());
                showJavaFiles(file.listFiles(), consumer); // Calls same method again.
            } else {
                if (file.getName().endsWith(".java")) {
                    consumer.accept(file);
                }
            }
        }
    }


}
