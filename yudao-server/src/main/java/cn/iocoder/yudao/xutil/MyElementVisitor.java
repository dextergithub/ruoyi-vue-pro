package cn.iocoder.yudao.xutil;

import javax.lang.model.element.*;

public class MyElementVisitor implements ElementVisitor {
    @Override
    public Object visit(Element e, Object o) {
        return null;
    }

    @Override
    public Object visitPackage(PackageElement e, Object o) {
        return null;
    }

    @Override
    public Object visitType(TypeElement e, Object o) {
        return null;
    }

    @Override
    public Object visitVariable(VariableElement e, Object o) {
        return null;
    }

    @Override
    public Object visitExecutable(ExecutableElement e, Object o) {
        return null;
    }

    @Override
    public Object visitTypeParameter(TypeParameterElement e, Object o) {
        return null;
    }

    @Override
    public Object visitUnknown(Element e, Object o) {
        return null;
    }
}
