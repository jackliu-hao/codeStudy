package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.InvocationTargetException;

class MemberAndArguments extends MaybeEmptyMemberAndArguments {
   private final CallableMemberDescriptor callableMemberDesc;
   private final Object[] args;

   MemberAndArguments(CallableMemberDescriptor memberDesc, Object[] args) {
      this.callableMemberDesc = memberDesc;
      this.args = args;
   }

   Object[] getArgs() {
      return this.args;
   }

   TemplateModel invokeMethod(BeansWrapper bw, Object obj) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
      return this.callableMemberDesc.invokeMethod(bw, obj, this.args);
   }

   Object invokeConstructor(BeansWrapper bw) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException {
      return this.callableMemberDesc.invokeConstructor(bw, this.args);
   }

   CallableMemberDescriptor getCallableMemberDescriptor() {
      return this.callableMemberDesc;
   }
}
