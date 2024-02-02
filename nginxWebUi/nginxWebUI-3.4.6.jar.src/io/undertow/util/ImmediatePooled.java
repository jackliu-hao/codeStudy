/*    */ package io.undertow.util;
/*    */ 
/*    */ import org.xnio.Pooled;
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
/*    */ public class ImmediatePooled<T>
/*    */   implements Pooled<T>
/*    */ {
/*    */   private final T value;
/*    */   
/*    */   public ImmediatePooled(T value) {
/* 33 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void discard() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void free() {}
/*    */ 
/*    */   
/*    */   public T getResource() throws IllegalStateException {
/* 46 */     return this.value;
/*    */   }
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ImmediatePooled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */