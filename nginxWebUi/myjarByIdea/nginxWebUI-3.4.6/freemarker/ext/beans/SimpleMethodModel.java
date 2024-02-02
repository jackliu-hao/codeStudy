package freemarker.ext.beans;

import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public final class SimpleMethodModel extends SimpleMethod implements TemplateMethodModelEx, TemplateSequenceModel, _UnexpectedTypeErrorExplainerTemplateModel {
   private final Object object;
   private final BeansWrapper wrapper;

   SimpleMethodModel(Object object, Method method, Class[] argTypes, BeansWrapper wrapper) {
      super(method, argTypes);
      this.object = object;
      this.wrapper = wrapper;
   }

   public Object exec(List arguments) throws TemplateModelException {
      try {
         return this.wrapper.invokeMethod(this.object, (Method)this.getMember(), this.unwrapArguments(arguments, this.wrapper));
      } catch (TemplateModelException var3) {
         throw var3;
      } catch (Exception var4) {
         throw _MethodUtil.newInvocationTemplateModelException(this.object, (Member)this.getMember(), var4);
      }
   }

   public TemplateModel get(int index) throws TemplateModelException {
      return (TemplateModel)this.exec(Collections.singletonList(new SimpleNumber(index)));
   }

   public int size() throws TemplateModelException {
      throw new _TemplateModelException((new _ErrorDescriptionBuilder(new Object[]{"Getting the number of items or listing the items is not supported on this ", new _DelayedFTLTypeDescription(this), " value, because this value wraps the following Java method, not a real listable value: ", new _DelayedToString(this.getMember())})).tips("Maybe you should to call this method first and then do something with its return value.", "obj.someMethod(i) and obj.someMethod[i] does the same for this method, hence it's a \"+sequence\"."));
   }

   public String toString() {
      return this.getMember().toString();
   }

   public Object[] explainTypeError(Class[] expectedClasses) {
      Member member = this.getMember();
      if (!(member instanceof Method)) {
         return null;
      } else {
         Method m = (Method)member;
         Class returnType = m.getReturnType();
         if (returnType != null && returnType != Void.TYPE && returnType != Void.class) {
            String mName = m.getName();
            if (mName.startsWith("get") && mName.length() > 3 && Character.isUpperCase(mName.charAt(3)) && m.getParameterTypes().length == 0) {
               return new Object[]{"Maybe using obj.something instead of obj.getSomething will yield the desired value."};
            } else {
               return mName.startsWith("is") && mName.length() > 2 && Character.isUpperCase(mName.charAt(2)) && m.getParameterTypes().length == 0 ? new Object[]{"Maybe using obj.something instead of obj.isSomething will yield the desired value."} : new Object[]{"Maybe using obj.something(", m.getParameterTypes().length != 0 ? "params" : "", ") instead of obj.something will yield the desired value"};
            }
         } else {
            return null;
         }
      }
   }
}
