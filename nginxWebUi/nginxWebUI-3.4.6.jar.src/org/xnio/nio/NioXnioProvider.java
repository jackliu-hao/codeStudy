/*    */ package org.xnio.nio;
/*    */ 
/*    */ import org.xnio.Xnio;
/*    */ import org.xnio.XnioProvider;
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
/*    */ public final class NioXnioProvider
/*    */   implements XnioProvider
/*    */ {
/* 30 */   private static final Xnio INSTANCE = new NioXnio();
/*    */ 
/*    */   
/*    */   public Xnio getInstance() {
/* 34 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 39 */     return INSTANCE.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioXnioProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */