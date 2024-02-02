package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.InvocationTargetException;

abstract class CallableMemberDescriptor extends MaybeEmptyCallableMemberDescriptor {
  abstract TemplateModel invokeMethod(BeansWrapper paramBeansWrapper, Object paramObject, Object[] paramArrayOfObject) throws TemplateModelException, InvocationTargetException, IllegalAccessException;
  
  abstract Object invokeConstructor(BeansWrapper paramBeansWrapper, Object[] paramArrayOfObject) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException;
  
  abstract String getDeclaration();
  
  abstract boolean isConstructor();
  
  abstract boolean isStatic();
  
  abstract boolean isVarargs();
  
  abstract Class[] getParamTypes();
  
  abstract String getName();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\CallableMemberDescriptor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */