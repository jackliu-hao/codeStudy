/*     */ package com.sun.mail.pop3;
/*     */ 
/*     */ import com.sun.mail.util.LineInputStream;
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import com.sun.mail.util.SharedByteArrayOutputStream;
/*     */ import com.sun.mail.util.SocketFetcher;
/*     */ import com.sun.mail.util.TraceInputStream;
/*     */ import com.sun.mail.util.TraceOutputStream;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
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
/*     */ class Protocol
/*     */ {
/*     */   private Socket socket;
/*     */   private String host;
/*     */   private Properties props;
/*     */   private String prefix;
/*     */   private DataInputStream input;
/*     */   private PrintWriter output;
/*     */   private TraceInputStream traceInput;
/*     */   private TraceOutputStream traceOutput;
/*     */   private MailLogger logger;
/*     */   private MailLogger traceLogger;
/*     */   private String apopChallenge;
/*     */   private Map capabilities;
/*     */   private boolean pipelining;
/*     */   private boolean noauthdebug;
/*     */   private boolean traceSuspended;
/*     */   private static final int POP3_PORT = 110;
/*     */   private static final String CRLF = "\r\n";
/*     */   private static final int SLOP = 128;
/*     */   
/*     */   Protocol(String host, int port, MailLogger logger, Properties props, String prefix, boolean isSSL) throws IOException {
/*     */     Response r;
/*  77 */     this.apopChallenge = null;
/*  78 */     this.capabilities = null;
/*     */     
/*  80 */     this.noauthdebug = true;
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
/*  94 */     this.host = host;
/*  95 */     this.props = props;
/*  96 */     this.prefix = prefix;
/*  97 */     this.logger = logger;
/*  98 */     this.traceLogger = logger.getSubLogger("protocol", null);
/*  99 */     this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
/*     */ 
/*     */ 
/*     */     
/* 103 */     boolean enableAPOP = getBoolProp(props, prefix + ".apop.enable");
/* 104 */     boolean disableCapa = getBoolProp(props, prefix + ".disablecapa");
/*     */     try {
/* 106 */       if (port == -1)
/* 107 */         port = 110; 
/* 108 */       if (logger.isLoggable(Level.FINE)) {
/* 109 */         logger.fine("connecting to host \"" + host + "\", port " + port + ", isSSL " + isSSL);
/*     */       }
/*     */       
/* 112 */       this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
/* 113 */       initStreams();
/* 114 */       r = simpleCommand(null);
/* 115 */     } catch (IOException ioe) {
/*     */       try {
/* 117 */         this.socket.close();
/*     */       } finally {
/* 119 */         throw ioe;
/*     */       } 
/*     */     } 
/*     */     
/* 123 */     if (!r.ok) {
/*     */       try {
/* 125 */         this.socket.close();
/*     */       } finally {
/* 127 */         throw new IOException("Connect failed");
/*     */       } 
/*     */     }
/* 130 */     if (enableAPOP) {
/* 131 */       int challStart = r.data.indexOf('<');
/* 132 */       int challEnd = r.data.indexOf('>', challStart);
/* 133 */       if (challStart != -1 && challEnd != -1)
/* 134 */         this.apopChallenge = r.data.substring(challStart, challEnd + 1); 
/* 135 */       logger.log(Level.FINE, "APOP challenge: {0}", this.apopChallenge);
/*     */     } 
/*     */ 
/*     */     
/* 139 */     if (!disableCapa) {
/* 140 */       setCapabilities(capa());
/*     */     }
/* 142 */     this.pipelining = (hasCapability("PIPELINING") || PropUtil.getBooleanProperty(props, prefix + ".pipelining", false));
/*     */     
/* 144 */     if (this.pipelining) {
/* 145 */       logger.config("PIPELINING enabled");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final synchronized boolean getBoolProp(Properties props, String prop) {
/* 154 */     boolean val = PropUtil.getBooleanProperty(props, prop, false);
/* 155 */     if (this.logger.isLoggable(Level.CONFIG))
/* 156 */       this.logger.config(prop + ": " + val); 
/* 157 */     return val;
/*     */   }
/*     */   
/*     */   private void initStreams() throws IOException {
/* 161 */     boolean quote = PropUtil.getBooleanProperty(this.props, "mail.debug.quote", false);
/*     */     
/* 163 */     this.traceInput = new TraceInputStream(this.socket.getInputStream(), this.traceLogger);
/*     */     
/* 165 */     this.traceInput.setQuote(quote);
/*     */     
/* 167 */     this.traceOutput = new TraceOutputStream(this.socket.getOutputStream(), this.traceLogger);
/*     */     
/* 169 */     this.traceOutput.setQuote(quote);
/*     */     
/* 171 */     this.input = new DataInputStream(new BufferedInputStream((InputStream)this.traceInput));
/* 172 */     this.output = new PrintWriter(new BufferedWriter(new OutputStreamWriter((OutputStream)this.traceOutput, "iso-8859-1")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 179 */     super.finalize();
/* 180 */     if (this.socket != null) {
/* 181 */       quit();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void setCapabilities(InputStream in) {
/* 189 */     if (in == null) {
/* 190 */       this.capabilities = null;
/*     */       
/*     */       return;
/*     */     } 
/* 194 */     this.capabilities = new HashMap(10);
/* 195 */     BufferedReader r = null;
/*     */     try {
/* 197 */       r = new BufferedReader(new InputStreamReader(in, "us-ascii"));
/* 198 */     } catch (UnsupportedEncodingException ex) {
/*     */       assert false;
/*     */     } 
/*     */     while (true) {
/*     */       try {
/*     */         String s;
/* 204 */         if ((s = r.readLine()) != null) {
/* 205 */           String cap = s;
/* 206 */           int i = cap.indexOf(' ');
/* 207 */           if (i > 0)
/* 208 */             cap = cap.substring(0, i); 
/* 209 */           this.capabilities.put(cap.toUpperCase(Locale.ENGLISH), s); continue;
/*     */         } 
/* 211 */       } catch (IOException ex) {}
/*     */ 
/*     */       
/*     */       try {
/* 215 */         in.close();
/* 216 */       } catch (IOException ex) {}
/*     */       break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean hasCapability(String c) {
/* 226 */     return (this.capabilities != null && this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized Map getCapabilities() {
/* 234 */     return this.capabilities;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized String login(String user, String password) throws IOException {
/* 244 */     boolean batch = (this.pipelining && this.socket instanceof javax.net.ssl.SSLSocket);
/*     */     
/*     */     try {
/*     */       Response r;
/* 248 */       if (this.noauthdebug && isTracing()) {
/* 249 */         this.logger.fine("authentication command trace suppressed");
/* 250 */         suspendTracing();
/*     */       } 
/* 252 */       String dpw = null;
/* 253 */       if (this.apopChallenge != null)
/* 254 */         dpw = getDigest(password); 
/* 255 */       if (this.apopChallenge != null && dpw != null) {
/* 256 */         r = simpleCommand("APOP " + user + " " + dpw);
/* 257 */       } else if (batch) {
/* 258 */         String cmd = "USER " + user;
/* 259 */         batchCommandStart(cmd);
/* 260 */         issueCommand(cmd);
/* 261 */         cmd = "PASS " + password;
/* 262 */         batchCommandContinue(cmd);
/* 263 */         issueCommand(cmd);
/* 264 */         r = readResponse();
/* 265 */         if (!r.ok) {
/* 266 */           String err = (r.data != null) ? r.data : "USER command failed";
/* 267 */           r = readResponse();
/* 268 */           batchCommandEnd();
/* 269 */           return err;
/*     */         } 
/* 271 */         r = readResponse();
/* 272 */         batchCommandEnd();
/*     */       } else {
/* 274 */         r = simpleCommand("USER " + user);
/* 275 */         if (!r.ok)
/* 276 */           return (r.data != null) ? r.data : "USER command failed"; 
/* 277 */         r = simpleCommand("PASS " + password);
/*     */       } 
/* 279 */       if (this.noauthdebug && isTracing()) {
/* 280 */         this.logger.log(Level.FINE, "authentication command {0}", r.ok ? "succeeded" : "failed");
/*     */       }
/* 282 */       if (!r.ok)
/* 283 */         return (r.data != null) ? r.data : "login failed"; 
/* 284 */       return null;
/*     */     } finally {
/*     */       
/* 287 */       resumeTracing();
/*     */     } 
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
/*     */ 
/*     */ 
/*     */   
/*     */   private String getDigest(String password) {
/*     */     byte[] digest;
/* 305 */     String key = this.apopChallenge + password;
/*     */     
/*     */     try {
/* 308 */       MessageDigest md = MessageDigest.getInstance("MD5");
/* 309 */       digest = md.digest(key.getBytes("iso-8859-1"));
/* 310 */     } catch (NoSuchAlgorithmException nsae) {
/* 311 */       return null;
/* 312 */     } catch (UnsupportedEncodingException uee) {
/* 313 */       return null;
/*     */     } 
/* 315 */     return toHex(digest);
/*     */   }
/*     */   
/* 318 */   private static char[] digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */ 
/*     */   
/*     */   static final boolean $assertionsDisabled;
/*     */ 
/*     */ 
/*     */   
/*     */   private static String toHex(byte[] bytes) {
/* 327 */     char[] result = new char[bytes.length * 2];
/*     */     
/* 329 */     for (int index = 0, i = 0; index < bytes.length; index++) {
/* 330 */       int temp = bytes[index] & 0xFF;
/* 331 */       result[i++] = digits[temp >> 4];
/* 332 */       result[i++] = digits[temp & 0xF];
/*     */     } 
/* 334 */     return new String(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean quit() throws IOException {
/* 341 */     boolean ok = false;
/*     */     try {
/* 343 */       Response r = simpleCommand("QUIT");
/* 344 */       ok = r.ok;
/*     */     } finally {
/*     */       try {
/* 347 */         this.socket.close();
/*     */       } finally {
/* 349 */         this.socket = null;
/* 350 */         this.input = null;
/* 351 */         this.output = null;
/*     */       } 
/*     */     } 
/* 354 */     return ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized Status stat() throws IOException {
/* 362 */     Response r = simpleCommand("STAT");
/* 363 */     Status s = new Status();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 373 */     if (!r.ok) {
/* 374 */       throw new IOException("STAT command failed: " + r.data);
/*     */     }
/* 376 */     if (r.data != null) {
/*     */       try {
/* 378 */         StringTokenizer st = new StringTokenizer(r.data);
/* 379 */         s.total = Integer.parseInt(st.nextToken());
/* 380 */         s.size = Integer.parseInt(st.nextToken());
/* 381 */       } catch (Exception e) {}
/*     */     }
/*     */     
/* 384 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized int list(int msg) throws IOException {
/* 391 */     Response r = simpleCommand("LIST " + msg);
/* 392 */     int size = -1;
/* 393 */     if (r.ok && r.data != null) {
/*     */       try {
/* 395 */         StringTokenizer st = new StringTokenizer(r.data);
/* 396 */         st.nextToken();
/* 397 */         size = Integer.parseInt(st.nextToken());
/* 398 */       } catch (Exception e) {}
/*     */     }
/*     */     
/* 401 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized InputStream list() throws IOException {
/* 408 */     Response r = multilineCommand("LIST", 128);
/* 409 */     return r.bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized InputStream retr(int msg, int size) throws IOException {
/*     */     Response r;
/* 421 */     boolean batch = (size == 0 && this.pipelining);
/* 422 */     if (batch) {
/* 423 */       String cmd = "LIST " + msg;
/* 424 */       batchCommandStart(cmd);
/* 425 */       issueCommand(cmd);
/* 426 */       cmd = "RETR " + msg;
/* 427 */       batchCommandContinue(cmd);
/* 428 */       issueCommand(cmd);
/* 429 */       r = readResponse();
/* 430 */       if (r.ok && r.data != null) {
/*     */         
/*     */         try {
/* 433 */           StringTokenizer st = new StringTokenizer(r.data);
/* 434 */           st.nextToken();
/* 435 */           size = Integer.parseInt(st.nextToken());
/*     */           
/* 437 */           if (size > 1073741824 || size < 0) {
/* 438 */             size = 0;
/*     */           } else {
/* 440 */             if (this.logger.isLoggable(Level.FINE))
/* 441 */               this.logger.fine("pipeline message size " + size); 
/* 442 */             size += 128;
/*     */           } 
/* 444 */         } catch (Exception e) {}
/*     */       }
/*     */       
/* 447 */       r = readResponse();
/* 448 */       if (r.ok)
/* 449 */         r.bytes = readMultilineResponse(size + 128); 
/* 450 */       batchCommandEnd();
/*     */     } else {
/* 452 */       String cmd = "RETR " + msg;
/* 453 */       multilineCommandStart(cmd);
/* 454 */       issueCommand(cmd);
/* 455 */       r = readResponse();
/* 456 */       if (!r.ok) {
/* 457 */         multilineCommandEnd();
/* 458 */         return null;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 468 */       if (size <= 0 && r.data != null) {
/*     */         try {
/* 470 */           StringTokenizer st = new StringTokenizer(r.data);
/* 471 */           String s = st.nextToken();
/* 472 */           String octets = st.nextToken();
/* 473 */           if (octets.equals("octets")) {
/* 474 */             size = Integer.parseInt(s);
/*     */             
/* 476 */             if (size > 1073741824 || size < 0) {
/* 477 */               size = 0;
/*     */             } else {
/* 479 */               if (this.logger.isLoggable(Level.FINE))
/* 480 */                 this.logger.fine("guessing message size: " + size); 
/* 481 */               size += 128;
/*     */             } 
/*     */           } 
/* 484 */         } catch (Exception e) {}
/*     */       }
/*     */       
/* 487 */       r.bytes = readMultilineResponse(size);
/* 488 */       multilineCommandEnd();
/*     */     } 
/* 490 */     if (r.ok && 
/* 491 */       size > 0 && this.logger.isLoggable(Level.FINE)) {
/* 492 */       this.logger.fine("got message size " + r.bytes.available());
/*     */     }
/* 494 */     return r.bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean retr(int msg, OutputStream os) throws IOException {
/*     */     int b;
/* 502 */     String cmd = "RETR " + msg;
/* 503 */     multilineCommandStart(cmd);
/* 504 */     issueCommand(cmd);
/* 505 */     Response r = readResponse();
/* 506 */     if (!r.ok) {
/* 507 */       multilineCommandEnd();
/* 508 */       return false;
/*     */     } 
/*     */     
/* 511 */     Throwable terr = null;
/* 512 */     int lastb = 10;
/*     */     try {
/* 514 */       while ((b = this.input.read()) >= 0) {
/* 515 */         if (lastb == 10 && b == 46) {
/* 516 */           b = this.input.read();
/* 517 */           if (b == 13) {
/*     */             
/* 519 */             b = this.input.read();
/*     */ 
/*     */ 
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 528 */         if (terr == null) {
/*     */           try {
/* 530 */             os.write(b);
/* 531 */           } catch (IOException ex) {
/* 532 */             this.logger.log(Level.FINE, "exception while streaming", ex);
/* 533 */             terr = ex;
/* 534 */           } catch (RuntimeException ex) {
/* 535 */             this.logger.log(Level.FINE, "exception while streaming", ex);
/* 536 */             terr = ex;
/*     */           } 
/*     */         }
/* 539 */         lastb = b;
/*     */       } 
/* 541 */     } catch (InterruptedIOException iioex) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 546 */         this.socket.close();
/* 547 */       } catch (IOException cex) {}
/* 548 */       throw iioex;
/*     */     } 
/* 550 */     if (b < 0) {
/* 551 */       throw new EOFException("EOF on socket");
/*     */     }
/*     */     
/* 554 */     if (terr != null) {
/* 555 */       if (terr instanceof IOException)
/* 556 */         throw (IOException)terr; 
/* 557 */       if (terr instanceof RuntimeException)
/* 558 */         throw (RuntimeException)terr; 
/*     */       assert false;
/*     */     } 
/* 561 */     multilineCommandEnd();
/* 562 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized InputStream top(int msg, int n) throws IOException {
/* 569 */     Response r = multilineCommand("TOP " + msg + " " + n, 0);
/* 570 */     return r.bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean dele(int msg) throws IOException {
/* 577 */     Response r = simpleCommand("DELE " + msg);
/* 578 */     return r.ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized String uidl(int msg) throws IOException {
/* 585 */     Response r = simpleCommand("UIDL " + msg);
/* 586 */     if (!r.ok)
/* 587 */       return null; 
/* 588 */     int i = r.data.indexOf(' ');
/* 589 */     if (i > 0) {
/* 590 */       return r.data.substring(i + 1);
/*     */     }
/* 592 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean uidl(String[] uids) throws IOException {
/* 600 */     Response r = multilineCommand("UIDL", 15 * uids.length);
/* 601 */     if (!r.ok)
/* 602 */       return false; 
/* 603 */     LineInputStream lis = new LineInputStream(r.bytes);
/* 604 */     String line = null;
/* 605 */     while ((line = lis.readLine()) != null) {
/* 606 */       int i = line.indexOf(' ');
/* 607 */       if (i < 1 || i >= line.length())
/*     */         continue; 
/* 609 */       int n = Integer.parseInt(line.substring(0, i));
/* 610 */       if (n > 0 && n <= uids.length)
/* 611 */         uids[n - 1] = line.substring(i + 1); 
/*     */     } 
/*     */     try {
/* 614 */       r.bytes.close();
/* 615 */     } catch (IOException ex) {}
/* 616 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean noop() throws IOException {
/* 623 */     Response r = simpleCommand("NOOP");
/* 624 */     return r.ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean rset() throws IOException {
/* 631 */     Response r = simpleCommand("RSET");
/* 632 */     return r.ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean stls() throws IOException {
/* 640 */     if (this.socket instanceof javax.net.ssl.SSLSocket)
/* 641 */       return true; 
/* 642 */     Response r = simpleCommand("STLS");
/* 643 */     if (r.ok) {
/*     */       
/*     */       try {
/* 646 */         this.socket = SocketFetcher.startTLS(this.socket, this.host, this.props, this.prefix);
/* 647 */         initStreams();
/* 648 */       } catch (IOException ioex) {
/*     */         try {
/* 650 */           this.socket.close();
/*     */         } finally {
/* 652 */           this.socket = null;
/* 653 */           this.input = null;
/* 654 */           this.output = null;
/*     */         } 
/* 656 */         IOException sioex = new IOException("Could not convert socket to TLS");
/*     */         
/* 658 */         sioex.initCause(ioex);
/* 659 */         throw sioex;
/*     */       } 
/*     */     }
/* 662 */     return r.ok;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized boolean isSSL() {
/* 669 */     return this.socket instanceof javax.net.ssl.SSLSocket;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized InputStream capa() throws IOException {
/* 677 */     Response r = multilineCommand("CAPA", 128);
/* 678 */     if (!r.ok)
/* 679 */       return null; 
/* 680 */     return r.bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Response simpleCommand(String cmd) throws IOException {
/* 687 */     simpleCommandStart(cmd);
/* 688 */     issueCommand(cmd);
/* 689 */     Response r = readResponse();
/* 690 */     simpleCommandEnd();
/* 691 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void issueCommand(String cmd) throws IOException {
/* 698 */     if (this.socket == null) {
/* 699 */       throw new IOException("Folder is closed");
/*     */     }
/* 701 */     if (cmd != null) {
/* 702 */       cmd = cmd + "\r\n";
/* 703 */       this.output.print(cmd);
/* 704 */       this.output.flush();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Response readResponse() throws IOException {
/* 712 */     String line = null;
/*     */     try {
/* 714 */       line = this.input.readLine();
/* 715 */     } catch (InterruptedIOException iioex) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 723 */         this.socket.close();
/* 724 */       } catch (IOException cex) {}
/* 725 */       throw new EOFException(iioex.getMessage());
/* 726 */     } catch (SocketException ex) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 734 */         this.socket.close();
/* 735 */       } catch (IOException cex) {}
/* 736 */       throw new EOFException(ex.getMessage());
/*     */     } 
/*     */     
/* 739 */     if (line == null) {
/* 740 */       this.traceLogger.finest("<EOF>");
/* 741 */       throw new EOFException("EOF on socket");
/*     */     } 
/* 743 */     Response r = new Response();
/* 744 */     if (line.startsWith("+OK")) {
/* 745 */       r.ok = true;
/* 746 */     } else if (line.startsWith("-ERR")) {
/* 747 */       r.ok = false;
/*     */     } else {
/* 749 */       throw new IOException("Unexpected response: " + line);
/*     */     }  int i;
/* 751 */     if ((i = line.indexOf(' ')) >= 0)
/* 752 */       r.data = line.substring(i + 1); 
/* 753 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Response multilineCommand(String cmd, int size) throws IOException {
/* 761 */     multilineCommandStart(cmd);
/* 762 */     issueCommand(cmd);
/* 763 */     Response r = readResponse();
/* 764 */     if (!r.ok) {
/* 765 */       multilineCommandEnd();
/* 766 */       return r;
/*     */     } 
/* 768 */     r.bytes = readMultilineResponse(size);
/* 769 */     multilineCommandEnd();
/* 770 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private InputStream readMultilineResponse(int size) throws IOException {
/*     */     int b;
/* 780 */     SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size);
/* 781 */     int lastb = 10;
/*     */     try {
/* 783 */       while ((b = this.input.read()) >= 0) {
/* 784 */         if (lastb == 10 && b == 46) {
/* 785 */           b = this.input.read();
/* 786 */           if (b == 13) {
/*     */             
/* 788 */             b = this.input.read();
/*     */             break;
/*     */           } 
/*     */         } 
/* 792 */         buf.write(b);
/* 793 */         lastb = b;
/*     */       } 
/* 795 */     } catch (InterruptedIOException iioex) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 800 */         this.socket.close();
/* 801 */       } catch (IOException cex) {}
/* 802 */       throw iioex;
/*     */     } 
/* 804 */     if (b < 0)
/* 805 */       throw new EOFException("EOF on socket"); 
/* 806 */     return buf.toStream();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isTracing() {
/* 813 */     return this.traceLogger.isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void suspendTracing() {
/* 821 */     if (this.traceLogger.isLoggable(Level.FINEST)) {
/* 822 */       this.traceInput.setTrace(false);
/* 823 */       this.traceOutput.setTrace(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void resumeTracing() {
/* 831 */     if (this.traceLogger.isLoggable(Level.FINEST)) {
/* 832 */       this.traceInput.setTrace(true);
/* 833 */       this.traceOutput.setTrace(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void simpleCommandStart(String command) {}
/*     */   
/*     */   private void simpleCommandEnd() {}
/*     */   
/*     */   private void multilineCommandStart(String command) {}
/*     */   
/*     */   private void multilineCommandEnd() {}
/*     */   
/*     */   private void batchCommandStart(String command) {}
/*     */   
/*     */   private void batchCommandContinue(String command) {}
/*     */   
/*     */   private void batchCommandEnd() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\Protocol.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */