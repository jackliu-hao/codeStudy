/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
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
/*    */ public class StopException
/*    */   extends TemplateException
/*    */ {
/*    */   StopException(Environment env) {
/* 33 */     super(env);
/*    */   }
/*    */   
/*    */   StopException(Environment env, String s) {
/* 37 */     super(s, env);
/*    */   }
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintWriter pw) {
/* 42 */     synchronized (pw) {
/* 43 */       String msg = getMessage();
/* 44 */       pw.print("Encountered stop instruction");
/* 45 */       if (msg != null && !msg.equals(""))
/* 46 */       { pw.println("\nCause given: " + msg); }
/* 47 */       else { pw.println(); }
/* 48 */        super.printStackTrace(pw);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintStream ps) {
/* 54 */     synchronized (ps) {
/* 55 */       String msg = getMessage();
/* 56 */       ps.print("Encountered stop instruction");
/* 57 */       if (msg != null && !msg.equals(""))
/* 58 */       { ps.println("\nCause given: " + msg); }
/* 59 */       else { ps.println(); }
/* 60 */        super.printStackTrace(ps);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\StopException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */