package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class AllowAllMemberAccessPolicy implements MemberAccessPolicy {
   public static final AllowAllMemberAccessPolicy INSTANCE = new AllowAllMemberAccessPolicy();
   public static final ClassMemberAccessPolicy CLASS_POLICY_INSTANCE = new ClassMemberAccessPolicy() {
      public boolean isMethodExposed(Method method) {
         return true;
      }

      public boolean isConstructorExposed(Constructor<?> constructor) {
         return true;
      }

      public boolean isFieldExposed(Field field) {
         return true;
      }
   };

   private AllowAllMemberAccessPolicy() {
   }

   public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
      return CLASS_POLICY_INSTANCE;
   }

   public boolean isToStringAlwaysExposed() {
      return true;
   }
}
