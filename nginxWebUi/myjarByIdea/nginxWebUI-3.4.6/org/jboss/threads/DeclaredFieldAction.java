package org.jboss.threads;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

final class DeclaredFieldAction implements PrivilegedAction<Field> {
   private final Class<?> clazz;
   private final String fieldName;

   DeclaredFieldAction(Class<?> clazz, String fieldName) {
      this.clazz = clazz;
      this.fieldName = fieldName;
   }

   public Field run() {
      try {
         return this.clazz.getDeclaredField(this.fieldName);
      } catch (NoSuchFieldException var2) {
         return null;
      }
   }
}
