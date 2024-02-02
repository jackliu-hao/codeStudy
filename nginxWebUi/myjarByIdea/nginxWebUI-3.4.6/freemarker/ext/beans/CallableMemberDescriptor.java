package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.InvocationTargetException;

abstract class CallableMemberDescriptor extends MaybeEmptyCallableMemberDescriptor {
   abstract TemplateModel invokeMethod(BeansWrapper var1, Object var2, Object[] var3) throws TemplateModelException, InvocationTargetException, IllegalAccessException;

   abstract Object invokeConstructor(BeansWrapper var1, Object[] var2) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException;

   abstract String getDeclaration();

   abstract boolean isConstructor();

   abstract boolean isStatic();

   abstract boolean isVarargs();

   abstract Class[] getParamTypes();

   abstract String getName();
}
