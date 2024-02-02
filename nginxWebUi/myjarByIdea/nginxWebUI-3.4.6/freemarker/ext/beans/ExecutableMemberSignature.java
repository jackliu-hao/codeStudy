package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

final class ExecutableMemberSignature {
   private final String name;
   private final Class<?>[] args;

   ExecutableMemberSignature(String name, Class<?>[] args) {
      this.name = name;
      this.args = args;
   }

   ExecutableMemberSignature(Method method) {
      this(method.getName(), method.getParameterTypes());
   }

   ExecutableMemberSignature(Constructor<?> constructor) {
      this("<init>", constructor.getParameterTypes());
   }

   public boolean equals(Object o) {
      if (!(o instanceof ExecutableMemberSignature)) {
         return false;
      } else {
         ExecutableMemberSignature ms = (ExecutableMemberSignature)o;
         return ms.name.equals(this.name) && Arrays.equals(this.args, ms.args);
      }
   }

   public int hashCode() {
      return this.name.hashCode() + this.args.length * 31;
   }
}
