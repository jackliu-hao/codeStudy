/*    */ package freemarker.debug.impl;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ import freemarker.template.Template;
/*    */ import freemarker.template.utility.SecurityUtilities;
/*    */ import java.rmi.RemoteException;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ public abstract class DebuggerService
/*    */ {
/* 36 */   private static final DebuggerService instance = createInstance();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static DebuggerService createInstance() {
/* 42 */     return 
/* 43 */       (SecurityUtilities.getSystemProperty("freemarker.debug.password", null) == null) ? new NoOpDebuggerService() : new RmiDebuggerService();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static List getBreakpoints(String templateName) {
/* 49 */     return instance.getBreakpointsSpi(templateName);
/*    */   }
/*    */   
/*    */   abstract List getBreakpointsSpi(String paramString);
/*    */   
/*    */   public static void registerTemplate(Template template) {
/* 55 */     instance.registerTemplateSpi(template);
/*    */   }
/*    */ 
/*    */   
/*    */   abstract void registerTemplateSpi(Template paramTemplate);
/*    */   
/*    */   public static boolean suspendEnvironment(Environment env, String templateName, int line) throws RemoteException {
/* 62 */     return instance.suspendEnvironmentSpi(env, templateName, line);
/*    */   }
/*    */ 
/*    */   
/*    */   abstract boolean suspendEnvironmentSpi(Environment paramEnvironment, String paramString, int paramInt) throws RemoteException;
/*    */   
/*    */   abstract void shutdownSpi();
/*    */   
/*    */   public static void shutdown() {
/* 71 */     instance.shutdownSpi();
/*    */   }
/*    */   
/*    */   private static class NoOpDebuggerService
/*    */     extends DebuggerService {
/*    */     List getBreakpointsSpi(String templateName) {
/* 77 */       return Collections.EMPTY_LIST;
/*    */     }
/*    */     private NoOpDebuggerService() {}
/*    */     
/*    */     boolean suspendEnvironmentSpi(Environment env, String templateName, int line) {
/* 82 */       throw new UnsupportedOperationException();
/*    */     }
/*    */     
/*    */     void registerTemplateSpi(Template template) {}
/*    */     
/*    */     void shutdownSpi() {}
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\DebuggerService.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */