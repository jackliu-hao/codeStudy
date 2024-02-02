/*      */ package com.sun.mail.util.logging;
/*      */ 
/*      */ import com.sun.mail.smtp.SMTPTransport;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URLConnection;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.charset.Charset;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.logging.ErrorManager;
/*      */ import java.util.logging.Filter;
/*      */ import java.util.logging.Formatter;
/*      */ import java.util.logging.Handler;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.LogManager;
/*      */ import java.util.logging.LogRecord;
/*      */ import java.util.logging.SimpleFormatter;
/*      */ import javax.activation.DataHandler;
/*      */ import javax.activation.DataSource;
/*      */ import javax.activation.FileTypeMap;
/*      */ import javax.mail.Address;
/*      */ import javax.mail.Authenticator;
/*      */ import javax.mail.BodyPart;
/*      */ import javax.mail.Message;
/*      */ import javax.mail.MessageContext;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.Multipart;
/*      */ import javax.mail.Part;
/*      */ import javax.mail.PasswordAuthentication;
/*      */ import javax.mail.SendFailedException;
/*      */ import javax.mail.Session;
/*      */ import javax.mail.Transport;
/*      */ import javax.mail.internet.AddressException;
/*      */ import javax.mail.internet.ContentType;
/*      */ import javax.mail.internet.InternetAddress;
/*      */ import javax.mail.internet.MimeBodyPart;
/*      */ import javax.mail.internet.MimeMessage;
/*      */ import javax.mail.internet.MimeMultipart;
/*      */ import javax.mail.internet.MimePart;
/*      */ import javax.mail.internet.MimeUtility;
/*      */ import javax.mail.util.ByteArrayDataSource;
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
/*      */ public class MailHandler
/*      */   extends Handler
/*      */ {
/*      */   private static final Filter[] EMPTY_FILTERS;
/*      */   private static final Formatter[] EMPTY_FORMATTERS;
/*      */   private static final int MIN_HEADER_SIZE = 1024;
/*      */   private static final int offValue;
/*      */   private static final GetAndSetContext GET_AND_SET_CCL;
/*      */   private static final ThreadLocal MUTEX;
/*      */   private static final Object MUTEX_PUBLISH;
/*      */   private static final Object MUTEX_REPORT;
/*      */   private static final Method REMOVE;
/*      */   private volatile boolean sealed;
/*      */   private boolean isWriting;
/*      */   private Properties mailProps;
/*      */   private Authenticator auth;
/*      */   private Session session;
/*      */   private LogRecord[] data;
/*      */   private int size;
/*      */   private int capacity;
/*      */   private Comparator comparator;
/*      */   private Formatter subjectFormatter;
/*      */   private Level pushLevel;
/*      */   private Filter pushFilter;
/*      */   private Filter[] attachmentFilters;
/*      */   private Formatter[] attachmentFormatters;
/*      */   private Formatter[] attachmentNames;
/*      */   private FileTypeMap contentTypes;
/*  314 */   static final boolean $assertionsDisabled = !MailHandler.class.desiredAssertionStatus();
/*      */   static Class array$Ljava$util$logging$Filter;
/*      */   
/*      */   static {
/*  318 */     EMPTY_FILTERS = new Filter[0];
/*      */ 
/*      */ 
/*      */     
/*  322 */     EMPTY_FORMATTERS = new Formatter[0];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  330 */     offValue = Level.OFF.intValue();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  335 */     GET_AND_SET_CCL = new GetAndSetContext(MailHandler.class);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  344 */     MUTEX = new ThreadLocal();
/*      */ 
/*      */ 
/*      */     
/*  348 */     MUTEX_PUBLISH = Level.ALL;
/*      */ 
/*      */ 
/*      */     
/*  352 */     MUTEX_REPORT = Level.OFF;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  362 */       method = ThreadLocal.class.getMethod("remove", (Class[])null);
/*  363 */     } catch (RuntimeException noAccess) {
/*  364 */       method = null;
/*  365 */     } catch (Exception javaOnePointFour) {
/*  366 */       method = null;
/*      */     } 
/*  368 */     REMOVE = method;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static Class array$Ljava$util$logging$Formatter;
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
/*      */   static {
/*      */     Method method;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MailHandler() {
/*  465 */     init(true);
/*  466 */     this.sealed = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MailHandler(int capacity) {
/*  477 */     init(true);
/*  478 */     this.sealed = true;
/*  479 */     setCapacity0(capacity);
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
/*      */   public MailHandler(Properties props) {
/*  493 */     init(false);
/*  494 */     this.sealed = true;
/*  495 */     setMailProperties0(props);
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
/*      */   public boolean isLoggable(LogRecord record) {
/*  511 */     int levelValue = getLevel().intValue();
/*  512 */     if (record.getLevel().intValue() < levelValue || levelValue == offValue) {
/*  513 */       return false;
/*      */     }
/*      */     
/*  516 */     Filter body = getFilter();
/*  517 */     if (body == null || body.isLoggable(record)) {
/*  518 */       return true;
/*      */     }
/*      */     
/*  521 */     return isAttachmentLoggable(record);
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
/*      */   public void publish(LogRecord record) {
/*  545 */     if (tryMutex()) {
/*      */       try {
/*  547 */         if (isLoggable(record)) {
/*  548 */           record.getSourceMethodName();
/*  549 */           publish0(record);
/*      */         } 
/*      */       } finally {
/*  552 */         releaseMutex();
/*      */       } 
/*      */     } else {
/*  555 */       reportUnPublishedError(record);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void publish0(LogRecord record) {
/*      */     MessageContext ctx;
/*      */     boolean priority;
/*  567 */     synchronized (this) {
/*  568 */       if (this.size == this.data.length && this.size < this.capacity) {
/*  569 */         grow();
/*      */       }
/*      */       
/*  572 */       if (this.size < this.data.length) {
/*  573 */         this.data[this.size] = record;
/*  574 */         this.size++;
/*  575 */         priority = isPushable(record);
/*  576 */         if (priority || this.size >= this.capacity) {
/*  577 */           ctx = writeLogRecords(1);
/*      */         } else {
/*  579 */           ctx = null;
/*      */         } 
/*      */       } else {
/*  582 */         priority = false;
/*  583 */         ctx = null;
/*      */       } 
/*      */     } 
/*      */     
/*  587 */     if (ctx != null) {
/*  588 */       send(ctx, priority, 1);
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
/*      */   private void reportUnPublishedError(LogRecord record) {
/*  601 */     if (MUTEX_PUBLISH.equals(MUTEX.get())) {
/*  602 */       MUTEX.set(MUTEX_REPORT);
/*      */       try {
/*      */         String msg;
/*  605 */         if (record != null) {
/*  606 */           SimpleFormatter f = new SimpleFormatter();
/*  607 */           msg = "Log record " + record.getSequenceNumber() + " was not published. " + head(f) + format(f, record) + tail(f, "");
/*      */         }
/*      */         else {
/*      */           
/*  611 */           msg = null;
/*      */         } 
/*  613 */         Exception e = new IllegalStateException("Recursive publish detected by thread " + Thread.currentThread());
/*      */ 
/*      */         
/*  616 */         reportError(msg, e, 1);
/*      */       } finally {
/*  618 */         MUTEX.set(MUTEX_PUBLISH);
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
/*      */   private boolean tryMutex() {
/*  631 */     if (MUTEX.get() == null) {
/*  632 */       MUTEX.set(MUTEX_PUBLISH);
/*  633 */       return true;
/*      */     } 
/*  635 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void releaseMutex() {
/*  645 */     if (REMOVE != null) {
/*      */       try {
/*  647 */         REMOVE.invoke(MUTEX, (Object[])null);
/*  648 */       } catch (RuntimeException ignore) {
/*  649 */         MUTEX.set(null);
/*  650 */       } catch (Exception ignore) {
/*  651 */         MUTEX.set(null);
/*      */       } 
/*      */     } else {
/*  654 */       MUTEX.set(null);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void push() {
/*  665 */     push(true, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void flush() {
/*  675 */     push(false, 2);
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
/*      */   public void close() {
/*  694 */     Object ccl = getAndSetContextClassLoader();
/*      */     try {
/*  696 */       MessageContext ctx = null;
/*  697 */       synchronized (this) {
/*  698 */         super.setLevel(Level.OFF);
/*      */         try {
/*  700 */           ctx = writeLogRecords(3);
/*      */ 
/*      */         
/*      */         }
/*      */         finally {
/*      */ 
/*      */           
/*  707 */           if (this.capacity > 0) {
/*  708 */             this.capacity = -this.capacity;
/*      */           }
/*      */ 
/*      */           
/*  712 */           if (this.size == 0 && this.data.length != 1) {
/*  713 */             this.data = new LogRecord[1];
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  718 */       if (ctx != null) {
/*  719 */         send(ctx, false, 3);
/*      */       }
/*      */     } finally {
/*  722 */       setContextClassLoader(ccl);
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
/*      */   
/*      */   public synchronized void setLevel(Level newLevel) {
/*  736 */     if (this.capacity > 0) {
/*  737 */       super.setLevel(newLevel);
/*      */     } else {
/*  739 */       if (newLevel == null) {
/*  740 */         throw new NullPointerException();
/*      */       }
/*  742 */       checkAccess();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized Level getPushLevel() {
/*  752 */     return this.pushLevel;
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
/*      */   public final synchronized void setPushLevel(Level level) {
/*  767 */     checkAccess();
/*  768 */     if (level == null) {
/*  769 */       throw new NullPointerException();
/*      */     }
/*      */     
/*  772 */     if (this.isWriting) {
/*  773 */       throw new IllegalStateException();
/*      */     }
/*  775 */     this.pushLevel = level;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized Filter getPushFilter() {
/*  783 */     return this.pushFilter;
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
/*      */   public final synchronized void setPushFilter(Filter filter) {
/*  798 */     checkAccess();
/*  799 */     if (this.isWriting) {
/*  800 */       throw new IllegalStateException();
/*      */     }
/*  802 */     this.pushFilter = filter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized Comparator getComparator() {
/*  811 */     return this.comparator;
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
/*      */   public final synchronized void setComparator(Comparator c) {
/*  823 */     checkAccess();
/*  824 */     if (this.isWriting) {
/*  825 */       throw new IllegalStateException();
/*      */     }
/*  827 */     this.comparator = c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized int getCapacity() {
/*  837 */     assert this.capacity != Integer.MIN_VALUE && this.capacity != 0 : this.capacity;
/*  838 */     return Math.abs(this.capacity);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized Authenticator getAuthenticator() {
/*  848 */     checkAccess();
/*  849 */     return this.auth;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setAuthenticator(Authenticator auth) {
/*  860 */     setAuthenticator0(auth);
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
/*      */   public final void setAuthenticator(char[] password) {
/*  873 */     if (password == null) {
/*  874 */       setAuthenticator0((Authenticator)null);
/*      */     } else {
/*  876 */       setAuthenticator0(new DefaultAuthenticator(new String(password)));
/*      */     } 
/*      */   }
/*      */   private void setAuthenticator0(Authenticator auth) {
/*      */     Session settings;
/*  881 */     checkAccess();
/*      */ 
/*      */     
/*  884 */     synchronized (this) {
/*  885 */       if (this.isWriting) {
/*  886 */         throw new IllegalStateException();
/*      */       }
/*  888 */       this.auth = auth;
/*  889 */       settings = fixUpSession();
/*      */     } 
/*  891 */     verifySettings(settings);
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
/*      */   public final void setMailProperties(Properties props) {
/*  906 */     setMailProperties0(props);
/*      */   }
/*      */   private void setMailProperties0(Properties props) {
/*      */     Session settings;
/*  910 */     checkAccess();
/*  911 */     props = (Properties)props.clone();
/*      */     
/*  913 */     synchronized (this) {
/*  914 */       if (this.isWriting) {
/*  915 */         throw new IllegalStateException();
/*      */       }
/*  917 */       this.mailProps = props;
/*  918 */       settings = fixUpSession();
/*      */     } 
/*  920 */     verifySettings(settings);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Properties getMailProperties() {
/*      */     Properties props;
/*  930 */     checkAccess();
/*      */     
/*  932 */     synchronized (this) {
/*  933 */       props = this.mailProps;
/*      */     } 
/*  935 */     return (Properties)props.clone();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Filter[] getAttachmentFilters() {
/*  945 */     return (Filter[])readOnlyAttachmentFilters().clone();
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
/*      */   public final void setAttachmentFilters(Filter[] filters) {
/*  961 */     checkAccess();
/*  962 */     filters = (Filter[])copyOf((Object[])filters, filters.length, (array$Ljava$util$logging$Filter == null) ? (array$Ljava$util$logging$Filter = class$("[Ljava.util.logging.Filter;")) : array$Ljava$util$logging$Filter);
/*  963 */     synchronized (this) {
/*  964 */       if (this.attachmentFormatters.length != filters.length) {
/*  965 */         throw attachmentMismatch(this.attachmentFormatters.length, filters.length);
/*      */       }
/*      */       
/*  968 */       if (this.isWriting) {
/*  969 */         throw new IllegalStateException();
/*      */       }
/*  971 */       this.attachmentFilters = filters;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Formatter[] getAttachmentFormatters() {
/*      */     Formatter[] formatters;
/*  982 */     synchronized (this) {
/*  983 */       formatters = this.attachmentFormatters;
/*      */     } 
/*  985 */     return (Formatter[])formatters.clone();
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
/*      */   public final void setAttachmentFormatters(Formatter[] formatters) {
/* 1001 */     checkAccess();
/* 1002 */     if (formatters.length == 0) {
/* 1003 */       formatters = emptyFormatterArray();
/*      */     } else {
/* 1005 */       formatters = (Formatter[])copyOf((Object[])formatters, formatters.length, (array$Ljava$util$logging$Formatter == null) ? (array$Ljava$util$logging$Formatter = class$("[Ljava.util.logging.Formatter;")) : array$Ljava$util$logging$Formatter);
/*      */       
/* 1007 */       for (int i = 0; i < formatters.length; i++) {
/* 1008 */         if (formatters[i] == null) {
/* 1009 */           throw new NullPointerException(atIndexMsg(i));
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1014 */     synchronized (this) {
/* 1015 */       if (this.isWriting) {
/* 1016 */         throw new IllegalStateException();
/*      */       }
/*      */       
/* 1019 */       this.attachmentFormatters = formatters;
/* 1020 */       fixUpAttachmentFilters();
/* 1021 */       fixUpAttachmentNames();
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
/*      */   public final Formatter[] getAttachmentNames() {
/*      */     Formatter[] formatters;
/* 1034 */     synchronized (this) {
/* 1035 */       formatters = this.attachmentNames;
/*      */     } 
/* 1037 */     return (Formatter[])formatters.clone();
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
/*      */   public final void setAttachmentNames(String[] names) {
/*      */     Formatter[] formatters;
/* 1055 */     checkAccess();
/*      */ 
/*      */     
/* 1058 */     if (names.length == 0) {
/* 1059 */       formatters = emptyFormatterArray();
/*      */     } else {
/* 1061 */       formatters = new Formatter[names.length];
/*      */     } 
/*      */     
/* 1064 */     for (int i = 0; i < names.length; i++) {
/* 1065 */       String name = names[i];
/* 1066 */       if (name != null) {
/* 1067 */         if (name.length() > 0) {
/* 1068 */           formatters[i] = new TailNameFormatter(name);
/*      */         } else {
/* 1070 */           throw new IllegalArgumentException(atIndexMsg(i));
/*      */         } 
/*      */       } else {
/* 1073 */         throw new NullPointerException(atIndexMsg(i));
/*      */       } 
/*      */     } 
/*      */     
/* 1077 */     synchronized (this) {
/* 1078 */       if (this.attachmentFormatters.length != names.length) {
/* 1079 */         throw attachmentMismatch(this.attachmentFormatters.length, names.length);
/*      */       }
/*      */       
/* 1082 */       if (this.isWriting) {
/* 1083 */         throw new IllegalStateException();
/*      */       }
/* 1085 */       this.attachmentNames = formatters;
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
/*      */   public final void setAttachmentNames(Formatter[] formatters) {
/* 1110 */     checkAccess();
/*      */     
/* 1112 */     formatters = (Formatter[])copyOf((Object[])formatters, formatters.length, (array$Ljava$util$logging$Formatter == null) ? (array$Ljava$util$logging$Formatter = class$("[Ljava.util.logging.Formatter;")) : array$Ljava$util$logging$Formatter);
/* 1113 */     for (int i = 0; i < formatters.length; i++) {
/* 1114 */       if (formatters[i] == null) {
/* 1115 */         throw new NullPointerException(atIndexMsg(i));
/*      */       }
/*      */     } 
/*      */     
/* 1119 */     synchronized (this) {
/* 1120 */       if (this.attachmentFormatters.length != formatters.length) {
/* 1121 */         throw attachmentMismatch(this.attachmentFormatters.length, formatters.length);
/*      */       }
/*      */       
/* 1124 */       if (this.isWriting) {
/* 1125 */         throw new IllegalStateException();
/*      */       }
/*      */       
/* 1128 */       this.attachmentNames = formatters;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized Formatter getSubject() {
/* 1139 */     return this.subjectFormatter;
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
/*      */   public final void setSubject(String subject) {
/* 1153 */     if (subject != null) {
/* 1154 */       setSubject(new TailNameFormatter(subject));
/*      */     } else {
/* 1156 */       checkAccess();
/* 1157 */       throw new NullPointerException();
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
/*      */   public final void setSubject(Formatter format) {
/* 1180 */     checkAccess();
/* 1181 */     if (format == null) {
/* 1182 */       throw new NullPointerException();
/*      */     }
/*      */     
/* 1185 */     synchronized (this) {
/* 1186 */       if (this.isWriting) {
/* 1187 */         throw new IllegalStateException();
/*      */       }
/* 1189 */       this.subjectFormatter = format;
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
/*      */ 
/*      */   
/*      */   protected void reportError(String msg, Exception ex, int code) {
/* 1204 */     if (msg != null) {
/* 1205 */       super.reportError(Level.SEVERE.getName() + ": " + msg, ex, code);
/*      */     } else {
/* 1207 */       super.reportError(null, ex, code);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void checkAccess() {
/* 1215 */     if (this.sealed) {
/* 1216 */       LogManagerProperties.getLogManager().checkAccess();
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
/*      */   
/*      */   final String contentTypeOf(String head) {
/* 1230 */     if (head != null && head.length() > 0) {
/* 1231 */       int MAX_CHARS = 25;
/* 1232 */       if (head.length() > 25) {
/* 1233 */         head = head.substring(0, 25);
/*      */       }
/*      */       try {
/* 1236 */         String encoding = getEncodingName();
/* 1237 */         ByteArrayInputStream in = new ByteArrayInputStream(head.getBytes(encoding));
/*      */ 
/*      */         
/* 1240 */         assert in.markSupported() : in.getClass().getName();
/* 1241 */         return URLConnection.guessContentTypeFromStream(in);
/* 1242 */       } catch (IOException IOE) {
/* 1243 */         reportError(IOE.getMessage(), IOE, 5);
/*      */       } 
/*      */     } 
/* 1246 */     return null;
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
/*      */   final boolean isMissingContent(Message msg, Throwable t) {
/* 1259 */     for (Throwable cause = t.getCause(); cause != null; ) {
/* 1260 */       t = cause;
/* 1261 */       cause = cause.getCause();
/*      */     } 
/*      */     
/*      */     try {
/* 1265 */       msg.writeTo(new ByteArrayOutputStream(1024));
/* 1266 */     } catch (RuntimeException RE) {
/* 1267 */       throw RE;
/* 1268 */     } catch (Exception noContent) {
/* 1269 */       String txt = noContent.getMessage();
/* 1270 */       if (!isEmpty(txt) && noContent.getClass() == t.getClass()) {
/* 1271 */         return txt.equals(t.getMessage());
/*      */       }
/*      */     } 
/* 1274 */     return false;
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
/*      */   private void reportError(Message msg, Exception ex, int code) {
/*      */     try {
/* 1287 */       super.reportError(toRawString(msg), ex, code);
/* 1288 */     } catch (MessagingException rawMe) {
/* 1289 */       reportError(toMsgString((Throwable)rawMe), ex, code);
/* 1290 */     } catch (IOException rawIo) {
/* 1291 */       reportError(toMsgString(rawIo), ex, code);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getContentType(String name) {
/* 1302 */     assert Thread.holdsLock(this);
/* 1303 */     String type = this.contentTypes.getContentType(name);
/* 1304 */     if ("application/octet-stream".equalsIgnoreCase(type)) {
/* 1305 */       return null;
/*      */     }
/* 1307 */     return type;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getEncodingName() {
/* 1316 */     String encoding = getEncoding();
/* 1317 */     if (encoding == null) {
/* 1318 */       encoding = MimeUtility.getDefaultJavaCharset();
/*      */     }
/* 1320 */     return encoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setContent(MimeBodyPart part, CharSequence buf, String type) throws MessagingException {
/* 1331 */     String encoding = getEncodingName();
/* 1332 */     if (type != null && !"text/plain".equalsIgnoreCase(type)) {
/* 1333 */       type = contentWithEncoding(type, encoding);
/*      */       try {
/* 1335 */         ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(buf.toString(), type);
/* 1336 */         part.setDataHandler(new DataHandler((DataSource)byteArrayDataSource));
/* 1337 */       } catch (IOException IOE) {
/* 1338 */         reportError(IOE.getMessage(), IOE, 5);
/* 1339 */         part.setText(buf.toString(), encoding);
/*      */       } 
/*      */     } else {
/* 1342 */       part.setText(buf.toString(), MimeUtility.mimeCharset(encoding));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String contentWithEncoding(String type, String encoding) {
/* 1353 */     assert encoding != null;
/*      */     try {
/* 1355 */       ContentType ct = new ContentType(type);
/* 1356 */       ct.setParameter("charset", MimeUtility.mimeCharset(encoding));
/* 1357 */       encoding = ct.toString();
/* 1358 */       if (!isEmpty(encoding)) {
/* 1359 */         type = encoding;
/*      */       }
/* 1361 */     } catch (MessagingException ME) {
/* 1362 */       reportError(type, (Exception)ME, 5);
/*      */     } 
/* 1364 */     return type;
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
/*      */   private synchronized void setCapacity0(int newCapacity) {
/* 1376 */     if (newCapacity <= 0) {
/* 1377 */       throw new IllegalArgumentException("Capacity must be greater than zero.");
/*      */     }
/*      */     
/* 1380 */     if (this.isWriting) {
/* 1381 */       throw new IllegalStateException();
/*      */     }
/*      */     
/* 1384 */     if (this.capacity < 0) {
/* 1385 */       this.capacity = -newCapacity;
/*      */     } else {
/* 1387 */       this.capacity = newCapacity;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized Filter[] readOnlyAttachmentFilters() {
/* 1398 */     return this.attachmentFilters;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Formatter[] emptyFormatterArray() {
/* 1406 */     return EMPTY_FORMATTERS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Filter[] emptyFilterArray() {
/* 1414 */     return EMPTY_FILTERS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean fixUpAttachmentNames() {
/* 1422 */     assert Thread.holdsLock(this);
/* 1423 */     boolean fixed = false;
/* 1424 */     int expect = this.attachmentFormatters.length;
/* 1425 */     int current = this.attachmentNames.length;
/* 1426 */     if (current != expect) {
/* 1427 */       this.attachmentNames = (Formatter[])copyOf((Object[])this.attachmentNames, expect);
/* 1428 */       fixed = (current != 0);
/*      */     } 
/*      */ 
/*      */     
/* 1432 */     if (expect == 0) {
/* 1433 */       this.attachmentNames = emptyFormatterArray();
/* 1434 */       assert this.attachmentNames.length == 0;
/*      */     } else {
/* 1436 */       for (int i = 0; i < expect; i++) {
/* 1437 */         if (this.attachmentNames[i] == null) {
/* 1438 */           this.attachmentNames[i] = new TailNameFormatter(toString(this.attachmentFormatters[i]));
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1443 */     return fixed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean fixUpAttachmentFilters() {
/* 1451 */     assert Thread.holdsLock(this);
/*      */     
/* 1453 */     boolean fixed = false;
/* 1454 */     int expect = this.attachmentFormatters.length;
/* 1455 */     int current = this.attachmentFilters.length;
/* 1456 */     if (current != expect) {
/* 1457 */       this.attachmentFilters = (Filter[])copyOf((Object[])this.attachmentFilters, expect);
/* 1458 */       fixed = (current != 0);
/*      */     } 
/*      */ 
/*      */     
/* 1462 */     if (expect == 0) {
/* 1463 */       this.attachmentFilters = emptyFilterArray();
/* 1464 */       assert this.attachmentFilters.length == 0;
/*      */     } 
/* 1466 */     return fixed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Object[] copyOf(Object[] a, int size) {
/* 1476 */     Object[] copy = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
/*      */     
/* 1478 */     System.arraycopy(a, 0, copy, 0, Math.min(a.length, size));
/* 1479 */     return copy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Object[] copyOf(Object[] a, int len, Class type) {
/* 1490 */     if (type == a.getClass()) {
/* 1491 */       return (Object[])a.clone();
/*      */     }
/* 1493 */     Object[] copy = (Object[])Array.newInstance(type.getComponentType(), len);
/*      */     
/* 1495 */     System.arraycopy(a, 0, copy, 0, Math.min(len, a.length));
/* 1496 */     return copy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void reset() {
/* 1504 */     assert Thread.holdsLock(this);
/* 1505 */     if (this.size < this.data.length) {
/* 1506 */       Arrays.fill((Object[])this.data, 0, this.size, (Object)null);
/*      */     } else {
/* 1508 */       Arrays.fill((Object[])this.data, (Object)null);
/*      */     } 
/* 1510 */     this.size = 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void grow() {
/* 1517 */     assert Thread.holdsLock(this);
/* 1518 */     int len = this.data.length;
/* 1519 */     int newCapacity = len + (len >> 1) + 1;
/* 1520 */     if (newCapacity > this.capacity || newCapacity < len) {
/* 1521 */       newCapacity = this.capacity;
/*      */     }
/* 1523 */     assert len != this.capacity : len;
/* 1524 */     this.data = (LogRecord[])copyOf((Object[])this.data, newCapacity);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void init(boolean inherit) {
/* 1534 */     LogManager manager = LogManagerProperties.getLogManager();
/* 1535 */     String p = getClass().getName();
/* 1536 */     this.mailProps = new Properties();
/* 1537 */     this.contentTypes = FileTypeMap.getDefaultFileTypeMap();
/*      */ 
/*      */     
/* 1540 */     initErrorManager(manager, p);
/*      */     
/* 1542 */     initLevel(manager, p);
/* 1543 */     initFilter(manager, p);
/* 1544 */     initCapacity(manager, p);
/* 1545 */     initAuthenticator(manager, p);
/*      */     
/* 1547 */     initEncoding(manager, p);
/* 1548 */     initFormatter(manager, p);
/* 1549 */     initComparator(manager, p);
/* 1550 */     initPushLevel(manager, p);
/* 1551 */     initPushFilter(manager, p);
/*      */     
/* 1553 */     initSubject(manager, p);
/*      */     
/* 1555 */     initAttachmentFormaters(manager, p);
/* 1556 */     initAttachmentFilters(manager, p);
/* 1557 */     initAttachmentNames(manager, p);
/*      */     
/* 1559 */     if (inherit && manager.getProperty(p.concat(".verify")) != null) {
/* 1560 */       verifySettings(initSession());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isEmpty(String s) {
/* 1570 */     return (s == null || s.length() == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean hasValue(String name) {
/* 1579 */     return (!isEmpty(name) && !"null".equalsIgnoreCase(name));
/*      */   }
/*      */   
/*      */   private void initAttachmentFilters(LogManager manager, String p) {
/* 1583 */     assert Thread.holdsLock(this);
/* 1584 */     assert this.attachmentFormatters != null;
/* 1585 */     String list = manager.getProperty(p.concat(".attachment.filters"));
/* 1586 */     if (list != null && list.length() > 0) {
/* 1587 */       String[] names = list.split(",");
/* 1588 */       Filter[] a = new Filter[names.length];
/* 1589 */       for (int i = 0; i < a.length; i++) {
/* 1590 */         names[i] = names[i].trim();
/* 1591 */         if (!"null".equalsIgnoreCase(names[i])) {
/*      */           try {
/* 1593 */             a[i] = LogManagerProperties.newFilter(names[i]);
/* 1594 */           } catch (SecurityException SE) {
/* 1595 */             throw SE;
/* 1596 */           } catch (Exception E) {
/* 1597 */             reportError(E.getMessage(), E, 4);
/*      */           } 
/*      */         }
/*      */       } 
/*      */       
/* 1602 */       this.attachmentFilters = a;
/* 1603 */       if (fixUpAttachmentFilters()) {
/* 1604 */         reportError("Attachment filters.", attachmentMismatch("Length mismatch."), 4);
/*      */       }
/*      */     } else {
/*      */       
/* 1608 */       this.attachmentFilters = emptyFilterArray();
/* 1609 */       fixUpAttachmentFilters();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initAttachmentFormaters(LogManager manager, String p) {
/* 1614 */     assert Thread.holdsLock(this);
/* 1615 */     String list = manager.getProperty(p.concat(".attachment.formatters"));
/* 1616 */     if (list != null && list.length() > 0) {
/*      */       Formatter[] a;
/* 1618 */       String[] names = list.split(",");
/* 1619 */       if (names.length == 0) {
/* 1620 */         a = emptyFormatterArray();
/*      */       } else {
/* 1622 */         a = new Formatter[names.length];
/*      */       } 
/*      */       
/* 1625 */       for (int i = 0; i < a.length; i++) {
/* 1626 */         names[i] = names[i].trim();
/* 1627 */         if (!"null".equalsIgnoreCase(names[i])) {
/*      */           try {
/* 1629 */             a[i] = LogManagerProperties.newFormatter(names[i]);
/* 1630 */             if (a[i] instanceof TailNameFormatter) {
/* 1631 */               a[i] = new SimpleFormatter();
/* 1632 */               Exception CNFE = new ClassNotFoundException(a[i].toString());
/* 1633 */               reportError("Attachment formatter.", CNFE, 4);
/*      */             } 
/* 1635 */           } catch (SecurityException SE) {
/* 1636 */             throw SE;
/* 1637 */           } catch (Exception E) {
/* 1638 */             a[i] = new SimpleFormatter();
/* 1639 */             reportError(E.getMessage(), E, 4);
/*      */           } 
/*      */         } else {
/* 1642 */           a[i] = new SimpleFormatter();
/* 1643 */           Exception NPE = new NullPointerException(atIndexMsg(i));
/* 1644 */           reportError("Attachment formatter.", NPE, 4);
/*      */         } 
/*      */       } 
/*      */       
/* 1648 */       this.attachmentFormatters = a;
/*      */     } else {
/* 1650 */       this.attachmentFormatters = emptyFormatterArray();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initAttachmentNames(LogManager manager, String p) {
/* 1655 */     assert Thread.holdsLock(this);
/* 1656 */     assert this.attachmentFormatters != null;
/*      */     
/* 1658 */     String list = manager.getProperty(p.concat(".attachment.names"));
/* 1659 */     if (list != null && list.length() > 0) {
/* 1660 */       String[] names = list.split(",");
/* 1661 */       Formatter[] a = new Formatter[names.length];
/* 1662 */       for (int i = 0; i < a.length; i++) {
/* 1663 */         names[i] = names[i].trim();
/* 1664 */         if (!"null".equalsIgnoreCase(names[i])) {
/*      */           try {
/*      */             try {
/* 1667 */               a[i] = LogManagerProperties.newFormatter(names[i]);
/* 1668 */             } catch (ClassNotFoundException literal) {
/* 1669 */               a[i] = new TailNameFormatter(names[i]);
/* 1670 */             } catch (ClassCastException literal) {
/* 1671 */               a[i] = new TailNameFormatter(names[i]);
/*      */             } 
/* 1673 */           } catch (SecurityException SE) {
/* 1674 */             throw SE;
/* 1675 */           } catch (Exception E) {
/* 1676 */             reportError(E.getMessage(), E, 4);
/*      */           } 
/*      */         } else {
/* 1679 */           Exception NPE = new NullPointerException(atIndexMsg(i));
/* 1680 */           reportError("Attachment names.", NPE, 4);
/*      */         } 
/*      */       } 
/*      */       
/* 1684 */       this.attachmentNames = a;
/* 1685 */       if (fixUpAttachmentNames()) {
/* 1686 */         reportError("Attachment names.", attachmentMismatch("Length mismatch."), 4);
/*      */       }
/*      */     } else {
/*      */       
/* 1690 */       this.attachmentNames = emptyFormatterArray();
/* 1691 */       fixUpAttachmentNames();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initAuthenticator(LogManager manager, String p) {
/* 1696 */     assert Thread.holdsLock(this);
/* 1697 */     String name = manager.getProperty(p.concat(".authenticator"));
/* 1698 */     if (hasValue(name)) {
/*      */       try {
/* 1700 */         this.auth = LogManagerProperties.newAuthenticator(name);
/* 1701 */       } catch (SecurityException SE) {
/* 1702 */         throw SE;
/* 1703 */       } catch (ClassNotFoundException literalAuth) {
/* 1704 */         this.auth = new DefaultAuthenticator(name);
/* 1705 */       } catch (ClassCastException literalAuth) {
/* 1706 */         this.auth = new DefaultAuthenticator(name);
/* 1707 */       } catch (Exception E) {
/* 1708 */         reportError(E.getMessage(), E, 4);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private void initLevel(LogManager manager, String p) {
/* 1714 */     assert Thread.holdsLock(this);
/*      */     try {
/* 1716 */       String val = manager.getProperty(p.concat(".level"));
/* 1717 */       if (val != null) {
/* 1718 */         super.setLevel(Level.parse(val));
/*      */       } else {
/* 1720 */         super.setLevel(Level.WARNING);
/*      */       } 
/* 1722 */     } catch (SecurityException SE) {
/* 1723 */       throw SE;
/* 1724 */     } catch (RuntimeException RE) {
/* 1725 */       reportError(RE.getMessage(), RE, 4);
/*      */       try {
/* 1727 */         super.setLevel(Level.WARNING);
/* 1728 */       } catch (RuntimeException fail) {
/* 1729 */         reportError(fail.getMessage(), fail, 4);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initFilter(LogManager manager, String p) {
/* 1735 */     assert Thread.holdsLock(this);
/*      */     try {
/* 1737 */       String name = manager.getProperty(p.concat(".filter"));
/* 1738 */       if (hasValue(name)) {
/* 1739 */         setFilter(LogManagerProperties.newFilter(name));
/*      */       }
/* 1741 */     } catch (SecurityException SE) {
/* 1742 */       throw SE;
/* 1743 */     } catch (Exception E) {
/* 1744 */       reportError(E.getMessage(), E, 4);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initCapacity(LogManager manager, String p) {
/* 1749 */     assert Thread.holdsLock(this);
/* 1750 */     int DEFAULT_CAPACITY = 1000;
/*      */     try {
/* 1752 */       String value = manager.getProperty(p.concat(".capacity"));
/* 1753 */       if (value != null) {
/* 1754 */         setCapacity0(Integer.parseInt(value));
/*      */       } else {
/* 1756 */         setCapacity0(1000);
/*      */       } 
/* 1758 */     } catch (RuntimeException RE) {
/* 1759 */       reportError(RE.getMessage(), RE, 4);
/*      */     } 
/*      */     
/* 1762 */     if (this.capacity <= 0) {
/* 1763 */       this.capacity = 1000;
/*      */     }
/*      */     
/* 1766 */     this.data = new LogRecord[1];
/*      */   }
/*      */   
/*      */   private void initEncoding(LogManager manager, String p) {
/* 1770 */     assert Thread.holdsLock(this);
/*      */     try {
/* 1772 */       setEncoding(manager.getProperty(p.concat(".encoding")));
/* 1773 */     } catch (SecurityException SE) {
/* 1774 */       throw SE;
/* 1775 */     } catch (UnsupportedEncodingException UEE) {
/* 1776 */       reportError(UEE.getMessage(), UEE, 4);
/* 1777 */     } catch (RuntimeException RE) {
/* 1778 */       reportError(RE.getMessage(), RE, 4);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initErrorManager(LogManager manager, String p) {
/* 1783 */     assert Thread.holdsLock(this);
/* 1784 */     String name = manager.getProperty(p.concat(".errorManager"));
/* 1785 */     if (name != null) {
/*      */       try {
/* 1787 */         ErrorManager em = LogManagerProperties.newErrorManager(name);
/* 1788 */         setErrorManager(em);
/* 1789 */       } catch (SecurityException SE) {
/* 1790 */         throw SE;
/* 1791 */       } catch (Exception E) {
/* 1792 */         reportError(E.getMessage(), E, 4);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private void initFormatter(LogManager manager, String p) {
/* 1798 */     assert Thread.holdsLock(this);
/* 1799 */     String name = manager.getProperty(p.concat(".formatter"));
/* 1800 */     if (hasValue(name)) {
/*      */       try {
/* 1802 */         Formatter formatter = LogManagerProperties.newFormatter(name);
/* 1803 */         assert formatter != null;
/* 1804 */         if (!(formatter instanceof TailNameFormatter)) {
/* 1805 */           setFormatter(formatter);
/*      */         } else {
/* 1807 */           setFormatter(new SimpleFormatter());
/*      */         } 
/* 1809 */       } catch (SecurityException SE) {
/* 1810 */         throw SE;
/* 1811 */       } catch (Exception E) {
/* 1812 */         reportError(E.getMessage(), E, 4);
/*      */         try {
/* 1814 */           setFormatter(new SimpleFormatter());
/* 1815 */         } catch (RuntimeException fail) {
/* 1816 */           reportError(fail.getMessage(), fail, 4);
/*      */         } 
/*      */       } 
/*      */     } else {
/* 1820 */       setFormatter(new SimpleFormatter());
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initComparator(LogManager manager, String p) {
/* 1825 */     assert Thread.holdsLock(this);
/* 1826 */     String name = manager.getProperty(p.concat(".comparator"));
/* 1827 */     if (hasValue(name)) {
/*      */       try {
/* 1829 */         this.comparator = LogManagerProperties.newComparator(name);
/* 1830 */       } catch (SecurityException SE) {
/* 1831 */         throw SE;
/* 1832 */       } catch (Exception E) {
/* 1833 */         reportError(E.getMessage(), E, 4);
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
/*      */   private void initPushLevel(LogManager manager, String p) {
/* 1856 */     assert Thread.holdsLock(this);
/*      */     try {
/* 1858 */       String val = manager.getProperty(p.concat(".pushLevel"));
/* 1859 */       if (val != null) {
/* 1860 */         this.pushLevel = Level.parse(val);
/*      */       }
/* 1862 */     } catch (RuntimeException RE) {
/* 1863 */       reportError(RE.getMessage(), RE, 4);
/*      */     } 
/*      */     
/* 1866 */     if (this.pushLevel == null) {
/* 1867 */       this.pushLevel = Level.OFF;
/*      */     }
/*      */   }
/*      */   
/*      */   private void initPushFilter(LogManager manager, String p) {
/* 1872 */     assert Thread.holdsLock(this);
/* 1873 */     String name = manager.getProperty(p.concat(".pushFilter"));
/* 1874 */     if (hasValue(name)) {
/*      */       try {
/* 1876 */         this.pushFilter = LogManagerProperties.newFilter(name);
/* 1877 */       } catch (SecurityException SE) {
/* 1878 */         throw SE;
/* 1879 */       } catch (Exception E) {
/* 1880 */         reportError(E.getMessage(), E, 4);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private void initSubject(LogManager manager, String p) {
/* 1886 */     assert Thread.holdsLock(this);
/* 1887 */     String name = manager.getProperty(p.concat(".subject"));
/* 1888 */     if (hasValue(name)) {
/*      */       try {
/* 1890 */         this.subjectFormatter = LogManagerProperties.newFormatter(name);
/* 1891 */       } catch (SecurityException SE) {
/* 1892 */         throw SE;
/* 1893 */       } catch (ClassNotFoundException literalSubject) {
/* 1894 */         this.subjectFormatter = new TailNameFormatter(name);
/* 1895 */       } catch (ClassCastException literalSubject) {
/* 1896 */         this.subjectFormatter = new TailNameFormatter(name);
/* 1897 */       } catch (Exception E) {
/* 1898 */         this.subjectFormatter = new TailNameFormatter(name);
/* 1899 */         reportError(E.getMessage(), E, 4);
/*      */       } 
/*      */     }
/*      */     
/* 1903 */     if (this.subjectFormatter == null) {
/* 1904 */       this.subjectFormatter = new TailNameFormatter("");
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
/*      */   private boolean isAttachmentLoggable(LogRecord record) {
/* 1916 */     Filter[] filters = readOnlyAttachmentFilters();
/* 1917 */     for (int i = 0; i < filters.length; i++) {
/* 1918 */       Filter f = filters[i];
/* 1919 */       if (f == null || f.isLoggable(record)) {
/* 1920 */         return true;
/*      */       }
/*      */     } 
/* 1923 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isPushable(LogRecord record) {
/* 1933 */     assert Thread.holdsLock(this);
/* 1934 */     int value = getPushLevel().intValue();
/* 1935 */     if (value == offValue || record.getLevel().intValue() < value) {
/* 1936 */       return false;
/*      */     }
/*      */     
/* 1939 */     Filter filter = getPushFilter();
/* 1940 */     return (filter == null || filter.isLoggable(record));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void push(boolean priority, int code) {
/* 1949 */     if (tryMutex()) {
/*      */       try {
/* 1951 */         MessageContext ctx = writeLogRecords(code);
/* 1952 */         if (ctx != null) {
/* 1953 */           send(ctx, priority, code);
/*      */         }
/*      */       } finally {
/* 1956 */         releaseMutex();
/*      */       } 
/*      */     } else {
/* 1959 */       reportUnPublishedError((LogRecord)null);
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
/*      */ 
/*      */   
/*      */   private void send(MessageContext ctx, boolean priority, int code) {
/* 1974 */     Message msg = ctx.getMessage();
/*      */     try {
/* 1976 */       envelopeFor(ctx, priority);
/* 1977 */       Transport.send(msg);
/* 1978 */     } catch (Exception E) {
/* 1979 */       reportError(msg, E, code);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sort() {
/* 1988 */     assert Thread.holdsLock(this);
/* 1989 */     if (this.comparator != null) {
/*      */       try {
/* 1991 */         if (this.size != 1) {
/* 1992 */           Arrays.sort(this.data, 0, this.size, this.comparator);
/*      */         } else {
/* 1994 */           this.comparator.compare(this.data[0], this.data[0]);
/*      */         } 
/* 1996 */       } catch (RuntimeException RE) {
/* 1997 */         reportError(RE.getMessage(), RE, 5);
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
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized MessageContext writeLogRecords(int code) {
/* 2013 */     if (this.size == 0 || this.isWriting) {
/* 2014 */       return null;
/*      */     }
/*      */     
/* 2017 */     this.isWriting = true;
/*      */     try {
/* 2019 */       sort();
/* 2020 */       if (this.session == null) {
/* 2021 */         initSession();
/*      */       }
/* 2023 */       MimeMessage msg = new MimeMessage(this.session);
/* 2024 */       msg.setDescription(descriptionFrom(this.comparator, this.pushLevel, this.pushFilter));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2031 */       MimeBodyPart[] parts = new MimeBodyPart[this.attachmentFormatters.length];
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2036 */       StringBuffer[] buffers = new StringBuffer[parts.length];
/*      */       
/* 2038 */       String contentType = null;
/* 2039 */       StringBuffer buf = null;
/*      */       
/* 2041 */       appendSubject((Message)msg, head(this.subjectFormatter));
/*      */       
/* 2043 */       MimeBodyPart body = createBodyPart();
/* 2044 */       Formatter bodyFormat = getFormatter();
/* 2045 */       Filter bodyFilter = getFilter();
/*      */       
/* 2047 */       Locale lastLocale = null;
/* 2048 */       for (int ix = 0; ix < this.size; ix++) {
/* 2049 */         boolean formatted = false;
/* 2050 */         LogRecord r = this.data[ix];
/* 2051 */         this.data[ix] = null;
/*      */         
/* 2053 */         Locale locale = localeFor(r);
/* 2054 */         appendSubject((Message)msg, format(this.subjectFormatter, r));
/*      */         
/* 2056 */         if (bodyFilter == null || bodyFilter.isLoggable(r)) {
/* 2057 */           if (buf == null) {
/* 2058 */             buf = new StringBuffer();
/* 2059 */             String head = head(bodyFormat);
/* 2060 */             buf.append(head);
/* 2061 */             contentType = contentTypeOf(head);
/*      */           } 
/* 2063 */           formatted = true;
/* 2064 */           buf.append(format(bodyFormat, r));
/* 2065 */           if (locale != null && !locale.equals(lastLocale)) {
/* 2066 */             appendContentLang((MimePart)body, locale);
/*      */           }
/*      */         } 
/*      */         
/* 2070 */         for (int k = 0; k < parts.length; k++) {
/* 2071 */           Filter af = this.attachmentFilters[k];
/* 2072 */           if (af == null || af.isLoggable(r)) {
/* 2073 */             if (parts[k] == null) {
/* 2074 */               parts[k] = createBodyPart(k);
/* 2075 */               buffers[k] = new StringBuffer();
/* 2076 */               buffers[k].append(head(this.attachmentFormatters[k]));
/* 2077 */               appendFileName((Part)parts[k], head(this.attachmentNames[k]));
/*      */             } 
/* 2079 */             formatted = true;
/* 2080 */             appendFileName((Part)parts[k], format(this.attachmentNames[k], r));
/* 2081 */             buffers[k].append(format(this.attachmentFormatters[k], r));
/* 2082 */             if (locale != null && !locale.equals(lastLocale)) {
/* 2083 */               appendContentLang((MimePart)parts[k], locale);
/*      */             }
/*      */           } 
/*      */         } 
/*      */         
/* 2088 */         if (formatted) {
/* 2089 */           if (locale != null && !locale.equals(lastLocale)) {
/* 2090 */             appendContentLang((MimePart)msg, locale);
/*      */           }
/*      */         } else {
/* 2093 */           reportFilterError(r);
/*      */         } 
/* 2095 */         lastLocale = locale;
/*      */       } 
/* 2097 */       this.size = 0;
/*      */       
/* 2099 */       for (int i = parts.length - 1; i >= 0; i--) {
/* 2100 */         if (parts[i] != null) {
/* 2101 */           appendFileName((Part)parts[i], tail(this.attachmentNames[i], "err"));
/* 2102 */           buffers[i].append(tail(this.attachmentFormatters[i], ""));
/*      */           
/* 2104 */           if (buffers[i].length() > 0) {
/* 2105 */             String name = parts[i].getFileName();
/* 2106 */             if (isEmpty(name)) {
/* 2107 */               name = toString(this.attachmentFormatters[i]);
/* 2108 */               parts[i].setFileName(name);
/*      */             } 
/* 2110 */             setContent(parts[i], buffers[i], getContentType(name));
/*      */           } else {
/* 2112 */             setIncompleteCopy((Message)msg);
/* 2113 */             parts[i] = null;
/*      */           } 
/* 2115 */           buffers[i] = null;
/*      */         } 
/*      */       } 
/* 2118 */       buffers = null;
/*      */       
/* 2120 */       if (buf != null) {
/* 2121 */         buf.append(tail(bodyFormat, ""));
/*      */       }
/*      */       else {
/*      */         
/* 2125 */         buf = new StringBuffer(0);
/*      */       } 
/*      */       
/* 2128 */       appendSubject((Message)msg, tail(this.subjectFormatter, ""));
/*      */       
/* 2130 */       MimeMultipart multipart = new MimeMultipart();
/* 2131 */       String altType = getContentType(bodyFormat.getClass().getName());
/* 2132 */       setContent(body, buf, (altType == null) ? contentType : altType);
/* 2133 */       buf = null;
/* 2134 */       multipart.addBodyPart((BodyPart)body);
/*      */       
/* 2136 */       for (int j = 0; j < parts.length; j++) {
/* 2137 */         if (parts[j] != null) {
/* 2138 */           multipart.addBodyPart((BodyPart)parts[j]);
/*      */         }
/*      */       } 
/* 2141 */       parts = null;
/* 2142 */       msg.setContent((Multipart)multipart);
/* 2143 */       return new MessageContext((Part)msg);
/* 2144 */     } catch (RuntimeException re) {
/* 2145 */       reportError(re.getMessage(), re, code);
/* 2146 */     } catch (Exception e) {
/* 2147 */       reportError(e.getMessage(), e, code);
/*      */     } finally {
/* 2149 */       this.isWriting = false;
/* 2150 */       if (this.size > 0) {
/* 2151 */         reset();
/*      */       }
/*      */     } 
/* 2154 */     return null;
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
/*      */   private void verifySettings(Session session) {
/* 2167 */     if (session != null) {
/* 2168 */       Properties props = session.getProperties();
/* 2169 */       Object check = props.put("verify", "");
/* 2170 */       if (check instanceof String) {
/* 2171 */         String value = (String)check;
/*      */         
/* 2173 */         if (hasValue(value)) {
/* 2174 */           verifySettings0(session, value);
/*      */         }
/*      */       }
/* 2177 */       else if (check != null) {
/* 2178 */         verifySettings0(session, check.getClass().toString());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void verifySettings0(Session session, String verify) {
/* 2195 */     assert verify != null : (String)null;
/* 2196 */     if (!"local".equals(verify) && !"remote".equals(verify)) {
/* 2197 */       reportError("Verify must be 'local' or 'remote'.", new IllegalArgumentException(verify), 4);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 2203 */     String msg = "Local address is " + InternetAddress.getLocalAddress(session) + '.';
/*      */ 
/*      */     
/*      */     try {
/* 2207 */       Charset.forName(getEncodingName());
/* 2208 */     } catch (RuntimeException RE) {
/* 2209 */       IOException UEE = new UnsupportedEncodingException(RE.toString());
/* 2210 */       UEE.initCause(RE);
/* 2211 */       reportError(msg, UEE, 5);
/*      */     } 
/*      */ 
/*      */     
/* 2215 */     MimeMessage abort = new MimeMessage(session);
/* 2216 */     synchronized (this) {
/* 2217 */       appendSubject((Message)abort, head(this.subjectFormatter));
/* 2218 */       appendSubject((Message)abort, tail(this.subjectFormatter, ""));
/*      */     } 
/*      */     
/* 2221 */     setIncompleteCopy((Message)abort);
/* 2222 */     envelopeFor(new MessageContext((Part)abort), true);
/*      */     try {
/* 2224 */       abort.saveChanges();
/* 2225 */     } catch (MessagingException ME) {
/* 2226 */       reportError(msg, (Exception)ME, 5);
/*      */     } 
/*      */     try {
/*      */       InternetAddress[] arrayOfInternetAddress;
/*      */       Transport t;
/* 2231 */       Address[] all = abort.getAllRecipients();
/* 2232 */       if (all == null) {
/* 2233 */         arrayOfInternetAddress = new InternetAddress[0];
/*      */       }
/*      */       
/*      */       try {
/* 2237 */         Address[] any = (arrayOfInternetAddress.length != 0) ? (Address[])arrayOfInternetAddress : abort.getFrom();
/* 2238 */         if (any != null && any.length != 0) {
/* 2239 */           t = session.getTransport(any[0]);
/* 2240 */           session.getProperty("mail.transport.protocol");
/*      */         } else {
/* 2242 */           MessagingException me = new MessagingException("No recipient or from address.");
/*      */           
/* 2244 */           reportError(msg, (Exception)me, 4);
/* 2245 */           throw me;
/*      */         } 
/* 2247 */       } catch (MessagingException protocol) {
/*      */         try {
/* 2249 */           t = session.getTransport();
/* 2250 */         } catch (MessagingException fail) {
/* 2251 */           throw attach(protocol, fail);
/*      */         } 
/*      */       } 
/*      */       
/* 2255 */       String host = null;
/* 2256 */       if ("remote".equals(verify)) {
/* 2257 */         MessagingException closed = null;
/* 2258 */         t.connect();
/*      */         
/*      */         try {
/*      */           try {
/* 2262 */             if (t instanceof SMTPTransport) {
/* 2263 */               host = ((SMTPTransport)t).getLocalHost();
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 2268 */             t.sendMessage((Message)abort, (Address[])arrayOfInternetAddress);
/*      */           } finally {
/*      */             try {
/* 2271 */               t.close();
/* 2272 */             } catch (MessagingException ME) {
/* 2273 */               closed = ME;
/*      */             } 
/*      */           } 
/* 2276 */           reportUnexpectedSend(abort, verify, (Exception)null);
/* 2277 */         } catch (SendFailedException sfe) {
/* 2278 */           Address[] recip = sfe.getInvalidAddresses();
/* 2279 */           if (recip != null && recip.length != 0) {
/* 2280 */             fixUpContent(abort, verify, (Throwable)sfe);
/* 2281 */             reportError((Message)abort, (Exception)sfe, 4);
/*      */           } 
/*      */           
/* 2284 */           recip = sfe.getValidSentAddresses();
/* 2285 */           if (recip != null && recip.length != 0) {
/* 2286 */             reportUnexpectedSend(abort, verify, (Exception)sfe);
/*      */           }
/* 2288 */         } catch (MessagingException ME) {
/* 2289 */           if (!isMissingContent((Message)abort, (Throwable)ME)) {
/* 2290 */             fixUpContent(abort, verify, (Throwable)ME);
/* 2291 */             reportError((Message)abort, (Exception)ME, 4);
/*      */           } 
/*      */         } 
/*      */         
/* 2295 */         if (closed != null) {
/* 2296 */           fixUpContent(abort, verify, (Throwable)closed);
/* 2297 */           reportError((Message)abort, (Exception)closed, 3);
/*      */         } 
/*      */       } else {
/* 2300 */         String protocol = t.getURLName().getProtocol();
/* 2301 */         session.getProperty("mail.host");
/* 2302 */         session.getProperty("mail.user");
/* 2303 */         session.getProperty("mail." + protocol + ".host");
/* 2304 */         session.getProperty("mail." + protocol + ".port");
/* 2305 */         session.getProperty("mail." + protocol + ".user");
/* 2306 */         if (t instanceof SMTPTransport) {
/* 2307 */           host = ((SMTPTransport)t).getLocalHost();
/*      */         } else {
/* 2309 */           host = session.getProperty("mail." + protocol + ".localhost");
/*      */           
/* 2311 */           if (isEmpty(host)) {
/* 2312 */             host = session.getProperty("mail." + protocol + ".localaddress");
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*      */       try {
/* 2319 */         if (isEmpty(host)) {
/* 2320 */           if (InetAddress.getLocalHost().getCanonicalHostName().length() == 0)
/*      */           {
/* 2322 */             throw new UnknownHostException();
/*      */           }
/*      */         }
/* 2325 */         else if (InetAddress.getByName(host).getCanonicalHostName().length() == 0) {
/*      */           
/* 2327 */           throw new UnknownHostException(host);
/*      */         }
/*      */       
/* 2330 */       } catch (IOException IOE) {
/* 2331 */         MessagingException ME = new MessagingException(msg, IOE);
/* 2332 */         fixUpContent(abort, verify, (Throwable)ME);
/* 2333 */         reportError((Message)abort, (Exception)ME, 4);
/*      */       } 
/*      */       
/*      */       try {
/* 2337 */         MimeMultipart multipart = new MimeMultipart();
/* 2338 */         MimeBodyPart body = new MimeBodyPart();
/* 2339 */         body.setDisposition("inline");
/* 2340 */         body.setDescription(verify);
/* 2341 */         setAcceptLang((Part)body);
/* 2342 */         setContent(body, "", "text/plain");
/* 2343 */         multipart.addBodyPart((BodyPart)body);
/* 2344 */         abort.setContent((Multipart)multipart);
/* 2345 */         abort.saveChanges();
/* 2346 */         abort.writeTo(new ByteArrayOutputStream(1024));
/* 2347 */       } catch (IOException IOE) {
/* 2348 */         MessagingException ME = new MessagingException(msg, IOE);
/* 2349 */         fixUpContent(abort, verify, (Throwable)ME);
/* 2350 */         reportError((Message)abort, (Exception)ME, 5);
/*      */       } 
/*      */ 
/*      */       
/* 2354 */       if (arrayOfInternetAddress.length != 0) {
/* 2355 */         verifyAddresses((Address[])arrayOfInternetAddress);
/*      */       } else {
/* 2357 */         throw new MessagingException("No recipient addresses.");
/*      */       } 
/*      */ 
/*      */       
/* 2361 */       Address[] from = abort.getFrom();
/* 2362 */       Address sender = abort.getSender();
/* 2363 */       if (sender instanceof InternetAddress) {
/* 2364 */         ((InternetAddress)sender).validate();
/*      */       }
/*      */ 
/*      */       
/* 2368 */       if (abort.getHeader("From", ",") != null && from.length != 0) {
/* 2369 */         verifyAddresses(from);
/* 2370 */         for (int i = 0; i < from.length; i++) {
/* 2371 */           if (from[i].equals(sender)) {
/* 2372 */             MessagingException ME = new MessagingException("Sender address '" + sender + "' equals from address.");
/*      */ 
/*      */             
/* 2375 */             throw new MessagingException(msg, ME);
/*      */           }
/*      */         
/*      */         } 
/* 2379 */       } else if (sender == null) {
/* 2380 */         MessagingException ME = new MessagingException("No from or sender address.");
/*      */         
/* 2382 */         throw new MessagingException(msg, ME);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2387 */       verifyAddresses(abort.getReplyTo());
/* 2388 */     } catch (MessagingException ME) {
/* 2389 */       fixUpContent(abort, verify, (Throwable)ME);
/* 2390 */       reportError((Message)abort, (Exception)ME, 4);
/* 2391 */     } catch (RuntimeException RE) {
/* 2392 */       fixUpContent(abort, verify, RE);
/* 2393 */       reportError((Message)abort, RE, 4);
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
/*      */   private static void verifyAddresses(Address[] all) throws AddressException {
/* 2406 */     if (all != null) {
/* 2407 */       for (int i = 0; i < all.length; i++) {
/* 2408 */         Address a = all[i];
/* 2409 */         if (a instanceof InternetAddress) {
/* 2410 */           ((InternetAddress)a).validate();
/*      */         }
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
/*      */   private void reportUnexpectedSend(MimeMessage msg, String verify, Exception cause) {
/* 2424 */     MessagingException write = new MessagingException("An empty message was sent.", cause);
/*      */     
/* 2426 */     fixUpContent(msg, verify, (Throwable)write);
/* 2427 */     reportError((Message)msg, (Exception)write, 4);
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
/*      */   private void fixUpContent(MimeMessage msg, String verify, Throwable t) {
/*      */     try {
/*      */       MimeBodyPart body;
/*      */       String subjectType, msgDesc;
/* 2444 */       synchronized (this) {
/* 2445 */         body = createBodyPart();
/* 2446 */         msgDesc = descriptionFrom(this.comparator, this.pushLevel, this.pushFilter);
/* 2447 */         subjectType = getClassId(this.subjectFormatter);
/*      */       } 
/*      */       
/* 2450 */       body.setDescription("Formatted using " + ((t == null) ? Throwable.class.getName() : t.getClass().getName()) + ", filtered with " + verify + ", and named by " + subjectType + '.');
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2455 */       setContent(body, toMsgString(t), "text/plain");
/* 2456 */       MimeMultipart multipart = new MimeMultipart();
/* 2457 */       multipart.addBodyPart((BodyPart)body);
/* 2458 */       msg.setContent((Multipart)multipart);
/* 2459 */       msg.setDescription(msgDesc);
/* 2460 */       setAcceptLang((Part)msg);
/* 2461 */       msg.saveChanges();
/* 2462 */     } catch (MessagingException ME) {
/* 2463 */       reportError("Unable to create body.", (Exception)ME, 4);
/* 2464 */     } catch (RuntimeException RE) {
/* 2465 */       reportError("Unable to create body.", RE, 4);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Session fixUpSession() {
/*      */     Session settings;
/* 2475 */     assert Thread.holdsLock(this);
/*      */     
/* 2477 */     if (this.mailProps.getProperty("verify") != null) {
/* 2478 */       settings = initSession();
/* 2479 */       assert settings == this.session;
/*      */     } else {
/* 2481 */       this.session = null;
/* 2482 */       settings = null;
/*      */     } 
/* 2484 */     return settings;
/*      */   }
/*      */   
/*      */   private Session initSession() {
/* 2488 */     assert Thread.holdsLock(this);
/* 2489 */     String p = getClass().getName();
/* 2490 */     LogManagerProperties proxy = new LogManagerProperties(this.mailProps, p);
/* 2491 */     this.session = Session.getInstance(proxy, this.auth);
/* 2492 */     return this.session;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void envelopeFor(MessageContext ctx, boolean priority) {
/* 2503 */     Message msg = ctx.getMessage();
/* 2504 */     Properties proxyProps = ctx.getSession().getProperties();
/* 2505 */     setAcceptLang((Part)msg);
/* 2506 */     setFrom(msg, proxyProps);
/* 2507 */     setRecipient(msg, proxyProps, "mail.to", Message.RecipientType.TO);
/* 2508 */     setRecipient(msg, proxyProps, "mail.cc", Message.RecipientType.CC);
/* 2509 */     setRecipient(msg, proxyProps, "mail.bcc", Message.RecipientType.BCC);
/* 2510 */     setReplyTo(msg, proxyProps);
/* 2511 */     setSender(msg, proxyProps);
/* 2512 */     setMailer(msg);
/* 2513 */     setAutoSubmitted(msg);
/* 2514 */     if (priority) {
/* 2515 */       setPriority(msg);
/*      */     }
/*      */     
/*      */     try {
/* 2519 */       msg.setSentDate(new Date());
/* 2520 */     } catch (MessagingException ME) {
/* 2521 */       reportError(ME.getMessage(), (Exception)ME, 5);
/*      */     } 
/*      */   }
/*      */   
/*      */   private MimeBodyPart createBodyPart() throws MessagingException {
/* 2526 */     assert Thread.holdsLock(this);
/* 2527 */     MimeBodyPart part = new MimeBodyPart();
/* 2528 */     part.setDisposition("inline");
/* 2529 */     part.setDescription(descriptionFrom(getFormatter(), getFilter(), this.subjectFormatter));
/*      */     
/* 2531 */     setAcceptLang((Part)part);
/* 2532 */     return part;
/*      */   }
/*      */   
/*      */   private MimeBodyPart createBodyPart(int index) throws MessagingException {
/* 2536 */     assert Thread.holdsLock(this);
/* 2537 */     MimeBodyPart part = new MimeBodyPart();
/* 2538 */     part.setDisposition("attachment");
/* 2539 */     part.setDescription(descriptionFrom(this.attachmentFormatters[index], this.attachmentFilters[index], this.attachmentNames[index]));
/*      */ 
/*      */ 
/*      */     
/* 2543 */     setAcceptLang((Part)part);
/* 2544 */     return part;
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
/*      */   private String descriptionFrom(Comparator c, Level l, Filter f) {
/* 2559 */     return "Sorted using " + ((c == null) ? "no comparator" : c.getClass().getName()) + ", pushed when " + l.getName() + ", and " + ((f == null) ? "no push filter" : f.getClass().getName()) + '.';
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String descriptionFrom(Formatter f, Filter filter, Formatter name) {
/* 2566 */     return "Formatted using " + getClassId(f) + ", filtered with " + ((filter == null) ? "no filter" : filter.getClass().getName()) + ", and named by " + getClassId(name) + '.';
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
/*      */   private String getClassId(Formatter f) {
/* 2581 */     if (f instanceof TailNameFormatter) {
/* 2582 */       return String.class.getName();
/*      */     }
/* 2584 */     return f.getClass().getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String toString(Formatter f) {
/* 2595 */     String name = f.toString();
/* 2596 */     if (!isEmpty(name)) {
/* 2597 */       return name;
/*      */     }
/* 2599 */     return getClassId(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void appendFileName(Part part, String chunk) {
/* 2610 */     if (chunk != null) {
/* 2611 */       if (chunk.length() > 0) {
/* 2612 */         appendFileName0(part, chunk);
/*      */       }
/*      */     } else {
/* 2615 */       reportNullError(5);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void appendFileName0(Part part, String chunk) {
/*      */     try {
/* 2627 */       String old = part.getFileName();
/* 2628 */       part.setFileName((old != null) ? old.concat(chunk) : chunk);
/* 2629 */     } catch (MessagingException ME) {
/* 2630 */       reportError(ME.getMessage(), (Exception)ME, 5);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void appendSubject(Message msg, String chunk) {
/* 2640 */     if (chunk != null) {
/* 2641 */       if (chunk.length() > 0) {
/* 2642 */         appendSubject0(msg, chunk);
/*      */       }
/*      */     } else {
/* 2645 */       reportNullError(5);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void appendSubject0(Message msg, String chunk) {
/*      */     try {
/* 2657 */       String encoding = getEncodingName();
/* 2658 */       String old = msg.getSubject();
/* 2659 */       assert msg instanceof MimeMessage;
/* 2660 */       ((MimeMessage)msg).setSubject((old != null) ? old.concat(chunk) : chunk, MimeUtility.mimeCharset(encoding));
/*      */     }
/* 2662 */     catch (MessagingException ME) {
/* 2663 */       reportError(ME.getMessage(), (Exception)ME, 5);
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
/*      */   private Locale localeFor(LogRecord r) {
/*      */     Locale l;
/* 2677 */     ResourceBundle rb = r.getResourceBundle();
/* 2678 */     if (rb != null) {
/* 2679 */       l = rb.getLocale();
/* 2680 */       if (l == null || isEmpty(l.getLanguage()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2686 */         l = Locale.getDefault();
/*      */       }
/*      */     } else {
/* 2689 */       l = null;
/*      */     } 
/* 2691 */     return l;
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
/*      */   private void appendContentLang(MimePart p, Locale l) {
/*      */     try {
/* 2706 */       String lang = LogManagerProperties.toLanguageTag(l);
/* 2707 */       if (lang.length() != 0) {
/* 2708 */         String header = p.getHeader("Content-Language", null);
/* 2709 */         if (isEmpty(header)) {
/* 2710 */           p.setHeader("Content-Language", lang);
/* 2711 */         } else if (!header.equalsIgnoreCase(lang)) {
/* 2712 */           lang = ",".concat(lang);
/* 2713 */           int idx = 0;
/* 2714 */           while ((idx = header.indexOf(lang, idx)) > -1) {
/* 2715 */             idx += lang.length();
/* 2716 */             if (idx == header.length() || header.charAt(idx) == ',') {
/*      */               break;
/*      */             }
/*      */           } 
/*      */ 
/*      */           
/* 2722 */           if (idx < 0) {
/* 2723 */             int len = header.lastIndexOf("\r\n\t");
/* 2724 */             if (len < 0) {
/* 2725 */               len = 20 + header.length();
/*      */             } else {
/* 2727 */               len = header.length() - len + 8;
/*      */             } 
/*      */ 
/*      */             
/* 2731 */             if (len + lang.length() > 76) {
/* 2732 */               header = header.concat("\r\n\t".concat(lang));
/*      */             } else {
/* 2734 */               header = header.concat(lang);
/*      */             } 
/* 2736 */             p.setHeader("Content-Language", header);
/*      */           } 
/*      */         } 
/*      */       } 
/* 2740 */     } catch (MessagingException ME) {
/* 2741 */       reportError(ME.getMessage(), (Exception)ME, 5);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setAcceptLang(Part p) {
/*      */     try {
/* 2753 */       String lang = LogManagerProperties.toLanguageTag(Locale.getDefault());
/*      */       
/* 2755 */       if (lang.length() != 0) {
/* 2756 */         p.setHeader("Accept-Language", lang);
/*      */       }
/* 2758 */     } catch (MessagingException ME) {
/* 2759 */       reportError(ME.getMessage(), (Exception)ME, 5);
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
/*      */   private void reportFilterError(LogRecord record) {
/* 2772 */     assert Thread.holdsLock(this);
/* 2773 */     SimpleFormatter f = new SimpleFormatter();
/* 2774 */     String msg = "Log record " + record.getSequenceNumber() + " was filtered from all message parts.  " + head(f) + format(f, record) + tail(f, "");
/*      */ 
/*      */     
/* 2777 */     String txt = getFilter() + ", " + Arrays.<Filter>asList(readOnlyAttachmentFilters());
/*      */     
/* 2779 */     reportError(msg, new IllegalArgumentException(txt), 5);
/*      */   }
/*      */ 
/*      */   
/*      */   private void reportNullError(int code) {
/* 2784 */     reportError("null", new NullPointerException(), code);
/*      */   }
/*      */   
/*      */   private String head(Formatter f) {
/*      */     try {
/* 2789 */       return f.getHead(this);
/* 2790 */     } catch (RuntimeException RE) {
/* 2791 */       reportError(RE.getMessage(), RE, 5);
/* 2792 */       return "";
/*      */     } 
/*      */   }
/*      */   
/*      */   private String format(Formatter f, LogRecord r) {
/*      */     try {
/* 2798 */       return f.format(r);
/* 2799 */     } catch (RuntimeException RE) {
/* 2800 */       reportError(RE.getMessage(), RE, 5);
/* 2801 */       return "";
/*      */     } 
/*      */   }
/*      */   
/*      */   private String tail(Formatter f, String def) {
/*      */     try {
/* 2807 */       return f.getTail(this);
/* 2808 */     } catch (RuntimeException RE) {
/* 2809 */       reportError(RE.getMessage(), RE, 5);
/* 2810 */       return def;
/*      */     } 
/*      */   }
/*      */   private void setMailer(Message msg) {
/*      */     try {
/*      */       String str;
/* 2816 */       Class mail = MailHandler.class;
/* 2817 */       Class k = getClass();
/*      */       
/* 2819 */       if (k == mail) {
/* 2820 */         str = mail.getName();
/*      */       } else {
/*      */         try {
/* 2823 */           str = MimeUtility.encodeText(k.getName());
/* 2824 */         } catch (UnsupportedEncodingException E) {
/* 2825 */           reportError(E.getMessage(), E, 5);
/* 2826 */           str = k.getName().replaceAll("[^\\x00-\\x7F]", "\032");
/*      */         } 
/* 2828 */         str = MimeUtility.fold(10, mail.getName() + " using the " + str + " extension.");
/*      */       } 
/*      */       
/* 2831 */       msg.setHeader("X-Mailer", str);
/* 2832 */     } catch (MessagingException ME) {
/* 2833 */       reportError(ME.getMessage(), (Exception)ME, 5);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void setPriority(Message msg) {
/*      */     try {
/* 2839 */       msg.setHeader("Importance", "High");
/* 2840 */       msg.setHeader("Priority", "urgent");
/* 2841 */       msg.setHeader("X-Priority", "2");
/* 2842 */     } catch (MessagingException ME) {
/* 2843 */       reportError(ME.getMessage(), (Exception)ME, 5);
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
/*      */ 
/*      */   
/*      */   private void setIncompleteCopy(Message msg) {
/*      */     try {
/* 2859 */       msg.setHeader("Incomplete-Copy", "");
/* 2860 */     } catch (MessagingException ME) {
/* 2861 */       reportError(ME.getMessage(), (Exception)ME, 5);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setAutoSubmitted(Message msg) {
/*      */     try {
/* 2873 */       msg.setHeader("auto-submitted", "auto-generated");
/* 2874 */     } catch (MessagingException ME) {
/* 2875 */       reportError(ME.getMessage(), (Exception)ME, 5);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void setFrom(Message msg, Properties props) {
/* 2880 */     String from = props.getProperty("mail.from");
/* 2881 */     if (from != null && from.length() > 0) {
/*      */       try {
/* 2883 */         InternetAddress[] arrayOfInternetAddress = InternetAddress.parse(from, false);
/* 2884 */         if (arrayOfInternetAddress == null || arrayOfInternetAddress.length == 0) {
/* 2885 */           setDefaultFrom(msg);
/*      */         }
/* 2887 */         else if (arrayOfInternetAddress.length == 1) {
/* 2888 */           msg.setFrom((Address)arrayOfInternetAddress[0]);
/*      */         } else {
/* 2890 */           msg.addFrom((Address[])arrayOfInternetAddress);
/*      */         }
/*      */       
/* 2893 */       } catch (MessagingException ME) {
/* 2894 */         reportError(ME.getMessage(), (Exception)ME, 5);
/* 2895 */         setDefaultFrom(msg);
/*      */       } 
/*      */     } else {
/* 2898 */       setDefaultFrom(msg);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void setDefaultFrom(Message msg) {
/*      */     try {
/* 2904 */       msg.setFrom();
/* 2905 */     } catch (MessagingException ME) {
/* 2906 */       reportError(ME.getMessage(), (Exception)ME, 5);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void setReplyTo(Message msg, Properties props) {
/* 2911 */     String reply = props.getProperty("mail.reply.to");
/* 2912 */     if (reply != null && reply.length() > 0) {
/*      */       try {
/* 2914 */         InternetAddress[] arrayOfInternetAddress = InternetAddress.parse(reply, false);
/* 2915 */         if (arrayOfInternetAddress != null && arrayOfInternetAddress.length > 0) {
/* 2916 */           msg.setReplyTo((Address[])arrayOfInternetAddress);
/*      */         }
/* 2918 */       } catch (MessagingException ME) {
/* 2919 */         reportError(ME.getMessage(), (Exception)ME, 5);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private void setSender(Message msg, Properties props) {
/* 2925 */     assert msg instanceof MimeMessage : msg;
/* 2926 */     String sender = props.getProperty("mail.sender");
/* 2927 */     if (sender != null && sender.length() > 0) {
/*      */       try {
/* 2929 */         InternetAddress[] address = InternetAddress.parse(sender, false);
/*      */         
/* 2931 */         if (address != null && address.length > 0) {
/* 2932 */           ((MimeMessage)msg).setSender((Address)address[0]);
/* 2933 */           if (address.length > 1) {
/* 2934 */             reportError("Ignoring other senders.", (Exception)tooManyAddresses((Address[])address, 1), 5);
/*      */           }
/*      */         }
/*      */       
/*      */       }
/* 2939 */       catch (MessagingException ME) {
/* 2940 */         reportError(ME.getMessage(), (Exception)ME, 5);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static AddressException tooManyAddresses(Address[] address, int offset) {
/* 2946 */     String msg = Arrays.<Address>asList(address).subList(offset, address.length).toString();
/* 2947 */     return new AddressException(msg);
/*      */   }
/*      */ 
/*      */   
/*      */   private void setRecipient(Message msg, Properties props, String key, Message.RecipientType type) {
/* 2952 */     String value = props.getProperty(key);
/* 2953 */     if (value != null && value.length() > 0) {
/*      */       try {
/* 2955 */         InternetAddress[] arrayOfInternetAddress = InternetAddress.parse(value, false);
/* 2956 */         if (arrayOfInternetAddress != null && arrayOfInternetAddress.length > 0) {
/* 2957 */           msg.setRecipients(type, (Address[])arrayOfInternetAddress);
/*      */         }
/* 2959 */       } catch (MessagingException ME) {
/* 2960 */         reportError(ME.getMessage(), (Exception)ME, 5);
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
/*      */ 
/*      */   
/*      */   private String toRawString(Message msg) throws MessagingException, IOException {
/* 2975 */     if (msg != null) {
/* 2976 */       int nbytes = Math.max(msg.getSize() + 1024, 1024);
/* 2977 */       ByteArrayOutputStream out = new ByteArrayOutputStream(nbytes);
/* 2978 */       msg.writeTo(out);
/* 2979 */       return out.toString("US-ASCII");
/*      */     } 
/* 2981 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String toMsgString(Throwable t) {
/* 2991 */     if (t == null) {
/* 2992 */       return "null";
/*      */     }
/*      */     
/* 2995 */     String encoding = getEncodingName();
/*      */     try {
/* 2997 */       ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
/*      */ 
/*      */ 
/*      */       
/* 3001 */       PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, encoding));
/*      */       
/* 3003 */       pw.println(t.getMessage());
/* 3004 */       t.printStackTrace(pw);
/* 3005 */       pw.flush();
/* 3006 */       pw.close();
/* 3007 */       return out.toString(encoding);
/* 3008 */     } catch (IOException badMimeCharset) {
/* 3009 */       return t.toString() + ' ' + badMimeCharset.toString();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object getAndSetContextClassLoader() {
/*      */     try {
/* 3021 */       return AccessController.doPrivileged(GET_AND_SET_CCL);
/* 3022 */     } catch (SecurityException ignore) {
/* 3023 */       return GET_AND_SET_CCL;
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
/*      */   private void setContextClassLoader(Object ccl) {
/* 3035 */     if (ccl == null || ccl instanceof ClassLoader) {
/* 3036 */       AccessController.doPrivileged(new GetAndSetContext(ccl));
/*      */     }
/*      */   }
/*      */   
/*      */   private static RuntimeException attachmentMismatch(String msg) {
/* 3041 */     return new IndexOutOfBoundsException(msg);
/*      */   }
/*      */   
/*      */   private static RuntimeException attachmentMismatch(int expected, int found) {
/* 3045 */     return attachmentMismatch("Attachments mismatched, expected " + expected + " but given " + found + '.');
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static MessagingException attach(MessagingException required, Exception optional) {
/* 3051 */     if (optional != null && !required.setNextException(optional) && 
/* 3052 */       optional instanceof MessagingException) {
/* 3053 */       MessagingException head = (MessagingException)optional;
/* 3054 */       if (head.setNextException((Exception)required)) {
/* 3055 */         return head;
/*      */       }
/*      */     } 
/*      */     
/* 3059 */     return required;
/*      */   }
/*      */   
/*      */   private static String atIndexMsg(int i) {
/* 3063 */     return "At index: " + i + '.';
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class DefaultAuthenticator
/*      */     extends Authenticator
/*      */   {
/*      */     private final String pass;
/*      */     
/*      */     static final boolean $assertionsDisabled;
/*      */     
/*      */     DefaultAuthenticator(String pass) {
/* 3075 */       assert pass != null;
/* 3076 */       this.pass = pass;
/*      */     }
/*      */     
/*      */     protected final PasswordAuthentication getPasswordAuthentication() {
/* 3080 */       return new PasswordAuthentication(getDefaultUserName(), this.pass);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class GetAndSetContext
/*      */     implements PrivilegedAction
/*      */   {
/*      */     private final Object source;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     GetAndSetContext(Object source) {
/* 3101 */       this.source = source;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final Object run() {
/*      */       ClassLoader loader;
/* 3111 */       Thread current = Thread.currentThread();
/* 3112 */       ClassLoader ccl = current.getContextClassLoader();
/*      */       
/* 3114 */       if (this.source == null) {
/* 3115 */         loader = null;
/* 3116 */       } else if (this.source instanceof ClassLoader) {
/* 3117 */         loader = (ClassLoader)this.source;
/* 3118 */       } else if (this.source instanceof Class) {
/* 3119 */         loader = ((Class)this.source).getClassLoader();
/*      */       } else {
/* 3121 */         loader = this.source.getClass().getClassLoader();
/*      */       } 
/*      */       
/* 3124 */       if (ccl != loader) {
/* 3125 */         current.setContextClassLoader(loader);
/* 3126 */         return ccl;
/*      */       } 
/* 3128 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class TailNameFormatter
/*      */     extends Formatter
/*      */   {
/*      */     private final String name;
/*      */ 
/*      */     
/*      */     static final boolean $assertionsDisabled;
/*      */ 
/*      */ 
/*      */     
/*      */     TailNameFormatter(String name) {
/* 3146 */       assert name != null;
/* 3147 */       this.name = name;
/*      */     }
/*      */     
/*      */     public final String format(LogRecord record) {
/* 3151 */       return "";
/*      */     }
/*      */     
/*      */     public final String getTail(Handler h) {
/* 3155 */       return this.name;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final boolean equals(Object o) {
/* 3165 */       if (o instanceof TailNameFormatter) {
/* 3166 */         return this.name.equals(((TailNameFormatter)o).name);
/*      */       }
/* 3168 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final int hashCode() {
/* 3177 */       return getClass().hashCode() + this.name.hashCode();
/*      */     }
/*      */     
/*      */     public final String toString() {
/* 3181 */       return this.name;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\logging\MailHandler.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */