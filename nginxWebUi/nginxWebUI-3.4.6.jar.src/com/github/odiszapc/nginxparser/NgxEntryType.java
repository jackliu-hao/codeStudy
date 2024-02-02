/*    */ package com.github.odiszapc.nginxparser;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public enum NgxEntryType
/*    */ {
/* 23 */   PARAM((Class)NgxParam.class),
/* 24 */   COMMENT((Class)NgxComment.class),
/* 25 */   IF((Class)NgxIfBlock.class),
/* 26 */   BLOCK((Class)NgxBlock.class);
/*    */   private final Class<? extends NgxEntry> clazz;
/*    */   private static Map<Class<? extends NgxEntry>, NgxEntryType> types;
/*    */   
/*    */   Class<? extends NgxEntry> getType() {
/* 31 */     return this.clazz;
/*    */   }
/*    */ 
/*    */   
/*    */   NgxEntryType(Class<? extends NgxEntry> clazz) {
/* 36 */     this.clazz = clazz;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/* 42 */     types = new HashMap<Class<? extends NgxEntry>, NgxEntryType>();
/* 43 */     for (NgxEntryType type : values()) {
/* 44 */       types.put(type.clazz, type);
/*    */     }
/*    */   }
/*    */   
/*    */   public static NgxEntryType fromClass(Class<? extends NgxEntry> clazz) {
/* 49 */     return types.get(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxEntryType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */