/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.debug.impl.DebuggerService;
/*    */ import freemarker.template.TemplateException;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ @Deprecated
/*    */ public class DebugBreak
/*    */   extends TemplateElement
/*    */ {
/*    */   public DebugBreak(TemplateElement nestedBlock) {
/* 36 */     addChild(nestedBlock);
/* 37 */     copyLocationFrom(nestedBlock);
/*    */   }
/*    */ 
/*    */   
/*    */   protected TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 42 */     if (!DebuggerService.suspendEnvironment(env, 
/* 43 */         getTemplate().getSourceName(), getChild(0).getBeginLine())) {
/* 44 */       return getChild(0).accept(env);
/*    */     }
/* 46 */     throw new StopException(env, "Stopped by debugger");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 52 */     if (canonical) {
/* 53 */       StringBuilder sb = new StringBuilder();
/* 54 */       sb.append("<#-- ");
/* 55 */       sb.append("debug break");
/* 56 */       if (getChildCount() == 0) {
/* 57 */         sb.append(" /-->");
/*    */       } else {
/* 59 */         sb.append(" -->");
/* 60 */         sb.append(getChild(0).getCanonicalForm());
/* 61 */         sb.append("<#--/ debug break -->");
/*    */       } 
/* 63 */       return sb.toString();
/*    */     } 
/* 65 */     return "debug break";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 71 */     return "#debug_break";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 76 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 81 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 86 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 91 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\DebugBreak.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */