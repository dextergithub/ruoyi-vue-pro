package cn.iocoder.yudao.xutil;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTrees;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sun.source.tree.Tree.Kind.EXTENDS_WILDCARD;
import static org.glassfish.jaxb.runtime.api.JAXBRIContext.getBaseType;

public class Lookup {
    public static void lookup(DocTrees trees, Elements es) {
//        lookup(trees, es.getAllModuleElements());
        es.getAllModuleElements().forEach(t -> {
            lookup(trees, t);
        });
    }

    public static void lookup(DocTrees trees, Element e) {
        if (e.getKind().equals(ElementKind.MODULE) && !StringUtils.isBlank(e.getSimpleName())) {
            return;
        }
        //System.out.println(e.getKind().name() + "->" + e.getSimpleName());
        if (e.getKind().equals(ElementKind.CLASS) && e.getSimpleName().toString().endsWith("DO")) {
            String decodeClass = decodeClass(trees, e);
            //System.out.println("sql->" + e.getSimpleName() + ":" + decodeClass);
            System.out.println(decodeClass);
            return;
        }
        e.getEnclosedElements().forEach(t -> {
            lookup(trees, t);
        });


        switch (e.getKind()) {
//            case CLASS:
//                System.out.println("class:" + e.getSimpleName());
//            case INTERFACE:
//                System.out.println("interface:" + e.getSimpleName());
//            case ENUM:
//                System.out.println("enum:" + e.getSimpleName());
//            case ANNOTATION_TYPE:
//                System.out.println("annotation:" + e.getSimpleName());
//            case EXCEPTION_PARAMETER:
//                System.out.println("exception:" + e.getSimpleName());
////            case EXTENDS_WILDCARD:
////                System.out.println("extends wildcard:" + e.getSimpleName());
//            case METHOD:
//                System.out.println("method:" + e.getSimpleName());
//            case CONSTRUCTOR:
//                System.out.println("constructor:" + e.getSimpleName());
//            case ENUM_CONSTANT:
//                System.out.println("enum constant:" + e.getSimpleName());
//            case FIELD:
//                System.out.println("field:" + e.getSimpleName());
//            case PARAMETER:
//                System.out.println("parameter:" + e.getSimpleName());
//            case TYPE_PARAMETER:
//                System.out.println("type parameter:" + e.getSimpleName());
//            case OTHER:
//                System.out.println("other:" + e.getSimpleName());
//            case PACKAGE:
//                System.out.println("package:" + e.getSimpleName());
//            case MODULE:
//                System.out.println("MODULE:" + e.getSimpleName());
//            default:
//                System.out.println("unkown:" + e.getSimpleName());
        }
    }

    private static String decodeClass(DocTrees trees, Element e) {
        TableName tableName = e.getAnnotation(TableName.class);
        if (tableName == null) {
            return "";
        }


        DocCommentTree comment = trees.getDocCommentTree(e);
        String tabComment = getComment(comment);
        List<String> columns = new ArrayList<>();
        String baseType = getBaseDO(e);
        if (baseType.contains("TenantBaseDO")) {
            columns.add("tenant_id bigint null COMMENT '多租户编号'");
        }

        if (baseType.contains("BaseDO")) {
            columns.add("create_time datetime(3) not null default current_timestamp(3)   COMMENT '创建时间'");
            columns.add("update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3)   COMMENT '最后更新时间'");
            columns.add("creator varchar(64) null comment '创建人'");
            columns.add("updater varchar(64) null comment '更新人'");
            columns.add("deleted tinyint(1) default 0 comment '删除标记,1已删除0未删除'");
        }

        e.getEnclosedElements().forEach(x -> {
            if (x.getKind().equals(ElementKind.FIELD) &&
                    x.getModifiers().stream().noneMatch(p -> p.name().equalsIgnoreCase("static"))) {
                String fields = decodeField(trees, x);
                columns.add(fields);
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append("\nDROP TABLE IF EXISTS `"+ tableName.value() +"`;");
        sb.append("\nCREATE TABLE `" + tableName.value() + "`(");
        sb.append("\n\t" + columns.stream().collect(Collectors.joining("\r\n\t,")));
        sb.append("\n\t, PRIMARY KEY (`id`)");
        sb.append("\n)");
        if (StringUtils.isNoneBlank(tabComment)) {
            sb.append("COMMENT='" + tabComment + "';");
        }else {
            sb.append(";");
        }
        return sb.toString();
    }

    private static String getBaseDO(Element e) {
        if (e instanceof TypeElement ty) {
            if (ty.getSuperclass() instanceof DeclaredType dt) {
                return (dt).toString();
            }
        }
        return "";
    }

    private static String getComment(DocCommentTree comment) {
        if (comment == null || CollectionUtils.isEmpty(comment.getFullBody())) {
            return "";
        }
        String txt = comment.getFullBody().stream().filter(x -> x instanceof TextTree).map(x -> ((TextTree) x).getBody()).collect(Collectors.joining());
        return noWarp(txt).replaceAll("DO", "").trim();

    }

    private static String noWarp(String txt) {
        return txt.replaceAll("\n", "\\\\n");
    }

    private static String decodeField(DocTrees trees, Element e) {

        DocCommentTree comment = trees.getDocCommentTree(e);
        String fileName = e.getSimpleName().toString();

        String commentStr = "";
        if (comment != null && comment.getFirstSentence() != null && comment.getFirstSentence().getFirst() != null) {
            commentStr = ((TextTree) comment.getFirstSentence().getFirst()).getBody();
        }
        if (fileName.equals("id")) {
            return "id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键'";
        }
        return "`" + getColum(fileName) + "`\t" + getMySqlType(e.asType()) + "\t null\t comment '" + noWarp(commentStr) + "'";
    }

    public static String getMySqlType(TypeMirror t) {

        String javaType = t.toString();
        DeclaredType dt = (DeclaredType) t;

        switch (javaType) {
            case "java.lang.String":
                return "varchar(255)";
            case "java.lang.Integer":
                return "int(11)";
            case "java.lang.Long":
                return "bigint(20)";
            case "java.lang.Double":
                return "decimal(18,8)";
            case "java.lang.Boolean":
                return "tinyint(1)";
            case "java.util.Date":
                return "datetime(3)";
            case "java.time.LocalDateTime":
                return "datetime(3)";
            case "java.math.BigDecimal":
                return "decimal(18,8)";
            case "java.time.LocalDate":
                return "date";
            case "java.time.LocalTime":
                return "time";
            default:
        }
        if (javaType.startsWith("java.util.List")
                || javaType.startsWith("java.util.Set")
                || javaType.startsWith("cn.iocoder.yudao")
                || javaType.startsWith("java.util.Map")) {
            return "json";
        }
        return "";
    }

    public static String getColum(String field) {
        if (field == null || field.isEmpty()) {
            return field;
        }

        StringBuilder underscore = new StringBuilder();
        for (char ch : field.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                underscore.append("_").append(Character.toLowerCase(ch));
            } else {
                underscore.append(ch);
            }
        }
        return underscore.toString();
    }
}
