/*    */ package ch.qos.logback.classic.html;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.spi.IThrowableProxy;
/*    */ import ch.qos.logback.classic.spi.StackTraceElementProxy;
/*    */ import ch.qos.logback.core.CoreConstants;
/*    */ import ch.qos.logback.core.helpers.Transform;
/*    */ import ch.qos.logback.core.html.IThrowableRenderer;
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
/*    */ public class DefaultThrowableRenderer
/*    */   implements IThrowableRenderer<ILoggingEvent>
/*    */ {
/*    */   static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";
/*    */   
/*    */   public void render(StringBuilder sbuf, ILoggingEvent event) {
/* 28 */     IThrowableProxy tp = event.getThrowableProxy();
/* 29 */     sbuf.append("<tr><td class=\"Exception\" colspan=\"6\">");
/* 30 */     while (tp != null) {
/* 31 */       render(sbuf, tp);
/* 32 */       tp = tp.getCause();
/*    */     } 
/* 34 */     sbuf.append("</td></tr>");
/*    */   }
/*    */   
/*    */   void render(StringBuilder sbuf, IThrowableProxy tp) {
/* 38 */     printFirstLine(sbuf, tp);
/*    */     
/* 40 */     int commonFrames = tp.getCommonFrames();
/* 41 */     StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
/*    */     
/* 43 */     for (int i = 0; i < stepArray.length - commonFrames; i++) {
/* 44 */       StackTraceElementProxy step = stepArray[i];
/* 45 */       sbuf.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;");
/* 46 */       sbuf.append(Transform.escapeTags(step.toString()));
/* 47 */       sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     } 
/*    */     
/* 50 */     if (commonFrames > 0) {
/* 51 */       sbuf.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;");
/* 52 */       sbuf.append("\t... ").append(commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void printFirstLine(StringBuilder sb, IThrowableProxy tp) {
/* 57 */     int commonFrames = tp.getCommonFrames();
/* 58 */     if (commonFrames > 0) {
/* 59 */       sb.append("<br />").append("Caused by: ");
/*    */     }
/* 61 */     sb.append(tp.getClassName()).append(": ").append(Transform.escapeTags(tp.getMessage()));
/* 62 */     sb.append(CoreConstants.LINE_SEPARATOR);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\html\DefaultThrowableRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */