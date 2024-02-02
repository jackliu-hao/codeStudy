/*    */ package ch.qos.logback.core.util;
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
/*    */ public class IncompatibleClassException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = -5823372159561159549L;
/*    */   Class<?> requestedClass;
/*    */   Class<?> obtainedClass;
/*    */   
/*    */   IncompatibleClassException(Class<?> requestedClass, Class<?> obtainedClass) {
/* 25 */     this.requestedClass = requestedClass;
/* 26 */     this.obtainedClass = obtainedClass;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\IncompatibleClassException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */