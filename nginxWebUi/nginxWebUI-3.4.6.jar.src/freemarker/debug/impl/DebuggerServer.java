/*     */ package freemarker.debug.impl;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.utility.SecurityUtilities;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
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
/*     */ class DebuggerServer
/*     */ {
/*  42 */   private static final Logger LOG = Logger.getLogger("freemarker.debug.server");
/*     */   
/*  44 */   private static final Random R = new SecureRandom();
/*     */   
/*     */   private final byte[] password;
/*     */   private final int port;
/*     */   private final Serializable debuggerStub;
/*     */   private boolean stop = false;
/*     */   private ServerSocket serverSocket;
/*     */   
/*     */   public DebuggerServer(Serializable debuggerStub) {
/*  53 */     this.port = SecurityUtilities.getSystemProperty("freemarker.debug.port", 7011).intValue();
/*     */     try {
/*  55 */       this.password = SecurityUtilities.getSystemProperty("freemarker.debug.password", "").getBytes("UTF-8");
/*  56 */     } catch (UnsupportedEncodingException e) {
/*  57 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*  59 */     this.debuggerStub = debuggerStub;
/*     */   }
/*     */   
/*     */   public void start() {
/*  63 */     (new Thread(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/*  67 */             DebuggerServer.this.startInternal();
/*     */           }
/*  69 */         },  "FreeMarker Debugger Server Acceptor")).start();
/*     */   }
/*     */   
/*     */   private void startInternal() {
/*     */     try {
/*  74 */       this.serverSocket = new ServerSocket(this.port);
/*  75 */       while (!this.stop) {
/*  76 */         Socket s = this.serverSocket.accept();
/*  77 */         (new Thread(new DebuggerAuthProtocol(s))).start();
/*     */       } 
/*  79 */     } catch (IOException e) {
/*  80 */       LOG.error("Debugger server shut down.", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private class DebuggerAuthProtocol implements Runnable {
/*     */     private final Socket s;
/*     */     
/*     */     DebuggerAuthProtocol(Socket s) {
/*  88 */       this.s = s;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/*  94 */         ObjectOutputStream out = new ObjectOutputStream(this.s.getOutputStream());
/*  95 */         ObjectInputStream in = new ObjectInputStream(this.s.getInputStream());
/*  96 */         byte[] challenge = new byte[512];
/*  97 */         DebuggerServer.R.nextBytes(challenge);
/*  98 */         out.writeInt(220);
/*  99 */         out.writeObject(challenge);
/* 100 */         MessageDigest md = MessageDigest.getInstance("SHA");
/* 101 */         md.update(DebuggerServer.this.password);
/* 102 */         md.update(challenge);
/* 103 */         byte[] response = (byte[])in.readObject();
/* 104 */         if (Arrays.equals(response, md.digest())) {
/* 105 */           out.writeObject(DebuggerServer.this.debuggerStub);
/*     */         } else {
/* 107 */           out.writeObject(null);
/*     */         } 
/* 109 */       } catch (Exception e) {
/* 110 */         DebuggerServer.LOG.warn("Connection to " + this.s.getInetAddress().getHostAddress() + " abruply broke", e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 117 */     this.stop = true;
/* 118 */     if (this.serverSocket != null)
/*     */       try {
/* 120 */         this.serverSocket.close();
/* 121 */       } catch (IOException e) {
/* 122 */         LOG.error("Unable to close server socket.", e);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\DebuggerServer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */