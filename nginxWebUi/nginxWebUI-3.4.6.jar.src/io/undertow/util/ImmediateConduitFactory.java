/*    */ package io.undertow.util;
/*    */ 
/*    */ import org.xnio.conduits.Conduit;
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
/*    */ public class ImmediateConduitFactory<T extends Conduit>
/*    */   implements ConduitFactory<T>
/*    */ {
/*    */   private final T value;
/*    */   
/*    */   public ImmediateConduitFactory(T value) {
/* 31 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public T create() {
/* 36 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ImmediateConduitFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */