/*    */ package cn.hutool.poi.excel.sax;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum ElementName
/*    */ {
/* 13 */   row,
/*    */ 
/*    */ 
/*    */   
/* 17 */   c,
/*    */ 
/*    */ 
/*    */   
/* 21 */   v,
/*    */ 
/*    */ 
/*    */   
/* 25 */   f;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean match(String elementName) {
/* 34 */     return name().equals(elementName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ElementName of(String elementName) {
/*    */     try {
/* 44 */       return valueOf(elementName);
/* 45 */     } catch (Exception exception) {
/*    */       
/* 47 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\ElementName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */