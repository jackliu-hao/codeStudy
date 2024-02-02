package freemarker.ext.beans;

import java.lang.reflect.Field;

final class FieldMatcher extends MemberMatcher<Field, String> {
   protected String toMemberSignature(Field member) {
      return member.getName();
   }

   protected boolean matchInUpperBoundTypeSubtypes() {
      return true;
   }
}
