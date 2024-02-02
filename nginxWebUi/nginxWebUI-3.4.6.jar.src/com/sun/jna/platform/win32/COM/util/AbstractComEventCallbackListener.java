/*    */ package com.sun.jna.platform.win32.COM.util;
/*    */ 
/*    */ import com.sun.jna.platform.win32.COM.IDispatchCallback;
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
/*    */ public abstract class AbstractComEventCallbackListener
/*    */   implements IComEventCallbackListener
/*    */ {
/* 31 */   IDispatchCallback dispatchCallback = null;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDispatchCallbackListener(IDispatchCallback dispatchCallback) {
/* 37 */     this.dispatchCallback = dispatchCallback;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\AbstractComEventCallbackListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */