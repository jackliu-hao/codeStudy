/*     */ package ch.qos.logback.classic.net;
/*     */ 
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.classic.joran.JoranConfigurator;
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import javax.net.ServerSocketFactory;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class SimpleSocketServer
/*     */   extends Thread
/*     */ {
/*  52 */   Logger logger = LoggerFactory.getLogger(SimpleSocketServer.class);
/*     */   
/*     */   private final int port;
/*     */   private final LoggerContext lc;
/*     */   private boolean closed = false;
/*     */   private ServerSocket serverSocket;
/*  58 */   private List<SocketNode> socketNodeList = new ArrayList<SocketNode>();
/*     */   
/*     */   private CountDownLatch latch;
/*     */ 
/*     */   
/*     */   public static void main(String[] argv) throws Exception {
/*  64 */     doMain(SimpleSocketServer.class, argv);
/*     */   }
/*     */   
/*     */   protected static void doMain(Class<? extends SimpleSocketServer> serverClass, String[] argv) throws Exception {
/*  68 */     int port = -1;
/*  69 */     if (argv.length == 2) {
/*  70 */       port = parsePortNumber(argv[0]);
/*     */     } else {
/*  72 */       usage("Wrong number of arguments.");
/*     */     } 
/*     */     
/*  75 */     String configFile = argv[1];
/*  76 */     LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
/*  77 */     configureLC(lc, configFile);
/*     */     
/*  79 */     SimpleSocketServer sss = new SimpleSocketServer(lc, port);
/*  80 */     sss.start();
/*     */   }
/*     */   
/*     */   public SimpleSocketServer(LoggerContext lc, int port) {
/*  84 */     this.lc = lc;
/*  85 */     this.port = port;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*  90 */     String oldThreadName = Thread.currentThread().getName();
/*     */ 
/*     */     
/*     */     try {
/*  94 */       String newThreadName = getServerThreadName();
/*  95 */       Thread.currentThread().setName(newThreadName);
/*     */       
/*  97 */       this.logger.info("Listening on port " + this.port);
/*  98 */       this.serverSocket = getServerSocketFactory().createServerSocket(this.port);
/*  99 */       while (!this.closed) {
/* 100 */         this.logger.info("Waiting to accept a new client.");
/* 101 */         signalAlmostReadiness();
/* 102 */         Socket socket = this.serverSocket.accept();
/* 103 */         this.logger.info("Connected to client at " + socket.getInetAddress());
/* 104 */         this.logger.info("Starting new socket node.");
/* 105 */         SocketNode newSocketNode = new SocketNode(this, socket, this.lc);
/* 106 */         synchronized (this.socketNodeList) {
/* 107 */           this.socketNodeList.add(newSocketNode);
/*     */         } 
/* 109 */         String clientThreadName = getClientThreadName(socket);
/* 110 */         (new Thread(newSocketNode, clientThreadName)).start();
/*     */       } 
/* 112 */     } catch (Exception e) {
/* 113 */       if (this.closed) {
/* 114 */         this.logger.info("Exception in run method for a closed server. This is normal.");
/*     */       } else {
/* 116 */         this.logger.error("Unexpected failure in run method", e);
/*     */       }
/*     */     
/*     */     } finally {
/*     */       
/* 121 */       Thread.currentThread().setName(oldThreadName);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getServerThreadName() {
/* 129 */     return String.format("Logback %s (port %d)", new Object[] { getClass().getSimpleName(), Integer.valueOf(this.port) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getClientThreadName(Socket socket) {
/* 136 */     return String.format("Logback SocketNode (client: %s)", new Object[] { socket.getRemoteSocketAddress() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServerSocketFactory getServerSocketFactory() {
/* 145 */     return ServerSocketFactory.getDefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void signalAlmostReadiness() {
/* 153 */     if (this.latch != null && this.latch.getCount() != 0L)
/*     */     {
/* 155 */       this.latch.countDown();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setLatch(CountDownLatch latch) {
/* 164 */     this.latch = latch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CountDownLatch getLatch() {
/* 171 */     return this.latch;
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 175 */     return this.closed;
/*     */   }
/*     */   
/*     */   public void close() {
/* 179 */     this.closed = true;
/* 180 */     if (this.serverSocket != null) {
/*     */       try {
/* 182 */         this.serverSocket.close();
/* 183 */       } catch (IOException e) {
/* 184 */         this.logger.error("Failed to close serverSocket", e);
/*     */       } finally {
/* 186 */         this.serverSocket = null;
/*     */       } 
/*     */     }
/*     */     
/* 190 */     this.logger.info("closing this server");
/* 191 */     synchronized (this.socketNodeList) {
/* 192 */       for (SocketNode sn : this.socketNodeList) {
/* 193 */         sn.close();
/*     */       }
/*     */     } 
/* 196 */     if (this.socketNodeList.size() != 0) {
/* 197 */       this.logger.warn("Was expecting a 0-sized socketNodeList after server shutdown");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void socketNodeClosing(SocketNode sn) {
/* 203 */     this.logger.debug("Removing {}", sn);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 208 */     synchronized (this.socketNodeList) {
/* 209 */       this.socketNodeList.remove(sn);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void usage(String msg) {
/* 214 */     System.err.println(msg);
/* 215 */     System.err.println("Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
/* 216 */     System.exit(1);
/*     */   }
/*     */   
/*     */   static int parsePortNumber(String portStr) {
/*     */     try {
/* 221 */       return Integer.parseInt(portStr);
/* 222 */     } catch (NumberFormatException e) {
/* 223 */       e.printStackTrace();
/* 224 */       usage("Could not interpret port number [" + portStr + "].");
/*     */       
/* 226 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void configureLC(LoggerContext lc, String configFile) throws JoranException {
/* 231 */     JoranConfigurator configurator = new JoranConfigurator();
/* 232 */     lc.reset();
/* 233 */     configurator.setContext((Context)lc);
/* 234 */     configurator.doConfigure(configFile);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SimpleSocketServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */