package cn.hutool.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ModifierUtil {
   public static boolean hasModifier(Class<?> clazz, ModifierType... modifierTypes) {
      if (null != clazz && !ArrayUtil.isEmpty((Object[])modifierTypes)) {
         return 0 != (clazz.getModifiers() & modifiersToInt(modifierTypes));
      } else {
         return false;
      }
   }

   public static boolean hasModifier(Constructor<?> constructor, ModifierType... modifierTypes) {
      if (null != constructor && !ArrayUtil.isEmpty((Object[])modifierTypes)) {
         return 0 != (constructor.getModifiers() & modifiersToInt(modifierTypes));
      } else {
         return false;
      }
   }

   public static boolean hasModifier(Method method, ModifierType... modifierTypes) {
      if (null != method && !ArrayUtil.isEmpty((Object[])modifierTypes)) {
         return 0 != (method.getModifiers() & modifiersToInt(modifierTypes));
      } else {
         return false;
      }
   }

   public static boolean hasModifier(Field field, ModifierType... modifierTypes) {
      if (null != field && !ArrayUtil.isEmpty((Object[])modifierTypes)) {
         return 0 != (field.getModifiers() & modifiersToInt(modifierTypes));
      } else {
         return false;
      }
   }

   public static boolean isPublic(Field field) {
      return hasModifier(field, ModifierUtil.ModifierType.PUBLIC);
   }

   public static boolean isPublic(Method method) {
      return hasModifier(method, ModifierUtil.ModifierType.PUBLIC);
   }

   public static boolean isPublic(Class<?> clazz) {
      return hasModifier(clazz, ModifierUtil.ModifierType.PUBLIC);
   }

   public static boolean isPublic(Constructor<?> constructor) {
      return hasModifier(constructor, ModifierUtil.ModifierType.PUBLIC);
   }

   public static boolean isStatic(Field field) {
      return hasModifier(field, ModifierUtil.ModifierType.STATIC);
   }

   public static boolean isStatic(Method method) {
      return hasModifier(method, ModifierUtil.ModifierType.STATIC);
   }

   public static boolean isStatic(Class<?> clazz) {
      return hasModifier(clazz, ModifierUtil.ModifierType.STATIC);
   }

   public static boolean isSynthetic(Field field) {
      return field.isSynthetic();
   }

   public static boolean isSynthetic(Method method) {
      return method.isSynthetic();
   }

   public static boolean isSynthetic(Class<?> clazz) {
      return clazz.isSynthetic();
   }

   public static boolean isAbstract(Method method) {
      return hasModifier(method, ModifierUtil.ModifierType.ABSTRACT);
   }

   private static int modifiersToInt(ModifierType... modifierTypes) {
      int modifier = modifierTypes[0].getValue();

      for(int i = 1; i < modifierTypes.length; ++i) {
         modifier |= modifierTypes[i].getValue();
      }

      return modifier;
   }

   public static enum ModifierType {
      PUBLIC(1),
      PRIVATE(2),
      PROTECTED(4),
      STATIC(8),
      FINAL(16),
      SYNCHRONIZED(32),
      VOLATILE(64),
      TRANSIENT(128),
      NATIVE(256),
      ABSTRACT(1024),
      STRICT(2048);

      private final int value;

      private ModifierType(int modifier) {
         this.value = modifier;
      }

      public int getValue() {
         return this.value;
      }
   }
}
