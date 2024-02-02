/*    */ package org.h2.mvstore;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MVStoreException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 2847042930249663807L;
/*    */   private final int errorCode;
/*    */   
/*    */   public MVStoreException(int paramInt, String paramString) {
/* 18 */     super(paramString);
/* 19 */     this.errorCode = paramInt;
/*    */   }
/*    */   
/*    */   public int getErrorCode() {
/* 23 */     return this.errorCode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\MVStoreException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */