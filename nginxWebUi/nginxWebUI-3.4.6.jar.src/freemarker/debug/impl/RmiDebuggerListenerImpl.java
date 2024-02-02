/*    */ package freemarker.debug.impl;
/*    */ 
/*    */ import freemarker.debug.DebuggerListener;
/*    */ import freemarker.debug.EnvironmentSuspendedEvent;
/*    */ import freemarker.log.Logger;
/*    */ import java.rmi.NoSuchObjectException;
/*    */ import java.rmi.RemoteException;
/*    */ import java.rmi.server.UnicastRemoteObject;
/*    */ import java.rmi.server.Unreferenced;
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
/*    */ 
/*    */ public class RmiDebuggerListenerImpl
/*    */   extends UnicastRemoteObject
/*    */   implements DebuggerListener, Unreferenced
/*    */ {
/* 40 */   private static final Logger LOG = Logger.getLogger("freemarker.debug.client");
/*    */ 
/*    */   
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   private final DebuggerListener listener;
/*    */ 
/*    */   
/*    */   public void unreferenced() {
/*    */     try {
/* 50 */       UnicastRemoteObject.unexportObject(this, false);
/* 51 */     } catch (NoSuchObjectException e) {
/* 52 */       LOG.warn("Failed to unexport RMI debugger listener", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public RmiDebuggerListenerImpl(DebuggerListener listener) throws RemoteException {
/* 58 */     this.listener = listener;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void environmentSuspended(EnvironmentSuspendedEvent e) throws RemoteException {
/* 64 */     this.listener.environmentSuspended(e);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebuggerListenerImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */