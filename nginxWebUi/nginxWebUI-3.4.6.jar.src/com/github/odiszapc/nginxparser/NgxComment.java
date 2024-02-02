/*    */ package com.github.odiszapc.nginxparser;
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
/*    */ public class NgxComment
/*    */   extends NgxAbstractEntry
/*    */ {
/*    */   public NgxComment(String comment) {
/* 25 */     super(new String[0]);
/* 26 */     getTokens().add(new NgxToken(comment.substring(1)));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 31 */     return getName();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 35 */     return "#" + getValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxComment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */