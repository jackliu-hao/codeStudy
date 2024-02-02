/*    */ package org.noear.solon.logging.appender;
/*    */ 
/*    */ import java.io.Console;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConsoleAppender
/*    */   extends OutputStreamAppender
/*    */ {
/*    */   public ConsoleAppender() {
/* 16 */     Console console = System.console();
/*    */     
/* 18 */     if (console != null) {
/* 19 */       setOutput(console.writer());
/*    */     } else {
/* 21 */       setOutput(System.out);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\appender\ConsoleAppender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */