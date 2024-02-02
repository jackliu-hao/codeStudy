/*    */ package freemarker.core;
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
/*    */ public abstract class _DelayedConversionToString
/*    */ {
/* 25 */   private static final String NOT_SET = new String();
/*    */   
/*    */   private Object object;
/* 28 */   private volatile String stringValue = NOT_SET;
/*    */   
/*    */   public _DelayedConversionToString(Object object) {
/* 31 */     this.object = object;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 36 */     String stringValue = this.stringValue;
/* 37 */     if (stringValue == NOT_SET) {
/* 38 */       synchronized (this) {
/* 39 */         stringValue = this.stringValue;
/* 40 */         if (stringValue == NOT_SET) {
/* 41 */           stringValue = doConversion(this.object);
/* 42 */           this.stringValue = stringValue;
/* 43 */           this.object = null;
/*    */         } 
/*    */       } 
/*    */     }
/* 47 */     return stringValue;
/*    */   }
/*    */   
/*    */   protected abstract String doConversion(Object paramObject);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedConversionToString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */