/*    */ package com.sun.mail.iap;
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
/*    */ 
/*    */ public class ConnectionException
/*    */   extends ProtocolException
/*    */ {
/*    */   private transient Protocol p;
/*    */   private static final long serialVersionUID = 5749739604257464727L;
/*    */   
/*    */   public ConnectionException() {}
/*    */   
/*    */   public ConnectionException(String s) {
/* 64 */     super(s);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConnectionException(Protocol p, Response r) {
/* 72 */     super(r);
/* 73 */     this.p = p;
/*    */   }
/*    */   
/*    */   public Protocol getProtocol() {
/* 77 */     return this.p;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\ConnectionException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */