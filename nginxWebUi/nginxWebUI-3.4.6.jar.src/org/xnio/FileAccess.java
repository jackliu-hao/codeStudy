/*    */ package org.xnio;
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
/*    */ public enum FileAccess
/*    */ {
/* 27 */   READ_ONLY(true, false),
/* 28 */   READ_WRITE(true, true),
/* 29 */   WRITE_ONLY(false, true);
/*    */   
/*    */   private final boolean read;
/*    */   private final boolean write;
/*    */   
/*    */   FileAccess(boolean read, boolean write) {
/* 35 */     this.read = read;
/* 36 */     this.write = write;
/*    */   }
/*    */   
/*    */   boolean isRead() {
/* 40 */     return this.read;
/*    */   }
/*    */   
/*    */   boolean isWrite() {
/* 44 */     return this.write;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\FileAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */