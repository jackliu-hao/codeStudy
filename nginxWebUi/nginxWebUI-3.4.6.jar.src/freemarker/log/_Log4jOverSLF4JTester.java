/*    */ package freemarker.log;
/*    */ 
/*    */ import org.apache.log4j.MDC;
/*    */ import org.slf4j.MDC;
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
/*    */ public class _Log4jOverSLF4JTester
/*    */ {
/* 28 */   private static final String MDC_KEY = _Log4jOverSLF4JTester.class.getName();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static final boolean test() {
/* 35 */     MDC.put(MDC_KEY, "");
/*    */     try {
/* 37 */       return (MDC.get(MDC_KEY) != null);
/*    */     } finally {
/* 39 */       MDC.remove(MDC_KEY);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\log\_Log4jOverSLF4JTester.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */