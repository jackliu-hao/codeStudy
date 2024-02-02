package freemarker.ext.beans;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class MemberMatcher<M extends Member, S> {
   private final Map<S, Types> signaturesToUpperBoundTypes = new HashMap();

   protected abstract S toMemberSignature(M var1);

   protected abstract boolean matchInUpperBoundTypeSubtypes();

   void addMatching(Class<?> upperBoundType, M member) {
      Class<?> declaringClass = member.getDeclaringClass();
      if (!declaringClass.isAssignableFrom(upperBoundType)) {
         throw new IllegalArgumentException("Upper bound class " + upperBoundType.getName() + " is not the same type or a subtype of the declaring type of member " + member + ".");
      } else {
         S memberSignature = this.toMemberSignature(member);
         Types upperBoundTypes = (Types)this.signaturesToUpperBoundTypes.get(memberSignature);
         if (upperBoundTypes == null) {
            upperBoundTypes = new Types();
            this.signaturesToUpperBoundTypes.put(memberSignature, upperBoundTypes);
         }

         upperBoundTypes.set.add(upperBoundType);
         if (upperBoundType.isInterface()) {
            upperBoundTypes.containsInterfaces = true;
         }

      }
   }

   boolean matches(Class<?> contextClass, M member) {
      boolean var10000;
      label25: {
         S memberSignature = this.toMemberSignature(member);
         Types upperBoundTypes = (Types)this.signaturesToUpperBoundTypes.get(memberSignature);
         if (upperBoundTypes != null) {
            if (this.matchInUpperBoundTypeSubtypes()) {
               if (containsTypeOrSuperType(upperBoundTypes, contextClass)) {
                  break label25;
               }
            } else if (containsExactType(upperBoundTypes, contextClass)) {
               break label25;
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   private static boolean containsExactType(Types types, Class<?> c) {
      return c == null ? false : types.set.contains(c);
   }

   private static boolean containsTypeOrSuperType(Types types, Class<?> c) {
      if (c == null) {
         return false;
      } else if (types.set.contains(c)) {
         return true;
      } else if (containsTypeOrSuperType(types, c.getSuperclass())) {
         return true;
      } else {
         if (types.containsInterfaces) {
            Class[] var2 = c.getInterfaces();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Class<?> anInterface = var2[var4];
               if (containsTypeOrSuperType(types, anInterface)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static class Types {
      private final Set<Class<?>> set;
      private boolean containsInterfaces;

      private Types() {
         this.set = new HashSet();
      }

      // $FF: synthetic method
      Types(Object x0) {
         this();
      }
   }
}
