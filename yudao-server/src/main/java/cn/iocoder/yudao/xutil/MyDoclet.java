package cn.iocoder.yudao.xutil;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import java.util.Locale;
import java.util.Set;

public  class MyDoclet implements Doclet {

    @Override
    public void init(Locale locale, Reporter reporter) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Set.of();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return null;
    }


    @Override
    public boolean run(DocletEnvironment docEnv) {
        docEnv.getDocTrees();
        Elements elements = docEnv.getElementUtils();
        Lookup.lookup(docEnv.getDocTrees(),elements);

        return true;
    }

    public MyDoclet() {
    }
}
