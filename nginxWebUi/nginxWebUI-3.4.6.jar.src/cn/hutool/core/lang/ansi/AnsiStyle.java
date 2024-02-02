/*    */ package cn.hutool.core.lang.ansi;
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
/*    */ public enum AnsiStyle
/*    */   implements AnsiElement
/*    */ {
/* 16 */   NORMAL("0"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   BOLD("1"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 26 */   FAINT("2"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   ITALIC("3"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 36 */   UNDERLINE("4");
/*    */   
/*    */   private final String code;
/*    */   
/*    */   AnsiStyle(String code) {
/* 41 */     this.code = code;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     return this.code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ansi\AnsiStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */