/*    */ package freemarker.ext.beans;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class FastPropertyDescriptor
/*    */ {
/*    */   private final Method readMethod;
/*    */   private final Method indexedReadMethod;
/*    */   
/*    */   public FastPropertyDescriptor(Method readMethod, Method indexedReadMethod) {
/* 34 */     this.readMethod = readMethod;
/* 35 */     this.indexedReadMethod = indexedReadMethod;
/*    */   }
/*    */   
/*    */   public Method getReadMethod() {
/* 39 */     return this.readMethod;
/*    */   }
/*    */   
/*    */   public Method getIndexedReadMethod() {
/* 43 */     return this.indexedReadMethod;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\FastPropertyDescriptor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */