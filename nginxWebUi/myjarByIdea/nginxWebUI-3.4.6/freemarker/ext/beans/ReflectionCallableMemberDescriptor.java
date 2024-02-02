package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

final class ReflectionCallableMemberDescriptor extends CallableMemberDescriptor {
   private final Member member;
   final Class[] paramTypes;

   ReflectionCallableMemberDescriptor(Method member, Class[] paramTypes) {
      this.member = member;
      this.paramTypes = paramTypes;
   }

   ReflectionCallableMemberDescriptor(Constructor member, Class[] paramTypes) {
      this.member = member;
      this.paramTypes = paramTypes;
   }

   TemplateModel invokeMethod(BeansWrapper bw, Object obj, Object[] args) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
      return bw.invokeMethod(obj, (Method)this.member, args);
   }

   Object invokeConstructor(BeansWrapper bw, Object[] args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
      return ((Constructor)this.member).newInstance(args);
   }

   String getDeclaration() {
      return _MethodUtil.toString(this.member);
   }

   boolean isConstructor() {
      return this.member instanceof Constructor;
   }

   boolean isStatic() {
      return (this.member.getModifiers() & 8) != 0;
   }

   boolean isVarargs() {
      return _MethodUtil.isVarargs(this.member);
   }

   Class[] getParamTypes() {
      return this.paramTypes;
   }

   String getName() {
      return this.member.getName();
   }
}
