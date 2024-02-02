/*    */ package cn.hutool.poi.excel.sax;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum CellDataType
/*    */ {
/* 11 */   BOOL("b"),
/*    */   
/* 13 */   ERROR("e"),
/*    */   
/* 15 */   FORMULA("formula"),
/*    */   
/* 17 */   INLINESTR("inlineStr"),
/*    */   
/* 19 */   SSTINDEX("s"),
/*    */   
/* 21 */   NUMBER(""),
/*    */   
/* 23 */   DATE("m/d/yy"),
/*    */   
/* 25 */   NULL("");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final String name;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   CellDataType(String name) {
/* 36 */     this.name = name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 45 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CellDataType of(String name) {
/* 54 */     if (null == name)
/*    */     {
/* 56 */       return NUMBER;
/*    */     }
/*    */     
/* 59 */     if (BOOL.name.equals(name))
/* 60 */       return BOOL; 
/* 61 */     if (ERROR.name.equals(name))
/* 62 */       return ERROR; 
/* 63 */     if (INLINESTR.name.equals(name))
/* 64 */       return INLINESTR; 
/* 65 */     if (SSTINDEX.name.equals(name))
/* 66 */       return SSTINDEX; 
/* 67 */     if (FORMULA.name.equals(name)) {
/* 68 */       return FORMULA;
/*    */     }
/* 70 */     return NULL;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\CellDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */