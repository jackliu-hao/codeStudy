/*     */ package ch.qos.logback.core.net;
/*     */ 
/*     */ import ch.qos.logback.core.AppenderBase;
/*     */ import ch.qos.logback.core.Layout;
/*     */ import ch.qos.logback.core.boolex.EvaluationException;
/*     */ import ch.qos.logback.core.boolex.EventEvaluator;
/*     */ import ch.qos.logback.core.helpers.CyclicBuffer;
/*     */ import ch.qos.logback.core.pattern.PatternLayoutBase;
/*     */ import ch.qos.logback.core.sift.DefaultDiscriminator;
/*     */ import ch.qos.logback.core.sift.Discriminator;
/*     */ import ch.qos.logback.core.spi.CyclicBufferTracker;
/*     */ import ch.qos.logback.core.util.ContentTypeUtil;
/*     */ import ch.qos.logback.core.util.JNDIUtil;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.mail.Address;
/*     */ import javax.mail.BodyPart;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.Transport;
/*     */ import javax.mail.internet.AddressException;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ import javax.naming.Context;
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
/*     */ public abstract class SMTPAppenderBase<E>
/*     */   extends AppenderBase<E>
/*     */ {
/*  63 */   static InternetAddress[] EMPTY_IA_ARRAY = new InternetAddress[0];
/*     */   
/*     */   static final long MAX_DELAY_BETWEEN_STATUS_MESSAGES = 1228800000L;
/*     */   
/*  67 */   long lastTrackerStatusPrint = 0L;
/*  68 */   long delayBetweenStatusMessages = 300000L;
/*     */   
/*     */   protected Layout<E> subjectLayout;
/*     */   
/*     */   protected Layout<E> layout;
/*  73 */   private List<PatternLayoutBase<E>> toPatternLayoutList = new ArrayList<PatternLayoutBase<E>>();
/*     */   private String from;
/*  75 */   private String subjectStr = null;
/*     */   private String smtpHost;
/*  77 */   private int smtpPort = 25;
/*     */   private boolean starttls = false;
/*     */   private boolean ssl = false;
/*     */   private boolean sessionViaJNDI = false;
/*  81 */   private String jndiLocation = "java:comp/env/mail/Session";
/*     */   
/*     */   String username;
/*     */   
/*     */   String password;
/*     */   
/*     */   String localhost;
/*     */   boolean asynchronousSending = true;
/*  89 */   private String charsetEncoding = "UTF-8";
/*     */   
/*     */   protected Session session;
/*     */   
/*     */   protected EventEvaluator<E> eventEvaluator;
/*     */   
/*  95 */   protected Discriminator<E> discriminator = (Discriminator<E>)new DefaultDiscriminator();
/*     */   
/*     */   protected CyclicBufferTracker<E> cbTracker;
/*  98 */   private int errorCount = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Layout<E> makeSubjectLayout(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 115 */     if (this.cbTracker == null) {
/* 116 */       this.cbTracker = new CyclicBufferTracker();
/*     */     }
/*     */     
/* 119 */     if (this.sessionViaJNDI) {
/* 120 */       this.session = lookupSessionInJNDI();
/*     */     } else {
/* 122 */       this.session = buildSessionFromProperties();
/*     */     } 
/* 124 */     if (this.session == null) {
/* 125 */       addError("Failed to obtain javax.mail.Session. Cannot start.");
/*     */       
/*     */       return;
/*     */     } 
/* 129 */     this.subjectLayout = makeSubjectLayout(this.subjectStr);
/*     */     
/* 131 */     this.started = true;
/*     */   }
/*     */   
/*     */   private Session lookupSessionInJNDI() {
/* 135 */     addInfo("Looking up javax.mail.Session at JNDI location [" + this.jndiLocation + "]");
/*     */     try {
/* 137 */       Context initialContext = JNDIUtil.getInitialContext();
/* 138 */       Object obj = JNDIUtil.lookupObject(initialContext, this.jndiLocation);
/* 139 */       return (Session)obj;
/* 140 */     } catch (Exception e) {
/* 141 */       addError("Failed to obtain javax.mail.Session from JNDI location [" + this.jndiLocation + "]");
/* 142 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Session buildSessionFromProperties() {
/* 147 */     Properties props = new Properties(OptionHelper.getSystemProperties());
/* 148 */     if (this.smtpHost != null) {
/* 149 */       props.put("mail.smtp.host", this.smtpHost);
/*     */     }
/* 151 */     props.put("mail.smtp.port", Integer.toString(this.smtpPort));
/*     */     
/* 153 */     if (this.localhost != null) {
/* 154 */       props.put("mail.smtp.localhost", this.localhost);
/*     */     }
/*     */     
/* 157 */     LoginAuthenticator loginAuthenticator = null;
/*     */     
/* 159 */     if (!OptionHelper.isEmpty(this.username)) {
/* 160 */       loginAuthenticator = new LoginAuthenticator(this.username, this.password);
/* 161 */       props.put("mail.smtp.auth", "true");
/*     */     } 
/*     */     
/* 164 */     if (isSTARTTLS() && isSSL()) {
/* 165 */       addError("Both SSL and StartTLS cannot be enabled simultaneously");
/*     */     } else {
/* 167 */       if (isSTARTTLS())
/*     */       {
/* 169 */         props.put("mail.smtp.starttls.enable", "true");
/*     */       }
/* 171 */       if (isSSL()) {
/* 172 */         String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
/* 173 */         props.put("mail.smtp.socketFactory.port", Integer.toString(this.smtpPort));
/* 174 */         props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
/* 175 */         props.put("mail.smtp.socketFactory.fallback", "true");
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 181 */     return Session.getInstance(props, loginAuthenticator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void append(E eventObject) {
/* 190 */     if (!checkEntryConditions()) {
/*     */       return;
/*     */     }
/*     */     
/* 194 */     String key = this.discriminator.getDiscriminatingValue(eventObject);
/* 195 */     long now = System.currentTimeMillis();
/* 196 */     CyclicBuffer<E> cb = (CyclicBuffer<E>)this.cbTracker.getOrCreate(key, now);
/* 197 */     subAppend(cb, eventObject);
/*     */     
/*     */     try {
/* 200 */       if (this.eventEvaluator.evaluate(eventObject)) {
/*     */         
/* 202 */         CyclicBuffer<E> cbClone = new CyclicBuffer(cb);
/*     */         
/* 204 */         cb.clear();
/*     */         
/* 206 */         if (this.asynchronousSending) {
/*     */           
/* 208 */           SenderRunnable senderRunnable = new SenderRunnable(cbClone, eventObject);
/* 209 */           this.context.getScheduledExecutorService().execute(senderRunnable);
/*     */         } else {
/*     */           
/* 212 */           sendBuffer(cbClone, eventObject);
/*     */         } 
/*     */       } 
/* 215 */     } catch (EvaluationException ex) {
/* 216 */       this.errorCount++;
/* 217 */       if (this.errorCount < 4) {
/* 218 */         addError("SMTPAppender's EventEvaluator threw an Exception-", (Throwable)ex);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 223 */     if (eventMarksEndOfLife(eventObject)) {
/* 224 */       this.cbTracker.endOfLife(key);
/*     */     }
/*     */     
/* 227 */     this.cbTracker.removeStaleComponents(now);
/*     */     
/* 229 */     if (this.lastTrackerStatusPrint + this.delayBetweenStatusMessages < now) {
/* 230 */       addInfo("SMTPAppender [" + this.name + "] is tracking [" + this.cbTracker.getComponentCount() + "] buffers");
/* 231 */       this.lastTrackerStatusPrint = now;
/*     */       
/* 233 */       if (this.delayBetweenStatusMessages < 1228800000L) {
/* 234 */         this.delayBetweenStatusMessages *= 4L;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean eventMarksEndOfLife(E paramE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void subAppend(CyclicBuffer<E> paramCyclicBuffer, E paramE);
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkEntryConditions() {
/* 252 */     if (!this.started) {
/* 253 */       addError("Attempting to append to a non-started appender: " + getName());
/* 254 */       return false;
/*     */     } 
/*     */     
/* 257 */     if (this.eventEvaluator == null) {
/* 258 */       addError("No EventEvaluator is set for appender [" + this.name + "].");
/* 259 */       return false;
/*     */     } 
/*     */     
/* 262 */     if (this.layout == null) {
/* 263 */       addError("No layout set for appender named [" + this.name + "]. For more information, please visit http://logback.qos.ch/codes.html#smtp_no_layout");
/* 264 */       return false;
/*     */     } 
/* 266 */     return true;
/*     */   }
/*     */   
/*     */   public synchronized void stop() {
/* 270 */     this.started = false;
/*     */   }
/*     */   
/*     */   InternetAddress getAddress(String addressStr) {
/*     */     try {
/* 275 */       return new InternetAddress(addressStr);
/* 276 */     } catch (AddressException e) {
/* 277 */       addError("Could not parse address [" + addressStr + "].", (Throwable)e);
/* 278 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private List<InternetAddress> parseAddress(E event) {
/* 283 */     int len = this.toPatternLayoutList.size();
/*     */     
/* 285 */     List<InternetAddress> iaList = new ArrayList<InternetAddress>();
/*     */     
/* 287 */     for (int i = 0; i < len; i++) {
/*     */       try {
/* 289 */         PatternLayoutBase<E> emailPL = this.toPatternLayoutList.get(i);
/* 290 */         String emailAdrr = emailPL.doLayout(event);
/* 291 */         if (emailAdrr != null && emailAdrr.length() != 0)
/*     */         
/*     */         { 
/* 294 */           InternetAddress[] tmp = InternetAddress.parse(emailAdrr, true);
/* 295 */           iaList.addAll(Arrays.asList(tmp)); } 
/* 296 */       } catch (AddressException e) {
/* 297 */         addError("Could not parse email address for [" + this.toPatternLayoutList.get(i) + "] for event [" + event + "]", (Throwable)e);
/* 298 */         return iaList;
/*     */       } 
/*     */     } 
/*     */     
/* 302 */     return iaList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<PatternLayoutBase<E>> getToList() {
/* 309 */     return this.toPatternLayoutList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sendBuffer(CyclicBuffer<E> cb, E lastEventObject) {
/*     */     try {
/* 320 */       MimeBodyPart part = new MimeBodyPart();
/*     */       
/* 322 */       StringBuffer sbuf = new StringBuffer();
/*     */       
/* 324 */       String header = this.layout.getFileHeader();
/* 325 */       if (header != null) {
/* 326 */         sbuf.append(header);
/*     */       }
/* 328 */       String presentationHeader = this.layout.getPresentationHeader();
/* 329 */       if (presentationHeader != null) {
/* 330 */         sbuf.append(presentationHeader);
/*     */       }
/* 332 */       fillBuffer(cb, sbuf);
/* 333 */       String presentationFooter = this.layout.getPresentationFooter();
/* 334 */       if (presentationFooter != null) {
/* 335 */         sbuf.append(presentationFooter);
/*     */       }
/* 337 */       String footer = this.layout.getFileFooter();
/* 338 */       if (footer != null) {
/* 339 */         sbuf.append(footer);
/*     */       }
/*     */       
/* 342 */       String subjectStr = "Undefined subject";
/* 343 */       if (this.subjectLayout != null) {
/* 344 */         subjectStr = this.subjectLayout.doLayout(lastEventObject);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 349 */         int newLinePos = (subjectStr != null) ? subjectStr.indexOf('\n') : -1;
/* 350 */         if (newLinePos > -1) {
/* 351 */           subjectStr = subjectStr.substring(0, newLinePos);
/*     */         }
/*     */       } 
/*     */       
/* 355 */       MimeMessage mimeMsg = new MimeMessage(this.session);
/*     */       
/* 357 */       if (this.from != null) {
/* 358 */         mimeMsg.setFrom((Address)getAddress(this.from));
/*     */       } else {
/* 360 */         mimeMsg.setFrom();
/*     */       } 
/*     */       
/* 363 */       mimeMsg.setSubject(subjectStr, this.charsetEncoding);
/*     */       
/* 365 */       List<InternetAddress> destinationAddresses = parseAddress(lastEventObject);
/* 366 */       if (destinationAddresses.isEmpty()) {
/* 367 */         addInfo("Empty destination address. Aborting email transmission");
/*     */         
/*     */         return;
/*     */       } 
/* 371 */       InternetAddress[] toAddressArray = destinationAddresses.<InternetAddress>toArray(EMPTY_IA_ARRAY);
/* 372 */       mimeMsg.setRecipients(Message.RecipientType.TO, (Address[])toAddressArray);
/*     */       
/* 374 */       String contentType = this.layout.getContentType();
/*     */       
/* 376 */       if (ContentTypeUtil.isTextual(contentType)) {
/* 377 */         part.setText(sbuf.toString(), this.charsetEncoding, ContentTypeUtil.getSubType(contentType));
/*     */       } else {
/* 379 */         part.setContent(sbuf.toString(), this.layout.getContentType());
/*     */       } 
/*     */       
/* 382 */       MimeMultipart mimeMultipart = new MimeMultipart();
/* 383 */       mimeMultipart.addBodyPart((BodyPart)part);
/* 384 */       mimeMsg.setContent((Multipart)mimeMultipart);
/*     */       
/* 386 */       mimeMsg.setSentDate(new Date());
/* 387 */       addInfo("About to send out SMTP message \"" + subjectStr + "\" to " + Arrays.toString((Object[])toAddressArray));
/* 388 */       Transport.send((Message)mimeMsg);
/* 389 */     } catch (Exception e) {
/* 390 */       addError("Error occurred while sending e-mail notification.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void fillBuffer(CyclicBuffer<E> paramCyclicBuffer, StringBuffer paramStringBuffer);
/*     */ 
/*     */   
/*     */   public String getFrom() {
/* 400 */     return this.from;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSubject() {
/* 407 */     return this.subjectStr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFrom(String from) {
/* 415 */     this.from = from;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSubject(String subject) {
/* 423 */     this.subjectStr = subject;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSMTPHost(String smtpHost) {
/* 432 */     setSmtpHost(smtpHost);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSmtpHost(String smtpHost) {
/* 440 */     this.smtpHost = smtpHost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSMTPHost() {
/* 447 */     return getSmtpHost();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSmtpHost() {
/* 454 */     return this.smtpHost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSMTPPort(int port) {
/* 463 */     setSmtpPort(port);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSmtpPort(int port) {
/* 472 */     this.smtpPort = port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSMTPPort() {
/* 481 */     return getSmtpPort();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSmtpPort() {
/* 490 */     return this.smtpPort;
/*     */   }
/*     */   
/*     */   public String getLocalhost() {
/* 494 */     return this.localhost;
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
/*     */   public void setLocalhost(String localhost) {
/* 508 */     this.localhost = localhost;
/*     */   }
/*     */   
/*     */   public CyclicBufferTracker<E> getCyclicBufferTracker() {
/* 512 */     return this.cbTracker;
/*     */   }
/*     */   
/*     */   public void setCyclicBufferTracker(CyclicBufferTracker<E> cbTracker) {
/* 516 */     this.cbTracker = cbTracker;
/*     */   }
/*     */   
/*     */   public Discriminator<E> getDiscriminator() {
/* 520 */     return this.discriminator;
/*     */   }
/*     */   
/*     */   public void setDiscriminator(Discriminator<E> discriminator) {
/* 524 */     this.discriminator = discriminator;
/*     */   }
/*     */   
/*     */   public boolean isAsynchronousSending() {
/* 528 */     return this.asynchronousSending;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAsynchronousSending(boolean asynchronousSending) {
/* 539 */     this.asynchronousSending = asynchronousSending;
/*     */   }
/*     */   
/*     */   public void addTo(String to) {
/* 543 */     if (to == null || to.length() == 0) {
/* 544 */       throw new IllegalArgumentException("Null or empty <to> property");
/*     */     }
/* 546 */     PatternLayoutBase<E> plb = makeNewToPatternLayout(to.trim());
/* 547 */     plb.setContext(this.context);
/* 548 */     plb.start();
/* 549 */     this.toPatternLayoutList.add(plb);
/*     */   }
/*     */   
/*     */   protected abstract PatternLayoutBase<E> makeNewToPatternLayout(String paramString);
/*     */   
/*     */   public List<String> getToAsListOfString() {
/* 555 */     List<String> toList = new ArrayList<String>();
/* 556 */     for (PatternLayoutBase<E> plb : this.toPatternLayoutList) {
/* 557 */       toList.add(plb.getPattern());
/*     */     }
/* 559 */     return toList;
/*     */   }
/*     */   
/*     */   public boolean isSTARTTLS() {
/* 563 */     return this.starttls;
/*     */   }
/*     */   
/*     */   public void setSTARTTLS(boolean startTLS) {
/* 567 */     this.starttls = startTLS;
/*     */   }
/*     */   
/*     */   public boolean isSSL() {
/* 571 */     return this.ssl;
/*     */   }
/*     */   
/*     */   public void setSSL(boolean ssl) {
/* 575 */     this.ssl = ssl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEvaluator(EventEvaluator<E> eventEvaluator) {
/* 585 */     this.eventEvaluator = eventEvaluator;
/*     */   }
/*     */   
/*     */   public String getUsername() {
/* 589 */     return this.username;
/*     */   }
/*     */   
/*     */   public void setUsername(String username) {
/* 593 */     this.username = username;
/*     */   }
/*     */   
/*     */   public String getPassword() {
/* 597 */     return this.password;
/*     */   }
/*     */   
/*     */   public void setPassword(String password) {
/* 601 */     this.password = password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCharsetEncoding() {
/* 609 */     return this.charsetEncoding;
/*     */   }
/*     */   
/*     */   public String getJndiLocation() {
/* 613 */     return this.jndiLocation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setJndiLocation(String jndiLocation) {
/* 624 */     this.jndiLocation = jndiLocation;
/*     */   }
/*     */   
/*     */   public boolean isSessionViaJNDI() {
/* 628 */     return this.sessionViaJNDI;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSessionViaJNDI(boolean sessionViaJNDI) {
/* 638 */     this.sessionViaJNDI = sessionViaJNDI;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharsetEncoding(String charsetEncoding) {
/* 648 */     this.charsetEncoding = charsetEncoding;
/*     */   }
/*     */   
/*     */   public Layout<E> getLayout() {
/* 652 */     return this.layout;
/*     */   }
/*     */   
/*     */   public void setLayout(Layout<E> layout) {
/* 656 */     this.layout = layout;
/*     */   }
/*     */   
/*     */   class SenderRunnable
/*     */     implements Runnable {
/*     */     final CyclicBuffer<E> cyclicBuffer;
/*     */     final E e;
/*     */     
/*     */     SenderRunnable(CyclicBuffer<E> cyclicBuffer, E e) {
/* 665 */       this.cyclicBuffer = cyclicBuffer;
/* 666 */       this.e = e;
/*     */     }
/*     */     
/*     */     public void run() {
/* 670 */       SMTPAppenderBase.this.sendBuffer(this.cyclicBuffer, this.e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\SMTPAppenderBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */