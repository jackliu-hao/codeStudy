/*    */ package com.sun.jna;
/*    */ 
/*    */ import java.lang.reflect.Method;
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
/*    */ public class CallbackResultContext
/*    */   extends ToNativeContext
/*    */ {
/*    */   private Method method;
/*    */   
/*    */   CallbackResultContext(Method callbackMethod) {
/* 31 */     this.method = callbackMethod;
/*    */   } public Method getMethod() {
/* 33 */     return this.method;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\CallbackResultContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */