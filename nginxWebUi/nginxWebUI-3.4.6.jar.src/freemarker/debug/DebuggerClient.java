/*     */ package freemarker.debug;
/*     */ 
/*     */ import freemarker.debug.impl.RmiDebuggerListenerImpl;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.rmi.RemoteException;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DebuggerClient
/*     */ {
/*     */   public static Debugger getDebugger(InetAddress host, int port, String password) throws IOException {
/*  63 */     try (Socket s = new Socket(host, port)) {
/*  64 */       ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
/*  65 */       ObjectInputStream in = new ObjectInputStream(s.getInputStream());
/*  66 */       int protocolVersion = in.readInt();
/*  67 */       if (protocolVersion > 220) {
/*  68 */         throw new IOException("Incompatible protocol version " + protocolVersion + ". At most 220 was expected.");
/*     */       }
/*     */ 
/*     */       
/*  72 */       byte[] challenge = (byte[])in.readObject();
/*  73 */       MessageDigest md = MessageDigest.getInstance("SHA");
/*  74 */       md.update(password.getBytes("UTF-8"));
/*  75 */       md.update(challenge);
/*  76 */       out.writeObject(md.digest());
/*  77 */       return new LocalDebuggerProxy((Debugger)in.readObject());
/*     */     
/*     */     }
/*  80 */     catch (IOException e) {
/*  81 */       throw e;
/*  82 */     } catch (Exception e) {
/*  83 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class LocalDebuggerProxy implements Debugger {
/*     */     private final Debugger remoteDebugger;
/*     */     
/*     */     LocalDebuggerProxy(Debugger remoteDebugger) {
/*  91 */       this.remoteDebugger = remoteDebugger;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addBreakpoint(Breakpoint breakpoint) throws RemoteException {
/*  96 */       this.remoteDebugger.addBreakpoint(breakpoint);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object addDebuggerListener(DebuggerListener listener) throws RemoteException {
/* 102 */       if (listener instanceof java.rmi.server.RemoteObject) {
/* 103 */         return this.remoteDebugger.addDebuggerListener(listener);
/*     */       }
/* 105 */       RmiDebuggerListenerImpl remotableListener = new RmiDebuggerListenerImpl(listener);
/*     */       
/* 107 */       return this.remoteDebugger.addDebuggerListener((DebuggerListener)remotableListener);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public List getBreakpoints() throws RemoteException {
/* 113 */       return this.remoteDebugger.getBreakpoints();
/*     */     }
/*     */ 
/*     */     
/*     */     public List getBreakpoints(String templateName) throws RemoteException {
/* 118 */       return this.remoteDebugger.getBreakpoints(templateName);
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection getSuspendedEnvironments() throws RemoteException {
/* 123 */       return this.remoteDebugger.getSuspendedEnvironments();
/*     */     }
/*     */ 
/*     */     
/*     */     public void removeBreakpoint(Breakpoint breakpoint) throws RemoteException {
/* 128 */       this.remoteDebugger.removeBreakpoint(breakpoint);
/*     */     }
/*     */ 
/*     */     
/*     */     public void removeBreakpoints(String templateName) throws RemoteException {
/* 133 */       this.remoteDebugger.removeBreakpoints(templateName);
/*     */     }
/*     */ 
/*     */     
/*     */     public void removeBreakpoints() throws RemoteException {
/* 138 */       this.remoteDebugger.removeBreakpoints();
/*     */     }
/*     */ 
/*     */     
/*     */     public void removeDebuggerListener(Object id) throws RemoteException {
/* 143 */       this.remoteDebugger.removeDebuggerListener(id);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\DebuggerClient.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */