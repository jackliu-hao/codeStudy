/*      */ package com.sun.mail.imap.protocol;
/*      */ 
/*      */ import com.sun.mail.auth.Ntlm;
/*      */ import com.sun.mail.iap.Argument;
/*      */ import com.sun.mail.iap.BadCommandException;
/*      */ import com.sun.mail.iap.ByteArray;
/*      */ import com.sun.mail.iap.CommandFailedException;
/*      */ import com.sun.mail.iap.ConnectionException;
/*      */ import com.sun.mail.iap.Literal;
/*      */ import com.sun.mail.iap.LiteralException;
/*      */ import com.sun.mail.iap.ParsingException;
/*      */ import com.sun.mail.iap.Protocol;
/*      */ import com.sun.mail.iap.ProtocolException;
/*      */ import com.sun.mail.iap.Response;
/*      */ import com.sun.mail.imap.ACL;
/*      */ import com.sun.mail.imap.AppendUID;
/*      */ import com.sun.mail.imap.Rights;
/*      */ import com.sun.mail.imap.SortTerm;
/*      */ import com.sun.mail.util.ASCIIUtility;
/*      */ import com.sun.mail.util.BASE64EncoderStream;
/*      */ import com.sun.mail.util.MailLogger;
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.io.OutputStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import javax.mail.Flags;
/*      */ import javax.mail.Quota;
/*      */ import javax.mail.internet.MimeUtility;
/*      */ import javax.mail.search.SearchException;
/*      */ import javax.mail.search.SearchTerm;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class IMAPProtocol
/*      */   extends Protocol
/*      */ {
/*      */   private boolean connected = false;
/*      */   private boolean rev1 = false;
/*      */   private boolean noauthdebug = true;
/*      */   private boolean authenticated;
/*      */   private Map capabilities;
/*      */   private List authmechs;
/*      */   protected SearchSequence searchSequence;
/*      */   protected String[] searchCharsets;
/*      */   private String name;
/*      */   private SaslAuthenticator saslAuthenticator;
/*      */   private ByteArray ba;
/*   99 */   private static final byte[] CRLF = new byte[] { 13, 10 };
/*      */   
/*  101 */   private static final FetchItem[] fetchItems = new FetchItem[0];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile String idleTag;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IMAPProtocol(String name, String host, int port, Properties props, boolean isSSL, MailLogger logger) throws IOException, ProtocolException {
/*  115 */     super(host, port, props, "mail." + name, isSSL, logger);
/*      */     
/*      */     try {
/*  118 */       this.name = name;
/*  119 */       this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
/*      */ 
/*      */       
/*  122 */       if (this.capabilities == null) {
/*  123 */         capability();
/*      */       }
/*  125 */       if (hasCapability("IMAP4rev1")) {
/*  126 */         this.rev1 = true;
/*      */       }
/*  128 */       this.searchCharsets = new String[2];
/*  129 */       this.searchCharsets[0] = "UTF-8";
/*  130 */       this.searchCharsets[1] = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
/*      */ 
/*      */ 
/*      */       
/*  134 */       this.connected = true;
/*      */ 
/*      */     
/*      */     }
/*      */     finally {
/*      */ 
/*      */ 
/*      */       
/*  142 */       if (!this.connected) {
/*  143 */         disconnect();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FetchItem[] getFetchItems() {
/*  156 */     return fetchItems;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void capability() throws ProtocolException {
/*  166 */     Response[] r = command("CAPABILITY", null);
/*      */     
/*  168 */     if (!r[r.length - 1].isOK()) {
/*  169 */       throw new ProtocolException(r[r.length - 1].toString());
/*      */     }
/*  171 */     this.capabilities = new HashMap(10);
/*  172 */     this.authmechs = new ArrayList(5);
/*  173 */     for (int i = 0, len = r.length; i < len; i++) {
/*  174 */       if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */         
/*  177 */         IMAPResponse ir = (IMAPResponse)r[i];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  183 */         if (ir.keyEquals("CAPABILITY")) {
/*  184 */           parseCapabilities(ir);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setCapabilities(Response r) {
/*      */     byte b;
/*  194 */     while ((b = r.readByte()) > 0 && b != 91);
/*      */     
/*  196 */     if (b == 0) {
/*      */       return;
/*      */     }
/*  199 */     String s = r.readAtom();
/*  200 */     if (!s.equalsIgnoreCase("CAPABILITY"))
/*      */       return; 
/*  202 */     this.capabilities = new HashMap(10);
/*  203 */     this.authmechs = new ArrayList(5);
/*  204 */     parseCapabilities(r);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void parseCapabilities(Response r) {
/*      */     String s;
/*  213 */     while ((s = r.readAtom(']')) != null) {
/*  214 */       if (s.length() == 0) {
/*  215 */         if (r.peekByte() == 93) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  228 */         r.skipToken(); continue;
/*      */       } 
/*  230 */       this.capabilities.put(s.toUpperCase(Locale.ENGLISH), s);
/*  231 */       if (s.regionMatches(true, 0, "AUTH=", 0, 5)) {
/*  232 */         this.authmechs.add(s.substring(5));
/*  233 */         if (this.logger.isLoggable(Level.FINE)) {
/*  234 */           this.logger.fine("AUTH: " + s.substring(5));
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void processGreeting(Response r) throws ProtocolException {
/*  244 */     super.processGreeting(r);
/*  245 */     if (r.isOK()) {
/*  246 */       setCapabilities(r);
/*      */       
/*      */       return;
/*      */     } 
/*  250 */     IMAPResponse ir = (IMAPResponse)r;
/*  251 */     if (ir.keyEquals("PREAUTH")) {
/*  252 */       this.authenticated = true;
/*  253 */       setCapabilities(r);
/*      */     } else {
/*  255 */       throw new ConnectionException(this, r);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAuthenticated() {
/*  263 */     return this.authenticated;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isREV1() {
/*  270 */     return this.rev1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean supportsNonSyncLiterals() {
/*  277 */     return hasCapability("LITERAL+");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Response readResponse() throws IOException, ProtocolException {
/*  286 */     IMAPResponse r = new IMAPResponse(this);
/*  287 */     if (r.keyEquals("FETCH"))
/*  288 */       r = new FetchResponse(r, getFetchItems()); 
/*  289 */     return r;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasCapability(String c) {
/*  298 */     if (c.endsWith("*")) {
/*  299 */       c = c.substring(0, c.length() - 1).toUpperCase(Locale.ENGLISH);
/*  300 */       Iterator it = this.capabilities.keySet().iterator();
/*  301 */       while (it.hasNext()) {
/*  302 */         if (((String)it.next()).startsWith(c))
/*  303 */           return true; 
/*      */       } 
/*  305 */       return false;
/*      */     } 
/*  307 */     return this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map getCapabilities() {
/*  316 */     return this.capabilities;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disconnect() {
/*  326 */     super.disconnect();
/*  327 */     this.authenticated = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void noop() throws ProtocolException {
/*  336 */     this.logger.fine("IMAPProtocol noop");
/*  337 */     simpleCommand("NOOP", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void logout() throws ProtocolException {
/*      */     try {
/*  347 */       Response[] r = command("LOGOUT", null);
/*      */       
/*  349 */       this.authenticated = false;
/*      */ 
/*      */       
/*  352 */       notifyResponseHandlers(r);
/*      */     } finally {
/*  354 */       disconnect();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void login(String u, String p) throws ProtocolException {
/*  364 */     Argument args = new Argument();
/*  365 */     args.writeString(u);
/*  366 */     args.writeString(p);
/*      */     
/*  368 */     Response[] r = null;
/*      */     try {
/*  370 */       if (this.noauthdebug && isTracing()) {
/*  371 */         this.logger.fine("LOGIN command trace suppressed");
/*  372 */         suspendTracing();
/*      */       } 
/*  374 */       r = command("LOGIN", args);
/*      */     } finally {
/*  376 */       resumeTracing();
/*      */     } 
/*      */ 
/*      */     
/*  380 */     notifyResponseHandlers(r);
/*      */ 
/*      */     
/*  383 */     if (this.noauthdebug && isTracing())
/*  384 */       this.logger.fine("LOGIN command result: " + r[r.length - 1]); 
/*  385 */     handleResult(r[r.length - 1]);
/*      */     
/*  387 */     setCapabilities(r[r.length - 1]);
/*      */     
/*  389 */     this.authenticated = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void authlogin(String u, String p) throws ProtocolException {
/*  399 */     Vector v = new Vector();
/*  400 */     String tag = null;
/*  401 */     Response r = null;
/*  402 */     boolean done = false;
/*      */ 
/*      */     
/*      */     try {
/*  406 */       if (this.noauthdebug && isTracing()) {
/*  407 */         this.logger.fine("AUTHENTICATE LOGIN command trace suppressed");
/*  408 */         suspendTracing();
/*      */       } 
/*      */       
/*      */       try {
/*  412 */         tag = writeCommand("AUTHENTICATE LOGIN", null);
/*  413 */       } catch (Exception ex) {
/*      */         
/*  415 */         r = Response.byeResponse(ex);
/*  416 */         done = true;
/*      */       } 
/*      */       
/*  419 */       OutputStream os = getOutputStream();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  437 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  438 */       BASE64EncoderStream bASE64EncoderStream = new BASE64EncoderStream(bos, 2147483647);
/*  439 */       boolean first = true;
/*      */       
/*  441 */       while (!done) {
/*      */         try {
/*  443 */           r = readResponse();
/*  444 */           if (r.isContinuation()) {
/*      */             String s;
/*      */             
/*  447 */             if (first) {
/*  448 */               s = u;
/*  449 */               first = false;
/*      */             } else {
/*  451 */               s = p;
/*      */             } 
/*      */             
/*  454 */             bASE64EncoderStream.write(ASCIIUtility.getBytes(s));
/*  455 */             bASE64EncoderStream.flush();
/*      */             
/*  457 */             bos.write(CRLF);
/*  458 */             os.write(bos.toByteArray());
/*  459 */             os.flush();
/*  460 */             bos.reset(); continue;
/*  461 */           }  if (r.isTagged() && r.getTag().equals(tag)) {
/*      */             
/*  463 */             done = true; continue;
/*  464 */           }  if (r.isBYE()) {
/*  465 */             done = true; continue;
/*      */           } 
/*  467 */           v.addElement(r);
/*  468 */         } catch (Exception ioex) {
/*      */           
/*  470 */           r = Response.byeResponse(ioex);
/*  471 */           done = true;
/*      */         } 
/*      */       } 
/*      */     } finally {
/*      */       
/*  476 */       resumeTracing();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  485 */     Response[] responses = new Response[v.size()];
/*  486 */     v.copyInto((Object[])responses);
/*  487 */     notifyResponseHandlers(responses);
/*      */ 
/*      */     
/*  490 */     if (this.noauthdebug && isTracing())
/*  491 */       this.logger.fine("AUTHENTICATE LOGIN command result: " + r); 
/*  492 */     handleResult(r);
/*      */     
/*  494 */     setCapabilities(r);
/*      */     
/*  496 */     this.authenticated = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void authplain(String authzid, String u, String p) throws ProtocolException {
/*  514 */     Vector v = new Vector();
/*  515 */     String tag = null;
/*  516 */     Response r = null;
/*  517 */     boolean done = false;
/*      */ 
/*      */     
/*      */     try {
/*  521 */       if (this.noauthdebug && isTracing()) {
/*  522 */         this.logger.fine("AUTHENTICATE PLAIN command trace suppressed");
/*  523 */         suspendTracing();
/*      */       } 
/*      */       
/*      */       try {
/*  527 */         tag = writeCommand("AUTHENTICATE PLAIN", null);
/*  528 */       } catch (Exception ex) {
/*      */         
/*  530 */         r = Response.byeResponse(ex);
/*  531 */         done = true;
/*      */       } 
/*      */       
/*  534 */       OutputStream os = getOutputStream();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  552 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  553 */       BASE64EncoderStream bASE64EncoderStream = new BASE64EncoderStream(bos, 2147483647);
/*      */       
/*  555 */       while (!done) {
/*      */         try {
/*  557 */           r = readResponse();
/*  558 */           if (r.isContinuation()) {
/*      */             
/*  560 */             String nullByte = "\000";
/*  561 */             String s = ((authzid == null) ? "" : authzid) + "\000" + u + "\000" + p;
/*      */ 
/*      */ 
/*      */             
/*  565 */             bASE64EncoderStream.write(ASCIIUtility.getBytes(s));
/*  566 */             bASE64EncoderStream.flush();
/*      */             
/*  568 */             bos.write(CRLF);
/*  569 */             os.write(bos.toByteArray());
/*  570 */             os.flush();
/*  571 */             bos.reset(); continue;
/*  572 */           }  if (r.isTagged() && r.getTag().equals(tag)) {
/*      */             
/*  574 */             done = true; continue;
/*  575 */           }  if (r.isBYE()) {
/*  576 */             done = true; continue;
/*      */           } 
/*  578 */           v.addElement(r);
/*  579 */         } catch (Exception ioex) {
/*      */           
/*  581 */           r = Response.byeResponse(ioex);
/*  582 */           done = true;
/*      */         } 
/*      */       } 
/*      */     } finally {
/*      */       
/*  587 */       resumeTracing();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  596 */     Response[] responses = new Response[v.size()];
/*  597 */     v.copyInto((Object[])responses);
/*  598 */     notifyResponseHandlers(responses);
/*      */ 
/*      */     
/*  601 */     if (this.noauthdebug && isTracing())
/*  602 */       this.logger.fine("AUTHENTICATE PLAIN command result: " + r); 
/*  603 */     handleResult(r);
/*      */     
/*  605 */     setCapabilities(r);
/*      */     
/*  607 */     this.authenticated = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void authntlm(String authzid, String u, String p) throws ProtocolException {
/*  624 */     Vector v = new Vector();
/*  625 */     String tag = null;
/*  626 */     Response r = null;
/*  627 */     boolean done = false;
/*      */     
/*  629 */     String type1Msg = null;
/*  630 */     int flags = PropUtil.getIntProperty(this.props, "mail." + this.name + ".auth.ntlm.flags", 0);
/*      */     
/*  632 */     String domain = this.props.getProperty("mail." + this.name + ".auth.ntlm.domain", "");
/*      */     
/*  634 */     Ntlm ntlm = new Ntlm(domain, getLocalHost(), u, p, this.logger);
/*      */ 
/*      */     
/*      */     try {
/*  638 */       if (this.noauthdebug && isTracing()) {
/*  639 */         this.logger.fine("AUTHENTICATE NTLM command trace suppressed");
/*  640 */         suspendTracing();
/*      */       } 
/*      */       
/*      */       try {
/*  644 */         tag = writeCommand("AUTHENTICATE NTLM", null);
/*  645 */       } catch (Exception ex) {
/*      */         
/*  647 */         r = Response.byeResponse(ex);
/*  648 */         done = true;
/*      */       } 
/*      */       
/*  651 */       OutputStream os = getOutputStream();
/*  652 */       boolean first = true;
/*      */       
/*  654 */       while (!done) {
/*      */         try {
/*  656 */           r = readResponse();
/*  657 */           if (r.isContinuation()) {
/*      */             String s;
/*      */             
/*  660 */             if (first) {
/*  661 */               s = ntlm.generateType1Msg(flags);
/*  662 */               first = false;
/*      */             } else {
/*  664 */               s = ntlm.generateType3Msg(r.getRest());
/*      */             } 
/*      */             
/*  667 */             os.write(ASCIIUtility.getBytes(s));
/*  668 */             os.write(CRLF);
/*  669 */             os.flush(); continue;
/*  670 */           }  if (r.isTagged() && r.getTag().equals(tag)) {
/*      */             
/*  672 */             done = true; continue;
/*  673 */           }  if (r.isBYE()) {
/*  674 */             done = true; continue;
/*      */           } 
/*  676 */           v.addElement(r);
/*  677 */         } catch (Exception ioex) {
/*      */           
/*  679 */           r = Response.byeResponse(ioex);
/*  680 */           done = true;
/*      */         } 
/*      */       } 
/*      */     } finally {
/*      */       
/*  685 */       resumeTracing();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  695 */     Response[] responses = new Response[v.size()];
/*  696 */     v.copyInto((Object[])responses);
/*  697 */     notifyResponseHandlers(responses);
/*      */ 
/*      */     
/*  700 */     if (this.noauthdebug && isTracing())
/*  701 */       this.logger.fine("AUTHENTICATE NTLM command result: " + r); 
/*  702 */     handleResult(r);
/*      */     
/*  704 */     setCapabilities(r);
/*      */     
/*  706 */     this.authenticated = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sasllogin(String[] allowed, String realm, String authzid, String u, String p) throws ProtocolException {
/*      */     List v;
/*  714 */     if (this.saslAuthenticator == null) {
/*      */       try {
/*  716 */         Class sac = Class.forName("com.sun.mail.imap.protocol.IMAPSaslAuthenticator");
/*      */         
/*  718 */         Constructor c = sac.getConstructor(new Class[] { IMAPProtocol.class, String.class, Properties.class, MailLogger.class, String.class });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  725 */         this.saslAuthenticator = (SaslAuthenticator)c.newInstance(new Object[] { this, this.name, this.props, this.logger, this.host });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  733 */       catch (Exception ex) {
/*  734 */         this.logger.log(Level.FINE, "Can't load SASL authenticator", ex);
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  742 */     if (allowed != null && allowed.length > 0) {
/*      */       
/*  744 */       v = new ArrayList(allowed.length);
/*  745 */       for (int i = 0; i < allowed.length; i++) {
/*  746 */         if (this.authmechs.contains(allowed[i]))
/*  747 */           v.add(allowed[i]); 
/*      */       } 
/*      */     } else {
/*  750 */       v = this.authmechs;
/*      */     } 
/*  752 */     String[] mechs = (String[])v.toArray((Object[])new String[v.size()]);
/*      */ 
/*      */     
/*      */     try {
/*  756 */       if (this.noauthdebug && isTracing()) {
/*  757 */         this.logger.fine("SASL authentication command trace suppressed");
/*  758 */         suspendTracing();
/*      */       } 
/*      */       
/*  761 */       if (this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p)) {
/*  762 */         if (this.noauthdebug && isTracing())
/*  763 */           this.logger.fine("SASL authentication succeeded"); 
/*  764 */         this.authenticated = true;
/*      */       }
/*  766 */       else if (this.noauthdebug && isTracing()) {
/*  767 */         this.logger.fine("SASL authentication failed");
/*      */       } 
/*      */     } finally {
/*  770 */       resumeTracing();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   OutputStream getIMAPOutputStream() {
/*  776 */     return getOutputStream();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void proxyauth(String u) throws ProtocolException {
/*  785 */     Argument args = new Argument();
/*  786 */     args.writeString(u);
/*      */     
/*  788 */     simpleCommand("PROXYAUTH", args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void id(String guid) throws ProtocolException {
/*  810 */     simpleCommand("ID (\"GUID\" \"" + guid + "\")", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void startTLS() throws ProtocolException {
/*      */     try {
/*  820 */       startTLS("STARTTLS");
/*  821 */     } catch (ProtocolException pex) {
/*  822 */       this.logger.log(Level.FINE, "STARTTLS ProtocolException", (Throwable)pex);
/*      */ 
/*      */ 
/*      */       
/*  826 */       throw pex;
/*  827 */     } catch (Exception ex) {
/*  828 */       this.logger.log(Level.FINE, "STARTTLS Exception", ex);
/*      */ 
/*      */       
/*  831 */       Response[] r = { Response.byeResponse(ex) };
/*  832 */       notifyResponseHandlers(r);
/*  833 */       disconnect();
/*  834 */       throw new ProtocolException("STARTTLS failure", ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MailboxInfo select(String mbox) throws ProtocolException {
/*  845 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/*  847 */     Argument args = new Argument();
/*  848 */     args.writeString(mbox);
/*      */     
/*  850 */     Response[] r = command("SELECT", args);
/*      */ 
/*      */ 
/*      */     
/*  854 */     MailboxInfo minfo = new MailboxInfo(r);
/*      */ 
/*      */     
/*  857 */     notifyResponseHandlers(r);
/*      */     
/*  859 */     Response response = r[r.length - 1];
/*      */     
/*  861 */     if (response.isOK()) {
/*  862 */       if (response.toString().indexOf("READ-ONLY") != -1) {
/*  863 */         minfo.mode = 1;
/*      */       } else {
/*  865 */         minfo.mode = 2;
/*      */       } 
/*      */     }
/*  868 */     handleResult(response);
/*  869 */     return minfo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MailboxInfo examine(String mbox) throws ProtocolException {
/*  879 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/*  881 */     Argument args = new Argument();
/*  882 */     args.writeString(mbox);
/*      */     
/*  884 */     Response[] r = command("EXAMINE", args);
/*      */ 
/*      */ 
/*      */     
/*  888 */     MailboxInfo minfo = new MailboxInfo(r);
/*  889 */     minfo.mode = 1;
/*      */ 
/*      */     
/*  892 */     notifyResponseHandlers(r);
/*      */     
/*  894 */     handleResult(r[r.length - 1]);
/*  895 */     return minfo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unselect() throws ProtocolException {
/*  905 */     if (!hasCapability("UNSELECT"))
/*  906 */       throw new BadCommandException("UNSELECT not supported"); 
/*  907 */     simpleCommand("UNSELECT", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Status status(String mbox, String[] items) throws ProtocolException {
/*  917 */     if (!isREV1() && !hasCapability("IMAP4SUNVERSION"))
/*      */     {
/*      */       
/*  920 */       throw new BadCommandException("STATUS not supported");
/*      */     }
/*      */     
/*  923 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/*  925 */     Argument args = new Argument();
/*  926 */     args.writeString(mbox);
/*      */     
/*  928 */     Argument itemArgs = new Argument();
/*  929 */     if (items == null) {
/*  930 */       items = Status.standardItems;
/*      */     }
/*  932 */     for (int i = 0, len = items.length; i < len; i++)
/*  933 */       itemArgs.writeAtom(items[i]); 
/*  934 */     args.writeArgument(itemArgs);
/*      */     
/*  936 */     Response[] r = command("STATUS", args);
/*      */     
/*  938 */     Status status = null;
/*  939 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/*  942 */     if (response.isOK()) {
/*  943 */       for (int j = 0, k = r.length; j < k; j++) {
/*  944 */         if (r[j] instanceof IMAPResponse) {
/*      */ 
/*      */           
/*  947 */           IMAPResponse ir = (IMAPResponse)r[j];
/*  948 */           if (ir.keyEquals("STATUS")) {
/*  949 */             if (status == null) {
/*  950 */               status = new Status(ir);
/*      */             } else {
/*  952 */               Status.add(status, new Status(ir));
/*  953 */             }  r[j] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  959 */     notifyResponseHandlers(r);
/*  960 */     handleResult(response);
/*  961 */     return status;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void create(String mbox) throws ProtocolException {
/*  971 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/*  973 */     Argument args = new Argument();
/*  974 */     args.writeString(mbox);
/*      */     
/*  976 */     simpleCommand("CREATE", args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void delete(String mbox) throws ProtocolException {
/*  986 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/*  988 */     Argument args = new Argument();
/*  989 */     args.writeString(mbox);
/*      */     
/*  991 */     simpleCommand("DELETE", args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rename(String o, String n) throws ProtocolException {
/* 1001 */     o = BASE64MailboxEncoder.encode(o);
/* 1002 */     n = BASE64MailboxEncoder.encode(n);
/*      */     
/* 1004 */     Argument args = new Argument();
/* 1005 */     args.writeString(o);
/* 1006 */     args.writeString(n);
/*      */     
/* 1008 */     simpleCommand("RENAME", args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void subscribe(String mbox) throws ProtocolException {
/* 1017 */     Argument args = new Argument();
/*      */     
/* 1019 */     mbox = BASE64MailboxEncoder.encode(mbox);
/* 1020 */     args.writeString(mbox);
/*      */     
/* 1022 */     simpleCommand("SUBSCRIBE", args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsubscribe(String mbox) throws ProtocolException {
/* 1031 */     Argument args = new Argument();
/*      */     
/* 1033 */     mbox = BASE64MailboxEncoder.encode(mbox);
/* 1034 */     args.writeString(mbox);
/*      */     
/* 1036 */     simpleCommand("UNSUBSCRIBE", args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ListInfo[] list(String ref, String pattern) throws ProtocolException {
/* 1046 */     return doList("LIST", ref, pattern);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ListInfo[] lsub(String ref, String pattern) throws ProtocolException {
/* 1056 */     return doList("LSUB", ref, pattern);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ListInfo[] doList(String cmd, String ref, String pat) throws ProtocolException {
/* 1068 */     ref = BASE64MailboxEncoder.encode(ref);
/* 1069 */     pat = BASE64MailboxEncoder.encode(pat);
/*      */     
/* 1071 */     Argument args = new Argument();
/* 1072 */     args.writeString(ref);
/* 1073 */     args.writeString(pat);
/*      */     
/* 1075 */     Response[] r = command(cmd, args);
/*      */     
/* 1077 */     ListInfo[] linfo = null;
/* 1078 */     Response response = r[r.length - 1];
/*      */     
/* 1080 */     if (response.isOK()) {
/* 1081 */       Vector v = new Vector(1);
/* 1082 */       for (int i = 0, len = r.length; i < len; i++) {
/* 1083 */         if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 1086 */           IMAPResponse ir = (IMAPResponse)r[i];
/* 1087 */           if (ir.keyEquals(cmd)) {
/* 1088 */             v.addElement(new ListInfo(ir));
/* 1089 */             r[i] = null;
/*      */           } 
/*      */         } 
/* 1092 */       }  if (v.size() > 0) {
/* 1093 */         linfo = new ListInfo[v.size()];
/* 1094 */         v.copyInto((Object[])linfo);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1099 */     notifyResponseHandlers(r);
/* 1100 */     handleResult(response);
/* 1101 */     return linfo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void append(String mbox, Flags f, Date d, Literal data) throws ProtocolException {
/* 1111 */     appenduid(mbox, f, d, data, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AppendUID appenduid(String mbox, Flags f, Date d, Literal data) throws ProtocolException {
/* 1121 */     return appenduid(mbox, f, d, data, true);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AppendUID appenduid(String mbox, Flags f, Date d, Literal data, boolean uid) throws ProtocolException {
/* 1127 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 1129 */     Argument args = new Argument();
/* 1130 */     args.writeString(mbox);
/*      */     
/* 1132 */     if (f != null) {
/*      */       
/* 1134 */       if (f.contains(Flags.Flag.RECENT)) {
/* 1135 */         f = new Flags(f);
/* 1136 */         f.remove(Flags.Flag.RECENT);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1149 */       args.writeAtom(createFlagList(f));
/*      */     } 
/* 1151 */     if (d != null) {
/* 1152 */       args.writeString(INTERNALDATE.format(d));
/*      */     }
/* 1154 */     args.writeBytes(data);
/*      */     
/* 1156 */     Response[] r = command("APPEND", args);
/*      */ 
/*      */     
/* 1159 */     notifyResponseHandlers(r);
/*      */ 
/*      */     
/* 1162 */     handleResult(r[r.length - 1]);
/*      */     
/* 1164 */     if (uid) {
/* 1165 */       return getAppendUID(r[r.length - 1]);
/*      */     }
/* 1167 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AppendUID getAppendUID(Response r) {
/* 1175 */     if (!r.isOK())
/* 1176 */       return null; 
/*      */     byte b;
/* 1178 */     while ((b = r.readByte()) > 0 && b != 91);
/*      */     
/* 1180 */     if (b == 0) {
/* 1181 */       return null;
/*      */     }
/* 1183 */     String s = r.readAtom();
/* 1184 */     if (!s.equalsIgnoreCase("APPENDUID")) {
/* 1185 */       return null;
/*      */     }
/* 1187 */     long uidvalidity = r.readLong();
/* 1188 */     long uid = r.readLong();
/* 1189 */     return new AppendUID(uidvalidity, uid);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void check() throws ProtocolException {
/* 1198 */     simpleCommand("CHECK", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws ProtocolException {
/* 1207 */     simpleCommand("CLOSE", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void expunge() throws ProtocolException {
/* 1216 */     simpleCommand("EXPUNGE", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void uidexpunge(UIDSet[] set) throws ProtocolException {
/* 1225 */     if (!hasCapability("UIDPLUS"))
/* 1226 */       throw new BadCommandException("UID EXPUNGE not supported"); 
/* 1227 */     simpleCommand("UID EXPUNGE " + UIDSet.toString(set), null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BODYSTRUCTURE fetchBodyStructure(int msgno) throws ProtocolException {
/* 1235 */     Response[] r = fetch(msgno, "BODYSTRUCTURE");
/* 1236 */     notifyResponseHandlers(r);
/*      */     
/* 1238 */     Response response = r[r.length - 1];
/* 1239 */     if (response.isOK()) {
/* 1240 */       return (BODYSTRUCTURE)FetchResponse.getItem(r, msgno, BODYSTRUCTURE.class);
/*      */     }
/* 1242 */     if (response.isNO()) {
/* 1243 */       return null;
/*      */     }
/* 1245 */     handleResult(response);
/* 1246 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BODY peekBody(int msgno, String section) throws ProtocolException {
/* 1256 */     return fetchBody(msgno, section, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BODY fetchBody(int msgno, String section) throws ProtocolException {
/* 1264 */     return fetchBody(msgno, section, false);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected BODY fetchBody(int msgno, String section, boolean peek) throws ProtocolException {
/*      */     Response[] r;
/* 1271 */     if (peek) {
/* 1272 */       r = fetch(msgno, "BODY.PEEK[" + ((section == null) ? "]" : (section + "]")));
/*      */     } else {
/*      */       
/* 1275 */       r = fetch(msgno, "BODY[" + ((section == null) ? "]" : (section + "]")));
/*      */     } 
/*      */     
/* 1278 */     notifyResponseHandlers(r);
/*      */     
/* 1280 */     Response response = r[r.length - 1];
/* 1281 */     if (response.isOK())
/* 1282 */       return (BODY)FetchResponse.getItem(r, msgno, BODY.class); 
/* 1283 */     if (response.isNO()) {
/* 1284 */       return null;
/*      */     }
/* 1286 */     handleResult(response);
/* 1287 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BODY peekBody(int msgno, String section, int start, int size) throws ProtocolException {
/* 1296 */     return fetchBody(msgno, section, start, size, true, (ByteArray)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BODY fetchBody(int msgno, String section, int start, int size) throws ProtocolException {
/* 1304 */     return fetchBody(msgno, section, start, size, false, (ByteArray)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BODY peekBody(int msgno, String section, int start, int size, ByteArray ba) throws ProtocolException {
/* 1312 */     return fetchBody(msgno, section, start, size, true, ba);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BODY fetchBody(int msgno, String section, int start, int size, ByteArray ba) throws ProtocolException {
/* 1320 */     return fetchBody(msgno, section, start, size, false, ba);
/*      */   }
/*      */ 
/*      */   
/*      */   protected BODY fetchBody(int msgno, String section, int start, int size, boolean peek, ByteArray ba) throws ProtocolException {
/* 1325 */     this.ba = ba;
/* 1326 */     Response[] r = fetch(msgno, (peek ? "BODY.PEEK[" : "BODY[") + ((section == null) ? "]<" : (section + "]<")) + String.valueOf(start) + "." + String.valueOf(size) + ">");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1333 */     notifyResponseHandlers(r);
/*      */     
/* 1335 */     Response response = r[r.length - 1];
/* 1336 */     if (response.isOK())
/* 1337 */       return (BODY)FetchResponse.getItem(r, msgno, BODY.class); 
/* 1338 */     if (response.isNO()) {
/* 1339 */       return null;
/*      */     }
/* 1341 */     handleResult(response);
/* 1342 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ByteArray getResponseBuffer() {
/* 1352 */     ByteArray ret = this.ba;
/* 1353 */     this.ba = null;
/* 1354 */     return ret;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RFC822DATA fetchRFC822(int msgno, String what) throws ProtocolException {
/* 1364 */     Response[] r = fetch(msgno, (what == null) ? "RFC822" : ("RFC822." + what));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1369 */     notifyResponseHandlers(r);
/*      */     
/* 1371 */     Response response = r[r.length - 1];
/* 1372 */     if (response.isOK()) {
/* 1373 */       return (RFC822DATA)FetchResponse.getItem(r, msgno, RFC822DATA.class);
/*      */     }
/* 1375 */     if (response.isNO()) {
/* 1376 */       return null;
/*      */     }
/* 1378 */     handleResult(response);
/* 1379 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Flags fetchFlags(int msgno) throws ProtocolException {
/* 1387 */     Flags flags = null;
/* 1388 */     Response[] r = fetch(msgno, "FLAGS");
/*      */ 
/*      */     
/* 1391 */     for (int i = 0, len = r.length; i < len; i++) {
/* 1392 */       if (r[i] != null && r[i] instanceof FetchResponse && ((FetchResponse)r[i]).getNumber() == msgno) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1397 */         FetchResponse fr = (FetchResponse)r[i];
/* 1398 */         if ((flags = (Flags)fr.getItem(Flags.class)) != null) {
/* 1399 */           r[i] = null;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1405 */     notifyResponseHandlers(r);
/* 1406 */     handleResult(r[r.length - 1]);
/* 1407 */     return flags;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UID fetchUID(int msgno) throws ProtocolException {
/* 1414 */     Response[] r = fetch(msgno, "UID");
/*      */ 
/*      */     
/* 1417 */     notifyResponseHandlers(r);
/*      */     
/* 1419 */     Response response = r[r.length - 1];
/* 1420 */     if (response.isOK())
/* 1421 */       return (UID)FetchResponse.getItem(r, msgno, UID.class); 
/* 1422 */     if (response.isNO()) {
/* 1423 */       return null;
/*      */     }
/* 1425 */     handleResult(response);
/* 1426 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UID fetchSequenceNumber(long uid) throws ProtocolException {
/* 1436 */     UID u = null;
/* 1437 */     Response[] r = fetch(String.valueOf(uid), "UID", true);
/*      */     
/* 1439 */     for (int i = 0, len = r.length; i < len; i++) {
/* 1440 */       if (r[i] != null && r[i] instanceof FetchResponse) {
/*      */ 
/*      */         
/* 1443 */         FetchResponse fr = (FetchResponse)r[i];
/* 1444 */         if ((u = (UID)fr.getItem(UID.class)) != null) {
/* 1445 */           if (u.uid == uid) {
/*      */             break;
/*      */           }
/* 1448 */           u = null;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1452 */     notifyResponseHandlers(r);
/* 1453 */     handleResult(r[r.length - 1]);
/* 1454 */     return u;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UID[] fetchSequenceNumbers(long start, long end) throws ProtocolException {
/* 1464 */     Response[] r = fetch(String.valueOf(start) + ":" + ((end == -1L) ? "*" : String.valueOf(end)), "UID", true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1470 */     Vector v = new Vector();
/* 1471 */     for (int i = 0, len = r.length; i < len; i++) {
/* 1472 */       if (r[i] != null && r[i] instanceof FetchResponse) {
/*      */ 
/*      */         
/* 1475 */         FetchResponse fr = (FetchResponse)r[i]; UID u;
/* 1476 */         if ((u = (UID)fr.getItem(UID.class)) != null)
/* 1477 */           v.addElement(u); 
/*      */       } 
/*      */     } 
/* 1480 */     notifyResponseHandlers(r);
/* 1481 */     handleResult(r[r.length - 1]);
/*      */     
/* 1483 */     UID[] ua = new UID[v.size()];
/* 1484 */     v.copyInto((Object[])ua);
/* 1485 */     return ua;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UID[] fetchSequenceNumbers(long[] uids) throws ProtocolException {
/* 1494 */     StringBuffer sb = new StringBuffer();
/* 1495 */     for (int i = 0; i < uids.length; i++) {
/* 1496 */       if (i > 0)
/* 1497 */         sb.append(","); 
/* 1498 */       sb.append(String.valueOf(uids[i]));
/*      */     } 
/*      */     
/* 1501 */     Response[] r = fetch(sb.toString(), "UID", true);
/*      */ 
/*      */     
/* 1504 */     Vector v = new Vector();
/* 1505 */     for (int j = 0, len = r.length; j < len; j++) {
/* 1506 */       if (r[j] != null && r[j] instanceof FetchResponse) {
/*      */ 
/*      */         
/* 1509 */         FetchResponse fr = (FetchResponse)r[j]; UID u;
/* 1510 */         if ((u = (UID)fr.getItem(UID.class)) != null)
/* 1511 */           v.addElement(u); 
/*      */       } 
/*      */     } 
/* 1514 */     notifyResponseHandlers(r);
/* 1515 */     handleResult(r[r.length - 1]);
/*      */     
/* 1517 */     UID[] ua = new UID[v.size()];
/* 1518 */     v.copyInto((Object[])ua);
/* 1519 */     return ua;
/*      */   }
/*      */ 
/*      */   
/*      */   public Response[] fetch(MessageSet[] msgsets, String what) throws ProtocolException {
/* 1524 */     return fetch(MessageSet.toString(msgsets), what, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public Response[] fetch(int start, int end, String what) throws ProtocolException {
/* 1529 */     return fetch(String.valueOf(start) + ":" + String.valueOf(end), what, false);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Response[] fetch(int msg, String what) throws ProtocolException {
/* 1535 */     return fetch(String.valueOf(msg), what, false);
/*      */   }
/*      */ 
/*      */   
/*      */   private Response[] fetch(String msgSequence, String what, boolean uid) throws ProtocolException {
/* 1540 */     if (uid) {
/* 1541 */       return command("UID FETCH " + msgSequence + " (" + what + ")", null);
/*      */     }
/* 1543 */     return command("FETCH " + msgSequence + " (" + what + ")", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copy(MessageSet[] msgsets, String mbox) throws ProtocolException {
/* 1551 */     copy(MessageSet.toString(msgsets), mbox);
/*      */   }
/*      */ 
/*      */   
/*      */   public void copy(int start, int end, String mbox) throws ProtocolException {
/* 1556 */     copy(String.valueOf(start) + ":" + String.valueOf(end), mbox);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void copy(String msgSequence, String mbox) throws ProtocolException {
/* 1563 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 1565 */     Argument args = new Argument();
/* 1566 */     args.writeAtom(msgSequence);
/* 1567 */     args.writeString(mbox);
/*      */     
/* 1569 */     simpleCommand("COPY", args);
/*      */   }
/*      */ 
/*      */   
/*      */   public void storeFlags(MessageSet[] msgsets, Flags flags, boolean set) throws ProtocolException {
/* 1574 */     storeFlags(MessageSet.toString(msgsets), flags, set);
/*      */   }
/*      */ 
/*      */   
/*      */   public void storeFlags(int start, int end, Flags flags, boolean set) throws ProtocolException {
/* 1579 */     storeFlags(String.valueOf(start) + ":" + String.valueOf(end), flags, set);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void storeFlags(int msg, Flags flags, boolean set) throws ProtocolException {
/* 1588 */     storeFlags(String.valueOf(msg), flags, set);
/*      */   }
/*      */ 
/*      */   
/*      */   private void storeFlags(String msgset, Flags flags, boolean set) throws ProtocolException {
/*      */     Response[] r;
/* 1594 */     if (set) {
/* 1595 */       r = command("STORE " + msgset + " +FLAGS " + createFlagList(flags), null);
/*      */     } else {
/*      */       
/* 1598 */       r = command("STORE " + msgset + " -FLAGS " + createFlagList(flags), null);
/*      */     } 
/*      */ 
/*      */     
/* 1602 */     notifyResponseHandlers(r);
/* 1603 */     handleResult(r[r.length - 1]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String createFlagList(Flags flags) {
/* 1610 */     StringBuffer sb = new StringBuffer();
/* 1611 */     sb.append("(");
/*      */     
/* 1613 */     Flags.Flag[] sf = flags.getSystemFlags();
/* 1614 */     boolean first = true;
/* 1615 */     for (int i = 0; i < sf.length; i++) {
/*      */       String s;
/* 1617 */       Flags.Flag f = sf[i];
/* 1618 */       if (f == Flags.Flag.ANSWERED) {
/* 1619 */         s = "\\Answered";
/* 1620 */       } else if (f == Flags.Flag.DELETED) {
/* 1621 */         s = "\\Deleted";
/* 1622 */       } else if (f == Flags.Flag.DRAFT) {
/* 1623 */         s = "\\Draft";
/* 1624 */       } else if (f == Flags.Flag.FLAGGED) {
/* 1625 */         s = "\\Flagged";
/* 1626 */       } else if (f == Flags.Flag.RECENT) {
/* 1627 */         s = "\\Recent";
/* 1628 */       } else if (f == Flags.Flag.SEEN) {
/* 1629 */         s = "\\Seen";
/*      */       } else {
/*      */         continue;
/* 1632 */       }  if (first) {
/* 1633 */         first = false;
/*      */       } else {
/* 1635 */         sb.append(' ');
/* 1636 */       }  sb.append(s);
/*      */       continue;
/*      */     } 
/* 1639 */     String[] uf = flags.getUserFlags();
/* 1640 */     for (int j = 0; j < uf.length; j++) {
/* 1641 */       if (first) {
/* 1642 */         first = false;
/*      */       } else {
/* 1644 */         sb.append(' ');
/* 1645 */       }  sb.append(uf[j]);
/*      */     } 
/*      */     
/* 1648 */     sb.append(")");
/* 1649 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] search(MessageSet[] msgsets, SearchTerm term) throws ProtocolException, SearchException {
/* 1663 */     return search(MessageSet.toString(msgsets), term);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] search(SearchTerm term) throws ProtocolException, SearchException {
/* 1676 */     return search("ALL", term);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] search(String msgSequence, SearchTerm term) throws ProtocolException, SearchException {
/* 1687 */     getSearchSequence(); if (SearchSequence.isAscii(term)) {
/*      */       try {
/* 1689 */         return issueSearch(msgSequence, term, (String)null);
/* 1690 */       } catch (IOException ioex) {}
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1702 */     for (int i = 0; i < this.searchCharsets.length; i++) {
/* 1703 */       if (this.searchCharsets[i] != null) {
/*      */         
/*      */         try {
/*      */           
/* 1707 */           return issueSearch(msgSequence, term, this.searchCharsets[i]);
/* 1708 */         } catch (CommandFailedException cfx) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1715 */           this.searchCharsets[i] = null;
/*      */         }
/* 1717 */         catch (IOException ioex) {
/*      */ 
/*      */         
/* 1720 */         } catch (ProtocolException pex) {
/* 1721 */           throw pex;
/* 1722 */         } catch (SearchException sex) {
/* 1723 */           throw sex;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 1728 */     throw new SearchException("Search failed");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] issueSearch(String msgSequence, SearchTerm term, String charset) throws ProtocolException, SearchException, IOException {
/*      */     Response[] r;
/* 1741 */     Argument args = getSearchSequence().generateSequence(term, (charset == null) ? null : MimeUtility.javaCharset(charset));
/*      */ 
/*      */ 
/*      */     
/* 1745 */     args.writeAtom(msgSequence);
/*      */ 
/*      */ 
/*      */     
/* 1749 */     if (charset == null) {
/* 1750 */       r = command("SEARCH", args);
/*      */     } else {
/* 1752 */       r = command("SEARCH CHARSET " + charset, args);
/*      */     } 
/* 1754 */     Response response = r[r.length - 1];
/* 1755 */     int[] matches = null;
/*      */ 
/*      */     
/* 1758 */     if (response.isOK()) {
/* 1759 */       Vector v = new Vector();
/*      */       
/* 1761 */       for (int i = 0, len = r.length; i < len; i++) {
/* 1762 */         if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 1765 */           IMAPResponse ir = (IMAPResponse)r[i];
/*      */           
/* 1767 */           if (ir.keyEquals("SEARCH")) {
/* 1768 */             int num; while ((num = ir.readNumber()) != -1)
/* 1769 */               v.addElement(new Integer(num)); 
/* 1770 */             r[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1775 */       int vsize = v.size();
/* 1776 */       matches = new int[vsize];
/* 1777 */       for (int j = 0; j < vsize; j++) {
/* 1778 */         matches[j] = ((Integer)v.elementAt(j)).intValue();
/*      */       }
/*      */     } 
/*      */     
/* 1782 */     notifyResponseHandlers(r);
/* 1783 */     handleResult(response);
/* 1784 */     return matches;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SearchSequence getSearchSequence() {
/* 1797 */     if (this.searchSequence == null)
/* 1798 */       this.searchSequence = new SearchSequence(); 
/* 1799 */     return this.searchSequence;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] sort(SortTerm[] term, SearchTerm sterm) throws ProtocolException, SearchException {
/* 1818 */     if (!hasCapability("SORT*")) {
/* 1819 */       throw new BadCommandException("SORT not supported");
/*      */     }
/* 1821 */     if (term == null || term.length == 0) {
/* 1822 */       throw new BadCommandException("Must have at least one sort term");
/*      */     }
/* 1824 */     Argument args = new Argument();
/* 1825 */     Argument sargs = new Argument();
/* 1826 */     for (int i = 0; i < term.length; i++)
/* 1827 */       sargs.writeAtom(term[i].toString()); 
/* 1828 */     args.writeArgument(sargs);
/*      */     
/* 1830 */     args.writeAtom("UTF-8");
/* 1831 */     if (sterm != null) {
/*      */       try {
/* 1833 */         args.append(getSearchSequence().generateSequence(sterm, "UTF-8"));
/*      */       }
/* 1835 */       catch (IOException ioex) {
/*      */         
/* 1837 */         throw new SearchException(ioex.toString());
/*      */       } 
/*      */     } else {
/* 1840 */       args.writeAtom("ALL");
/*      */     } 
/* 1842 */     Response[] r = command("SORT", args);
/* 1843 */     Response response = r[r.length - 1];
/* 1844 */     int[] matches = null;
/*      */ 
/*      */     
/* 1847 */     if (response.isOK()) {
/* 1848 */       Vector v = new Vector();
/*      */       
/* 1850 */       for (int j = 0, len = r.length; j < len; j++) {
/* 1851 */         if (r[j] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 1854 */           IMAPResponse ir = (IMAPResponse)r[j];
/* 1855 */           if (ir.keyEquals("SORT")) {
/* 1856 */             int num; while ((num = ir.readNumber()) != -1)
/* 1857 */               v.addElement(new Integer(num)); 
/* 1858 */             r[j] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1863 */       int vsize = v.size();
/* 1864 */       matches = new int[vsize];
/* 1865 */       for (int k = 0; k < vsize; k++) {
/* 1866 */         matches[k] = ((Integer)v.elementAt(k)).intValue();
/*      */       }
/*      */     } 
/*      */     
/* 1870 */     notifyResponseHandlers(r);
/* 1871 */     handleResult(response);
/* 1872 */     return matches;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Namespaces namespace() throws ProtocolException {
/* 1881 */     if (!hasCapability("NAMESPACE")) {
/* 1882 */       throw new BadCommandException("NAMESPACE not supported");
/*      */     }
/* 1884 */     Response[] r = command("NAMESPACE", null);
/*      */     
/* 1886 */     Namespaces namespace = null;
/* 1887 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/* 1890 */     if (response.isOK()) {
/* 1891 */       for (int i = 0, len = r.length; i < len; i++) {
/* 1892 */         if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 1895 */           IMAPResponse ir = (IMAPResponse)r[i];
/* 1896 */           if (ir.keyEquals("NAMESPACE")) {
/* 1897 */             if (namespace == null)
/* 1898 */               namespace = new Namespaces(ir); 
/* 1899 */             r[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 1905 */     notifyResponseHandlers(r);
/* 1906 */     handleResult(response);
/* 1907 */     return namespace;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Quota[] getQuotaRoot(String mbox) throws ProtocolException {
/* 1920 */     if (!hasCapability("QUOTA")) {
/* 1921 */       throw new BadCommandException("GETQUOTAROOT not supported");
/*      */     }
/*      */     
/* 1924 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 1926 */     Argument args = new Argument();
/* 1927 */     args.writeString(mbox);
/*      */     
/* 1929 */     Response[] r = command("GETQUOTAROOT", args);
/*      */     
/* 1931 */     Response response = r[r.length - 1];
/*      */     
/* 1933 */     Hashtable tab = new Hashtable();
/*      */ 
/*      */     
/* 1936 */     if (response.isOK()) {
/* 1937 */       for (int j = 0, len = r.length; j < len; j++) {
/* 1938 */         if (r[j] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 1941 */           IMAPResponse ir = (IMAPResponse)r[j];
/* 1942 */           if (ir.keyEquals("QUOTAROOT")) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1947 */             ir.readAtomString();
/*      */             
/* 1949 */             String root = null;
/* 1950 */             while ((root = ir.readAtomString()) != null && root.length() > 0)
/*      */             {
/* 1952 */               tab.put(root, new Quota(root)); } 
/* 1953 */             r[j] = null;
/* 1954 */           } else if (ir.keyEquals("QUOTA")) {
/* 1955 */             Quota quota = parseQuota(ir);
/* 1956 */             Quota q = (Quota)tab.get(quota.quotaRoot);
/* 1957 */             if (q == null || q.resources != null);
/*      */ 
/*      */             
/* 1960 */             tab.put(quota.quotaRoot, quota);
/* 1961 */             r[j] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 1967 */     notifyResponseHandlers(r);
/* 1968 */     handleResult(response);
/*      */     
/* 1970 */     Quota[] qa = new Quota[tab.size()];
/* 1971 */     Enumeration e = tab.elements();
/* 1972 */     for (int i = 0; e.hasMoreElements(); i++)
/* 1973 */       qa[i] = e.nextElement(); 
/* 1974 */     return qa;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Quota[] getQuota(String root) throws ProtocolException {
/* 1986 */     if (!hasCapability("QUOTA")) {
/* 1987 */       throw new BadCommandException("QUOTA not supported");
/*      */     }
/* 1989 */     Argument args = new Argument();
/* 1990 */     args.writeString(root);
/*      */     
/* 1992 */     Response[] r = command("GETQUOTA", args);
/*      */     
/* 1994 */     Quota quota = null;
/* 1995 */     Vector v = new Vector();
/* 1996 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/* 1999 */     if (response.isOK()) {
/* 2000 */       for (int i = 0, len = r.length; i < len; i++) {
/* 2001 */         if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 2004 */           IMAPResponse ir = (IMAPResponse)r[i];
/* 2005 */           if (ir.keyEquals("QUOTA")) {
/* 2006 */             quota = parseQuota(ir);
/* 2007 */             v.addElement(quota);
/* 2008 */             r[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 2014 */     notifyResponseHandlers(r);
/* 2015 */     handleResult(response);
/* 2016 */     Quota[] qa = new Quota[v.size()];
/* 2017 */     v.copyInto((Object[])qa);
/* 2018 */     return qa;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setQuota(Quota quota) throws ProtocolException {
/* 2029 */     if (!hasCapability("QUOTA")) {
/* 2030 */       throw new BadCommandException("QUOTA not supported");
/*      */     }
/* 2032 */     Argument args = new Argument();
/* 2033 */     args.writeString(quota.quotaRoot);
/* 2034 */     Argument qargs = new Argument();
/* 2035 */     if (quota.resources != null) {
/* 2036 */       for (int i = 0; i < quota.resources.length; i++) {
/* 2037 */         qargs.writeAtom((quota.resources[i]).name);
/* 2038 */         qargs.writeNumber((quota.resources[i]).limit);
/*      */       } 
/*      */     }
/* 2041 */     args.writeArgument(qargs);
/*      */     
/* 2043 */     Response[] r = command("SETQUOTA", args);
/* 2044 */     Response response = r[r.length - 1];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2071 */     notifyResponseHandlers(r);
/* 2072 */     handleResult(response);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Quota parseQuota(Response r) throws ParsingException {
/* 2085 */     String quotaRoot = r.readAtomString();
/* 2086 */     Quota q = new Quota(quotaRoot);
/* 2087 */     r.skipSpaces();
/*      */     
/* 2089 */     if (r.readByte() != 40) {
/* 2090 */       throw new ParsingException("parse error in QUOTA");
/*      */     }
/* 2092 */     Vector v = new Vector();
/* 2093 */     while (r.peekByte() != 41) {
/*      */       
/* 2095 */       String name = r.readAtom();
/* 2096 */       if (name != null) {
/* 2097 */         long usage = r.readLong();
/* 2098 */         long limit = r.readLong();
/* 2099 */         Quota.Resource res = new Quota.Resource(name, usage, limit);
/* 2100 */         v.addElement(res);
/*      */       } 
/*      */     } 
/* 2103 */     r.readByte();
/* 2104 */     q.resources = new Quota.Resource[v.size()];
/* 2105 */     v.copyInto((Object[])q.resources);
/* 2106 */     return q;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setACL(String mbox, char modifier, ACL acl) throws ProtocolException {
/* 2117 */     if (!hasCapability("ACL")) {
/* 2118 */       throw new BadCommandException("ACL not supported");
/*      */     }
/*      */     
/* 2121 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 2123 */     Argument args = new Argument();
/* 2124 */     args.writeString(mbox);
/* 2125 */     args.writeString(acl.getName());
/* 2126 */     String rights = acl.getRights().toString();
/* 2127 */     if (modifier == '+' || modifier == '-')
/* 2128 */       rights = modifier + rights; 
/* 2129 */     args.writeString(rights);
/*      */     
/* 2131 */     Response[] r = command("SETACL", args);
/* 2132 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/* 2135 */     notifyResponseHandlers(r);
/* 2136 */     handleResult(response);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void deleteACL(String mbox, String user) throws ProtocolException {
/* 2145 */     if (!hasCapability("ACL")) {
/* 2146 */       throw new BadCommandException("ACL not supported");
/*      */     }
/*      */     
/* 2149 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 2151 */     Argument args = new Argument();
/* 2152 */     args.writeString(mbox);
/* 2153 */     args.writeString(user);
/*      */     
/* 2155 */     Response[] r = command("DELETEACL", args);
/* 2156 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/* 2159 */     notifyResponseHandlers(r);
/* 2160 */     handleResult(response);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ACL[] getACL(String mbox) throws ProtocolException {
/* 2169 */     if (!hasCapability("ACL")) {
/* 2170 */       throw new BadCommandException("ACL not supported");
/*      */     }
/*      */     
/* 2173 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 2175 */     Argument args = new Argument();
/* 2176 */     args.writeString(mbox);
/*      */     
/* 2178 */     Response[] r = command("GETACL", args);
/* 2179 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/* 2182 */     Vector v = new Vector();
/* 2183 */     if (response.isOK()) {
/* 2184 */       for (int i = 0, len = r.length; i < len; i++) {
/* 2185 */         if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 2188 */           IMAPResponse ir = (IMAPResponse)r[i];
/* 2189 */           if (ir.keyEquals("ACL")) {
/*      */ 
/*      */ 
/*      */             
/* 2193 */             ir.readAtomString();
/* 2194 */             String name = null;
/* 2195 */             while ((name = ir.readAtomString()) != null) {
/* 2196 */               String rights = ir.readAtomString();
/* 2197 */               if (rights == null)
/*      */                 break; 
/* 2199 */               ACL acl = new ACL(name, new Rights(rights));
/* 2200 */               v.addElement(acl);
/*      */             } 
/* 2202 */             r[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 2208 */     notifyResponseHandlers(r);
/* 2209 */     handleResult(response);
/* 2210 */     ACL[] aa = new ACL[v.size()];
/* 2211 */     v.copyInto((Object[])aa);
/* 2212 */     return aa;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Rights[] listRights(String mbox, String user) throws ProtocolException {
/* 2222 */     if (!hasCapability("ACL")) {
/* 2223 */       throw new BadCommandException("ACL not supported");
/*      */     }
/*      */     
/* 2226 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 2228 */     Argument args = new Argument();
/* 2229 */     args.writeString(mbox);
/* 2230 */     args.writeString(user);
/*      */     
/* 2232 */     Response[] r = command("LISTRIGHTS", args);
/* 2233 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/* 2236 */     Vector v = new Vector();
/* 2237 */     if (response.isOK()) {
/* 2238 */       for (int i = 0, len = r.length; i < len; i++) {
/* 2239 */         if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 2242 */           IMAPResponse ir = (IMAPResponse)r[i];
/* 2243 */           if (ir.keyEquals("LISTRIGHTS")) {
/*      */ 
/*      */ 
/*      */             
/* 2247 */             ir.readAtomString();
/*      */             
/* 2249 */             ir.readAtomString();
/*      */             String rights;
/* 2251 */             while ((rights = ir.readAtomString()) != null)
/* 2252 */               v.addElement(new Rights(rights)); 
/* 2253 */             r[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 2259 */     notifyResponseHandlers(r);
/* 2260 */     handleResult(response);
/* 2261 */     Rights[] ra = new Rights[v.size()];
/* 2262 */     v.copyInto((Object[])ra);
/* 2263 */     return ra;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Rights myRights(String mbox) throws ProtocolException {
/* 2272 */     if (!hasCapability("ACL")) {
/* 2273 */       throw new BadCommandException("ACL not supported");
/*      */     }
/*      */     
/* 2276 */     mbox = BASE64MailboxEncoder.encode(mbox);
/*      */     
/* 2278 */     Argument args = new Argument();
/* 2279 */     args.writeString(mbox);
/*      */     
/* 2281 */     Response[] r = command("MYRIGHTS", args);
/* 2282 */     Response response = r[r.length - 1];
/*      */ 
/*      */     
/* 2285 */     Rights rights = null;
/* 2286 */     if (response.isOK()) {
/* 2287 */       for (int i = 0, len = r.length; i < len; i++) {
/* 2288 */         if (r[i] instanceof IMAPResponse) {
/*      */ 
/*      */           
/* 2291 */           IMAPResponse ir = (IMAPResponse)r[i];
/* 2292 */           if (ir.keyEquals("MYRIGHTS")) {
/*      */ 
/*      */             
/* 2295 */             ir.readAtomString();
/* 2296 */             String rs = ir.readAtomString();
/* 2297 */             if (rights == null)
/* 2298 */               rights = new Rights(rs); 
/* 2299 */             r[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 2305 */     notifyResponseHandlers(r);
/* 2306 */     handleResult(response);
/* 2307 */     return rights;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void idleStart() throws ProtocolException {
/* 2334 */     if (!hasCapability("IDLE")) {
/* 2335 */       throw new BadCommandException("IDLE not supported");
/*      */     }
/* 2337 */     Vector v = new Vector();
/* 2338 */     boolean done = false;
/* 2339 */     Response r = null;
/*      */ 
/*      */     
/*      */     try {
/* 2343 */       this.idleTag = writeCommand("IDLE", null);
/* 2344 */     } catch (LiteralException lex) {
/* 2345 */       v.addElement(lex.getResponse());
/* 2346 */       done = true;
/* 2347 */     } catch (Exception ex) {
/*      */       
/* 2349 */       v.addElement(Response.byeResponse(ex));
/* 2350 */       done = true;
/*      */     } 
/*      */     
/* 2353 */     while (!done) {
/*      */       try {
/* 2355 */         r = readResponse();
/* 2356 */       } catch (IOException ioex) {
/*      */         
/* 2358 */         r = Response.byeResponse(ioex);
/* 2359 */       } catch (ProtocolException pex) {
/*      */         continue;
/*      */       } 
/*      */       
/* 2363 */       v.addElement(r);
/*      */       
/* 2365 */       if (r.isContinuation() || r.isBYE()) {
/* 2366 */         done = true;
/*      */       }
/*      */     } 
/* 2369 */     Response[] responses = new Response[v.size()];
/* 2370 */     v.copyInto((Object[])responses);
/* 2371 */     r = responses[responses.length - 1];
/*      */ 
/*      */     
/* 2374 */     notifyResponseHandlers(responses);
/* 2375 */     if (!r.isContinuation()) {
/* 2376 */       handleResult(r);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Response readIdleResponse() {
/* 2389 */     if (this.idleTag == null)
/* 2390 */       return null; 
/* 2391 */     Response r = null;
/* 2392 */     while (r == null) {
/*      */       try {
/* 2394 */         r = readResponse();
/* 2395 */       } catch (InterruptedIOException iioex) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2402 */         if (iioex.bytesTransferred == 0) {
/* 2403 */           r = null;
/*      */           continue;
/*      */         } 
/* 2406 */         r = Response.byeResponse(iioex);
/* 2407 */       } catch (IOException ioex) {
/*      */         
/* 2409 */         r = Response.byeResponse(ioex);
/* 2410 */       } catch (ProtocolException pex) {
/*      */         
/* 2412 */         r = Response.byeResponse((Exception)pex);
/*      */       } 
/*      */     } 
/* 2415 */     return r;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean processIdleResponse(Response r) throws ProtocolException {
/* 2426 */     Response[] responses = new Response[1];
/* 2427 */     responses[0] = r;
/* 2428 */     boolean done = false;
/* 2429 */     notifyResponseHandlers(responses);
/*      */     
/* 2431 */     if (r.isBYE()) {
/* 2432 */       done = true;
/*      */     }
/*      */     
/* 2435 */     if (r.isTagged() && r.getTag().equals(this.idleTag)) {
/* 2436 */       done = true;
/*      */     }
/* 2438 */     if (done) {
/* 2439 */       this.idleTag = null;
/*      */     }
/* 2441 */     handleResult(r);
/* 2442 */     return !done;
/*      */   }
/*      */ 
/*      */   
/* 2446 */   private static final byte[] DONE = new byte[] { 68, 79, 78, 69, 13, 10 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void idleAbort() throws ProtocolException {
/* 2459 */     OutputStream os = getOutputStream();
/*      */     try {
/* 2461 */       os.write(DONE);
/* 2462 */       os.flush();
/* 2463 */     } catch (IOException ex) {}
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\IMAPProtocol.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */