/*    */ package org.noear.snack.core.exts;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EnumWrap
/*    */ {
/* 10 */   protected final Map<String, Enum> enumMap = new HashMap<>();
/*    */   protected final Enum[] enumOrdinal;
/*    */   
/*    */   public EnumWrap(Class<?> enumClass) {
/* 14 */     this.enumOrdinal = (Enum[])enumClass.getEnumConstants();
/*    */     
/* 16 */     for (int i = 0; i < this.enumOrdinal.length; i++) {
/* 17 */       Enum e = this.enumOrdinal[i];
/*    */       
/* 19 */       this.enumMap.put(e.name().toLowerCase(), e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public Enum get(int ordinal) {
/* 24 */     return this.enumOrdinal[ordinal];
/*    */   }
/*    */   
/*    */   public Enum get(String name) {
/* 28 */     return this.enumMap.get(name.toLowerCase());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\exts\EnumWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */