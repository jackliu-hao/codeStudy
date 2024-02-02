/*    */ package freemarker.debug.impl;
/*    */ 
/*    */ import freemarker.debug.Breakpoint;
/*    */ import freemarker.debug.Debugger;
/*    */ import freemarker.debug.DebuggerListener;
/*    */ import java.rmi.RemoteException;
/*    */ import java.rmi.server.UnicastRemoteObject;
/*    */ import java.util.Collection;
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
/*    */ class RmiDebuggerImpl
/*    */   extends UnicastRemoteObject
/*    */   implements Debugger
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final RmiDebuggerService service;
/*    */   
/*    */   protected RmiDebuggerImpl(RmiDebuggerService service) throws RemoteException {
/* 43 */     this.service = service;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addBreakpoint(Breakpoint breakpoint) {
/* 48 */     this.service.addBreakpoint(breakpoint);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object addDebuggerListener(DebuggerListener listener) {
/* 53 */     return this.service.addDebuggerListener(listener);
/*    */   }
/*    */ 
/*    */   
/*    */   public List getBreakpoints() {
/* 58 */     return this.service.getBreakpointsSpi();
/*    */   }
/*    */ 
/*    */   
/*    */   public List getBreakpoints(String templateName) {
/* 63 */     return this.service.getBreakpointsSpi(templateName);
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection getSuspendedEnvironments() {
/* 68 */     return this.service.getSuspendedEnvironments();
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeBreakpoint(Breakpoint breakpoint) {
/* 73 */     this.service.removeBreakpoint(breakpoint);
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeDebuggerListener(Object id) {
/* 78 */     this.service.removeDebuggerListener(id);
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeBreakpoints() {
/* 83 */     this.service.removeBreakpoints();
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeBreakpoints(String templateName) {
/* 88 */     this.service.removeBreakpoints(templateName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebuggerImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */