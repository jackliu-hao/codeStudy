package freemarker.ext.beans;

import java.lang.reflect.Method;

final class MethodMatcher extends MemberMatcher<Method, ExecutableMemberSignature> {
   protected ExecutableMemberSignature toMemberSignature(Method member) {
      return new ExecutableMemberSignature(member);
   }

   protected boolean matchInUpperBoundTypeSubtypes() {
      return true;
   }
}
