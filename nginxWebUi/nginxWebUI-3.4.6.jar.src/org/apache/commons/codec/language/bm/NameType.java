/*    */ package org.apache.commons.codec.language.bm;
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
/*    */ 
/*    */ public enum NameType
/*    */ {
/* 30 */   ASHKENAZI("ash"),
/*    */ 
/*    */   
/* 33 */   GENERIC("gen"),
/*    */ 
/*    */   
/* 36 */   SEPHARDIC("sep");
/*    */   
/*    */   private final String name;
/*    */   
/*    */   NameType(String name) {
/* 41 */     this.name = name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 50 */     return this.name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\bm\NameType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */