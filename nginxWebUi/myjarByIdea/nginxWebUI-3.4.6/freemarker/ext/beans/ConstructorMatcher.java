package freemarker.ext.beans;

import java.lang.reflect.Constructor;

final class ConstructorMatcher extends MemberMatcher<Constructor<?>, ExecutableMemberSignature> {
   protected ExecutableMemberSignature toMemberSignature(Constructor<?> member) {
      return new ExecutableMemberSignature(member);
   }

   protected boolean matchInUpperBoundTypeSubtypes() {
      return false;
   }
}
