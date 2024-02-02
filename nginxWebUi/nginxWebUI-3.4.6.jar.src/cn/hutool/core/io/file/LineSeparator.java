/*    */ package cn.hutool.core.io.file;
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
/*    */ public enum LineSeparator
/*    */ {
/* 20 */   MAC("\r"),
/*    */   
/* 22 */   LINUX("\n"),
/*    */   
/* 24 */   WINDOWS("\r\n");
/*    */   
/*    */   private final String value;
/*    */   
/*    */   LineSeparator(String lineSeparator) {
/* 29 */     this.value = lineSeparator;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 33 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\LineSeparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */