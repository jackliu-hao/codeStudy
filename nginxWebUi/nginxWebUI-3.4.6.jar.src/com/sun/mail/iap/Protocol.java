/*     */ package com.sun.mail.iap;
/*     */ 
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import com.sun.mail.util.SocketFetcher;
/*     */ import com.sun.mail.util.TraceInputStream;
/*     */ import com.sun.mail.util.TraceOutputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import java.util.logging.Level;
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
/*     */ public class Protocol
/*     */ {
/*     */   protected String host;
/*     */   private Socket socket;
/*     */   protected boolean quote;
/*     */   protected MailLogger logger;
/*     */   protected MailLogger traceLogger;
/*     */   protected Properties props;
/*     */   protected String prefix;
/*     */   private boolean connected = false;
/*     */   private TraceInputStream traceInput;
/*     */   private volatile ResponseInputStream input;
/*     */   private TraceOutputStream traceOutput;
/*     */   private volatile DataOutputStream output;
/*  78 */   private int tagCounter = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String localHostName;
/*     */ 
/*     */ 
/*     */   
/*  87 */   private final Vector handlers = new Vector();
/*     */   
/*     */   private volatile long timestamp;
/*     */   
/*  91 */   private static final byte[] CRLF = new byte[] { 13, 10 };
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
/*     */   public Protocol(String host, int port, Properties props, String prefix, boolean isSSL, MailLogger logger) throws IOException, ProtocolException {
/*     */     try {
/* 110 */       this.host = host;
/* 111 */       this.props = props;
/* 112 */       this.prefix = prefix;
/* 113 */       this.logger = logger;
/* 114 */       this.traceLogger = logger.getSubLogger("protocol", null);
/*     */       
/* 116 */       this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
/* 117 */       this.quote = PropUtil.getBooleanProperty(props, "mail.debug.quote", false);
/*     */ 
/*     */       
/* 120 */       initStreams();
/*     */ 
/*     */       
/* 123 */       processGreeting(readResponse());
/*     */       
/* 125 */       this.timestamp = System.currentTimeMillis();
/*     */       
/* 127 */       this.connected = true;
/*     */ 
/*     */     
/*     */     }
/*     */     finally {
/*     */ 
/*     */ 
/*     */       
/* 135 */       if (!this.connected)
/* 136 */         disconnect(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initStreams() throws IOException {
/* 141 */     this.traceInput = new TraceInputStream(this.socket.getInputStream(), this.traceLogger);
/* 142 */     this.traceInput.setQuote(this.quote);
/* 143 */     this.input = new ResponseInputStream((InputStream)this.traceInput);
/*     */     
/* 145 */     this.traceOutput = new TraceOutputStream(this.socket.getOutputStream(), this.traceLogger);
/*     */     
/* 147 */     this.traceOutput.setQuote(this.quote);
/* 148 */     this.output = new DataOutputStream(new BufferedOutputStream((OutputStream)this.traceOutput));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Protocol(InputStream in, PrintStream out, boolean debug) throws IOException {
/* 156 */     this.host = "localhost";
/* 157 */     this.quote = false;
/* 158 */     this.logger = new MailLogger(getClass(), "DEBUG", debug, out);
/* 159 */     this.traceLogger = this.logger.getSubLogger("protocol", null);
/*     */ 
/*     */     
/* 162 */     this.traceInput = new TraceInputStream(in, this.traceLogger);
/* 163 */     this.traceInput.setQuote(this.quote);
/* 164 */     this.input = new ResponseInputStream((InputStream)this.traceInput);
/*     */     
/* 166 */     this.traceOutput = new TraceOutputStream(out, this.traceLogger);
/* 167 */     this.traceOutput.setQuote(this.quote);
/* 168 */     this.output = new DataOutputStream(new BufferedOutputStream((OutputStream)this.traceOutput));
/*     */     
/* 170 */     this.timestamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTimestamp() {
/* 178 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addResponseHandler(ResponseHandler h) {
/* 185 */     this.handlers.addElement(h);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeResponseHandler(ResponseHandler h) {
/* 192 */     this.handlers.removeElement(h);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyResponseHandlers(Response[] responses) {
/* 199 */     if (this.handlers.size() == 0) {
/*     */       return;
/*     */     }
/* 202 */     for (int i = 0; i < responses.length; i++) {
/* 203 */       Response r = responses[i];
/*     */ 
/*     */       
/* 206 */       if (r != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 211 */         Object[] h = this.handlers.toArray();
/*     */ 
/*     */         
/* 214 */         for (int j = 0; j < h.length; j++) {
/* 215 */           if (h[j] != null)
/* 216 */             ((ResponseHandler)h[j]).handleResponse(r); 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   protected void processGreeting(Response r) throws ProtocolException {
/* 222 */     if (r.isBYE()) {
/* 223 */       throw new ConnectionException(this, r);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ResponseInputStream getInputStream() {
/* 230 */     return this.input;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected OutputStream getOutputStream() {
/* 237 */     return this.output;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized boolean supportsNonSyncLiterals() {
/* 245 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Response readResponse() throws IOException, ProtocolException {
/* 250 */     return new Response(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteArray getResponseBuffer() {
/* 261 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String writeCommand(String command, Argument args) throws IOException, ProtocolException {
/* 268 */     String tag = "A" + Integer.toString(this.tagCounter++, 10);
/*     */     
/* 270 */     this.output.writeBytes(tag + " " + command);
/*     */     
/* 272 */     if (args != null) {
/* 273 */       this.output.write(32);
/* 274 */       args.write(this);
/*     */     } 
/*     */     
/* 277 */     this.output.write(CRLF);
/* 278 */     this.output.flush();
/* 279 */     return tag;
/*     */   }
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
/*     */   public synchronized Response[] command(String command, Argument args) {
/* 292 */     commandStart(command);
/* 293 */     Vector v = new Vector();
/* 294 */     boolean done = false;
/* 295 */     String tag = null;
/* 296 */     Response r = null;
/*     */ 
/*     */     
/*     */     try {
/* 300 */       tag = writeCommand(command, args);
/* 301 */     } catch (LiteralException lex) {
/* 302 */       v.addElement(lex.getResponse());
/* 303 */       done = true;
/* 304 */     } catch (Exception ex) {
/*     */       
/* 306 */       v.addElement(Response.byeResponse(ex));
/* 307 */       done = true;
/*     */     } 
/*     */     
/* 310 */     Response byeResp = null;
/* 311 */     while (!done) {
/*     */       try {
/* 313 */         r = readResponse();
/* 314 */       } catch (IOException ioex) {
/* 315 */         if (byeResp != null) {
/*     */           break;
/*     */         }
/* 318 */         r = Response.byeResponse(ioex);
/* 319 */       } catch (ProtocolException pex) {
/*     */         continue;
/*     */       } 
/*     */       
/* 323 */       if (r.isBYE()) {
/* 324 */         byeResp = r;
/*     */         
/*     */         continue;
/*     */       } 
/* 328 */       v.addElement(r);
/*     */ 
/*     */       
/* 331 */       if (r.isTagged() && r.getTag().equals(tag)) {
/* 332 */         done = true;
/*     */       }
/*     */     } 
/* 335 */     if (byeResp != null)
/* 336 */       v.addElement(byeResp); 
/* 337 */     Response[] responses = new Response[v.size()];
/* 338 */     v.copyInto((Object[])responses);
/* 339 */     this.timestamp = System.currentTimeMillis();
/* 340 */     commandEnd();
/* 341 */     return responses;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleResult(Response response) throws ProtocolException {
/* 348 */     if (response.isOK())
/*     */       return; 
/* 350 */     if (response.isNO())
/* 351 */       throw new CommandFailedException(response); 
/* 352 */     if (response.isBAD())
/* 353 */       throw new BadCommandException(response); 
/* 354 */     if (response.isBYE()) {
/* 355 */       disconnect();
/* 356 */       throw new ConnectionException(this, response);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void simpleCommand(String cmd, Argument args) throws ProtocolException {
/* 367 */     Response[] r = command(cmd, args);
/*     */ 
/*     */     
/* 370 */     notifyResponseHandlers(r);
/*     */ 
/*     */     
/* 373 */     handleResult(r[r.length - 1]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void startTLS(String cmd) throws IOException, ProtocolException {
/* 385 */     if (this.socket instanceof javax.net.ssl.SSLSocket)
/*     */       return; 
/* 387 */     simpleCommand(cmd, null);
/* 388 */     this.socket = SocketFetcher.startTLS(this.socket, this.host, this.props, this.prefix);
/* 389 */     initStreams();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSSL() {
/* 399 */     return this.socket instanceof javax.net.ssl.SSLSocket;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized void disconnect() {
/* 406 */     if (this.socket != null) {
/*     */       try {
/* 408 */         this.socket.close();
/* 409 */       } catch (IOException e) {}
/*     */ 
/*     */       
/* 412 */       this.socket = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized String getLocalHost() {
/* 423 */     if (this.localHostName == null || this.localHostName.length() <= 0) {
/* 424 */       this.localHostName = this.props.getProperty(this.prefix + ".localhost");
/*     */     }
/* 426 */     if (this.localHostName == null || this.localHostName.length() <= 0) {
/* 427 */       this.localHostName = this.props.getProperty(this.prefix + ".localaddress");
/*     */     }
/*     */     try {
/* 430 */       if (this.localHostName == null || this.localHostName.length() <= 0) {
/* 431 */         InetAddress localHost = InetAddress.getLocalHost();
/* 432 */         this.localHostName = localHost.getCanonicalHostName();
/*     */         
/* 434 */         if (this.localHostName == null)
/*     */         {
/* 436 */           this.localHostName = "[" + localHost.getHostAddress() + "]"; } 
/*     */       } 
/* 438 */     } catch (UnknownHostException uhex) {}
/*     */ 
/*     */ 
/*     */     
/* 442 */     if ((this.localHostName == null || this.localHostName.length() <= 0) && 
/* 443 */       this.socket != null && this.socket.isBound()) {
/* 444 */       InetAddress localHost = this.socket.getLocalAddress();
/* 445 */       this.localHostName = localHost.getCanonicalHostName();
/*     */       
/* 447 */       if (this.localHostName == null)
/*     */       {
/* 449 */         this.localHostName = "[" + localHost.getHostAddress() + "]";
/*     */       }
/*     */     } 
/* 452 */     return this.localHostName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isTracing() {
/* 459 */     return this.traceLogger.isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void suspendTracing() {
/* 467 */     if (this.traceLogger.isLoggable(Level.FINEST)) {
/* 468 */       this.traceInput.setTrace(false);
/* 469 */       this.traceOutput.setTrace(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void resumeTracing() {
/* 477 */     if (this.traceLogger.isLoggable(Level.FINEST)) {
/* 478 */       this.traceInput.setTrace(true);
/* 479 */       this.traceOutput.setTrace(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 487 */     super.finalize();
/* 488 */     disconnect();
/*     */   }
/*     */   
/*     */   private void commandStart(String command) {}
/*     */   
/*     */   private void commandEnd() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\Protocol.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */