/*    */ package freemarker.template.utility;
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
/*    */ public class UnsupportedNumberClassException
/*    */   extends RuntimeException
/*    */ {
/*    */   private final Class fClass;
/*    */   
/*    */   public UnsupportedNumberClassException(Class pClass) {
/* 30 */     super("Unsupported number class: " + pClass.getName());
/* 31 */     this.fClass = pClass;
/*    */   }
/*    */   
/*    */   public Class getUnsupportedClass() {
/* 35 */     return this.fClass;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\UnsupportedNumberClassException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */