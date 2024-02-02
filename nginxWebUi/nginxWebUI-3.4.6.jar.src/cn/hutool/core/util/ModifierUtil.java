/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ModifierUtil
/*     */ {
/*     */   public enum ModifierType
/*     */   {
/*  26 */     PUBLIC(1),
/*     */ 
/*     */ 
/*     */     
/*  30 */     PRIVATE(2),
/*     */ 
/*     */ 
/*     */     
/*  34 */     PROTECTED(4),
/*     */ 
/*     */ 
/*     */     
/*  38 */     STATIC(8),
/*     */ 
/*     */ 
/*     */     
/*  42 */     FINAL(16),
/*     */ 
/*     */ 
/*     */     
/*  46 */     SYNCHRONIZED(32),
/*     */ 
/*     */ 
/*     */     
/*  50 */     VOLATILE(64),
/*     */ 
/*     */ 
/*     */     
/*  54 */     TRANSIENT(128),
/*     */ 
/*     */ 
/*     */     
/*  58 */     NATIVE(256),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  63 */     ABSTRACT(1024),
/*     */ 
/*     */ 
/*     */     
/*  67 */     STRICT(2048);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final int value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ModifierType(int modifier) {
/*  80 */       this.value = modifier;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getValue() {
/*  89 */       return this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasModifier(Class<?> clazz, ModifierType... modifierTypes) {
/* 101 */     if (null == clazz || ArrayUtil.isEmpty(modifierTypes)) {
/* 102 */       return false;
/*     */     }
/* 104 */     return (0 != (clazz.getModifiers() & modifiersToInt(modifierTypes)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasModifier(Constructor<?> constructor, ModifierType... modifierTypes) {
/* 115 */     if (null == constructor || ArrayUtil.isEmpty(modifierTypes)) {
/* 116 */       return false;
/*     */     }
/* 118 */     return (0 != (constructor.getModifiers() & modifiersToInt(modifierTypes)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasModifier(Method method, ModifierType... modifierTypes) {
/* 129 */     if (null == method || ArrayUtil.isEmpty(modifierTypes)) {
/* 130 */       return false;
/*     */     }
/* 132 */     return (0 != (method.getModifiers() & modifiersToInt(modifierTypes)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasModifier(Field field, ModifierType... modifierTypes) {
/* 143 */     if (null == field || ArrayUtil.isEmpty(modifierTypes)) {
/* 144 */       return false;
/*     */     }
/* 146 */     return (0 != (field.getModifiers() & modifiersToInt(modifierTypes)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPublic(Field field) {
/* 156 */     return hasModifier(field, new ModifierType[] { ModifierType.PUBLIC });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPublic(Method method) {
/* 166 */     return hasModifier(method, new ModifierType[] { ModifierType.PUBLIC });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPublic(Class<?> clazz) {
/* 176 */     return hasModifier(clazz, new ModifierType[] { ModifierType.PUBLIC });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPublic(Constructor<?> constructor) {
/* 186 */     return hasModifier(constructor, new ModifierType[] { ModifierType.PUBLIC });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isStatic(Field field) {
/* 197 */     return hasModifier(field, new ModifierType[] { ModifierType.STATIC });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isStatic(Method method) {
/* 208 */     return hasModifier(method, new ModifierType[] { ModifierType.STATIC });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isStatic(Class<?> clazz) {
/* 219 */     return hasModifier(clazz, new ModifierType[] { ModifierType.STATIC });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSynthetic(Field field) {
/* 230 */     return field.isSynthetic();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSynthetic(Method method) {
/* 241 */     return method.isSynthetic();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSynthetic(Class<?> clazz) {
/* 252 */     return clazz.isSynthetic();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAbstract(Method method) {
/* 263 */     return hasModifier(method, new ModifierType[] { ModifierType.ABSTRACT });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int modifiersToInt(ModifierType... modifierTypes) {
/* 274 */     int modifier = modifierTypes[0].getValue();
/* 275 */     for (int i = 1; i < modifierTypes.length; i++) {
/* 276 */       modifier |= modifierTypes[i].getValue();
/*     */     }
/* 278 */     return modifier;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ModifierUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */