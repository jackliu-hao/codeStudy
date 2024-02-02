/*    */ package org.noear.snack.core.exts;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ public class ThData<T> extends ThreadLocal<T> {
/*    */   private Supplier<T> _def;
/*    */   
/*    */   public ThData(Supplier<T> def) {
/*  9 */     this._def = def;
/*    */   }
/*    */ 
/*    */   
/*    */   protected T initialValue() {
/* 14 */     return this._def.get();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\exts\ThData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */