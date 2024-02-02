/*    */ package freemarker.debug;
/*    */ 
/*    */ import java.util.EventObject;
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
/*    */ public class EnvironmentSuspendedEvent
/*    */   extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final String name;
/*    */   private final int line;
/*    */   private final DebuggedEnvironment env;
/*    */   
/*    */   public EnvironmentSuspendedEvent(Object source, String templateName, int line, DebuggedEnvironment env) {
/* 36 */     super(source);
/* 37 */     this.name = templateName;
/* 38 */     this.line = line;
/* 39 */     this.env = env;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 48 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLine() {
/* 57 */     return this.line;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DebuggedEnvironment getEnvironment() {
/* 65 */     return this.env;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\EnvironmentSuspendedEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */