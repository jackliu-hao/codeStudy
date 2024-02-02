/*    */ package ch.qos.logback.classic.html;
/*    */ 
/*    */ import ch.qos.logback.core.CoreConstants;
/*    */ import ch.qos.logback.core.html.CssBuilder;
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
/*    */ public class DefaultCssBuilder
/*    */   implements CssBuilder
/*    */ {
/*    */   public void addCss(StringBuilder sbuf) {
/* 30 */     sbuf.append("<style  type=\"text/css\">");
/* 31 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 32 */     sbuf.append("table { margin-left: 2em; margin-right: 2em; border-left: 2px solid #AAA; }");
/* 33 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 35 */     sbuf.append("TR.even { background: #FFFFFF; }");
/* 36 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 38 */     sbuf.append("TR.odd { background: #EAEAEA; }");
/* 39 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 41 */     sbuf.append("TR.warn TD.Level, TR.error TD.Level, TR.fatal TD.Level {font-weight: bold; color: #FF4040 }");
/* 42 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 44 */     sbuf.append("TD { padding-right: 1ex; padding-left: 1ex; border-right: 2px solid #AAA; }");
/* 45 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 47 */     sbuf.append("TD.Time, TD.Date { text-align: right; font-family: courier, monospace; font-size: smaller; }");
/* 48 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 50 */     sbuf.append("TD.Thread { text-align: left; }");
/* 51 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 53 */     sbuf.append("TD.Level { text-align: right; }");
/* 54 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 56 */     sbuf.append("TD.Logger { text-align: left; }");
/* 57 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 59 */     sbuf.append("TR.header { background: #596ED5; color: #FFF; font-weight: bold; font-size: larger; }");
/* 60 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 62 */     sbuf.append("TD.Exception { background: #A2AEE8; font-family: courier, monospace;}");
/* 63 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */     
/* 65 */     sbuf.append("</style>");
/* 66 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\html\DefaultCssBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */