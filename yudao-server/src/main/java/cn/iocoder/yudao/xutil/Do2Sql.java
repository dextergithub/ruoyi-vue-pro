package cn.iocoder.yudao.xutil;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.reflect.ClassPath;
import jdk.javadoc.doclet.Doclet;
import lombok.SneakyThrows;
import org.jsoup.select.Evaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Do2Sql {
    @SneakyThrows
    public static void main(String[] args) {
        String packageName = "cn.iocoder.yudao.module";

        Set<Class> set = ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName()
                        .startsWith(packageName)
                        && clazz.getPackageName().contains("dataobject")
                        && clazz.getSimpleName().endsWith("DO")
                )
                .map(clazz -> clazz.load())
                .collect(Collectors.toSet());

        set.forEach(clazz -> {
            System.out.println("-- " + clazz.getName() + " --");
        });

        // 调用com.sun.tools.javadoc.Main执行javadoc,参见 参考资料3
        // javadoc的调用参数，参见 参考资料1
        // -doclet 指定自己的docLet类名
        // -classpath 参数指定 源码文件及依赖库的class位置，不提供也可以执行，但无法获取到完整的注释信息(比如annotation)
        // -encoding 指定源码文件的编码格式
//        com.sun.tools.javadoc.Main.execute(new String[] {"-doclet",
//                        Doclet.class.getName(),
//                        "-docletpath",
//                        Doclet.class.getResource("/").getPath(),
//                        "-encoding","utf-8",
//                "-classpath",
//                "D:/j/facelog/facelog-main/target/classes;D:/j/facelog/db/target/classes;D:/j/facelog/db/sql2java/lib/swift-annotations-0.14.2.jar",
//                "J:/facelog/facelog-main/src/main/java/net/gdface/facelog/FaceLogDefinition.java"});
    }

    private static String getSql(Class t) {
        String tableName = getTableName(t);
        List<String> getFields = getFields(t);
        return "";
    }

    private static List<String> getFields(Class t) {
        List<Field> all = new ArrayList<>();
        Field[] fields = t.getDeclaredFields();
        all.addAll(Arrays.asList(fields));
        if (t.getSuperclass().equals(BaseDO.class)) {
            all.addAll(List.of(BaseDO.class.getFields()));
        }
        return all.stream().map(x -> getFieldSql(x)).collect(Collectors.toList());
    }

    private static String getFieldSql(Field x) {
        if(x.getName().equals("id")){
            return "id bigint(20) NOT NULL AUTO_INCREMENT COMMENT ";
        }

        return "";
    }

    private static String getTableName(Class t) {
        TableName tableName = (TableName) t.getAnnotation(TableName.class);
        if (tableName == null) {
            return null;
        }
        return tableName.value();
    }

}
