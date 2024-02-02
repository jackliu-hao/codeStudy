/*    */ package org.noear.solon.ext;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class DataThrowable
/*    */   extends RuntimeException
/*    */   implements Serializable
/*    */ {
/*    */   private Object data;
/*    */   
/*    */   public Object data() {
/* 54 */     return this.data;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DataThrowable data(Object data) {
/* 61 */     this.data = data;
/* 62 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DataThrowable() {}
/*    */ 
/*    */   
/*    */   public DataThrowable(Throwable cause) {
/* 71 */     super(cause);
/*    */   }
/*    */   
/*    */   public DataThrowable(String message) {
/* 75 */     super(message);
/*    */   }
/*    */   
/*    */   public DataThrowable(String message, Throwable cause) {
/* 79 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\ext\DataThrowable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */