/*    */ package org.yaml.snakeyaml.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EnumUtils
/*    */ {
/*    */   public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
/* 30 */     for (Enum enum_ : (Enum[])enumType.getEnumConstants()) {
/* 31 */       if (enum_.name().compareToIgnoreCase(name) == 0) {
/* 32 */         return (T)enum_;
/*    */       }
/*    */     } 
/* 35 */     throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyam\\util\EnumUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */