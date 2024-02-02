/*    */ package cn.hutool.core.compiler;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.stream.Collectors;
/*    */ import javax.tools.DiagnosticCollector;
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
/*    */ public class DiagnosticUtil
/*    */ {
/*    */   public static String getMessages(DiagnosticCollector<?> collector) {
/* 22 */     List<?> diagnostics = collector.getDiagnostics();
/* 23 */     return diagnostics.stream().map(String::valueOf)
/* 24 */       .collect(Collectors.joining(System.lineSeparator()));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\DiagnosticUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */