package com.sun.mail.util.logging;

import com.sun.mail.smtp.SMTPTransport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

public class MailHandler extends Handler {
   private static final Filter[] EMPTY_FILTERS;
   private static final Formatter[] EMPTY_FORMATTERS;
   private static final int MIN_HEADER_SIZE = 1024;
   private static final int offValue;
   private static final GetAndSetContext GET_AND_SET_CCL;
   private static final ThreadLocal MUTEX;
   private static final Object MUTEX_PUBLISH;
   private static final Object MUTEX_REPORT;
   private static final Method REMOVE;
   private volatile boolean sealed;
   private boolean isWriting;
   private Properties mailProps;
   private Authenticator auth;
   private Session session;
   private LogRecord[] data;
   private int size;
   private int capacity;
   private Comparator comparator;
   private Formatter subjectFormatter;
   private Level pushLevel;
   private Filter pushFilter;
   private Filter[] attachmentFilters;
   private Formatter[] attachmentFormatters;
   private Formatter[] attachmentNames;
   private FileTypeMap contentTypes;
   // $FF: synthetic field
   static final boolean $assertionsDisabled;

   public MailHandler() {
      this.init(true);
      this.sealed = true;
   }

   public MailHandler(int capacity) {
      this.init(true);
      this.sealed = true;
      this.setCapacity0(capacity);
   }

   public MailHandler(Properties props) {
      this.init(false);
      this.sealed = true;
      this.setMailProperties0(props);
   }

   public boolean isLoggable(LogRecord record) {
      int levelValue = this.getLevel().intValue();
      if (record.getLevel().intValue() >= levelValue && levelValue != offValue) {
         Filter body = this.getFilter();
         return body != null && !body.isLoggable(record) ? this.isAttachmentLoggable(record) : true;
      } else {
         return false;
      }
   }

   public void publish(LogRecord record) {
      if (this.tryMutex()) {
         try {
            if (this.isLoggable(record)) {
               record.getSourceMethodName();
               this.publish0(record);
            }
         } finally {
            this.releaseMutex();
         }
      } else {
         this.reportUnPublishedError(record);
      }

   }

   private void publish0(LogRecord record) {
      MessageContext ctx;
      boolean priority;
      synchronized(this) {
         if (this.size == this.data.length && this.size < this.capacity) {
            this.grow();
         }

         if (this.size < this.data.length) {
            this.data[this.size] = record;
            ++this.size;
            priority = this.isPushable(record);
            if (!priority && this.size < this.capacity) {
               ctx = null;
            } else {
               ctx = this.writeLogRecords(1);
            }
         } else {
            priority = false;
            ctx = null;
         }
      }

      if (ctx != null) {
         this.send(ctx, priority, 1);
      }

   }

   private void reportUnPublishedError(LogRecord record) {
      if (MUTEX_PUBLISH.equals(MUTEX.get())) {
         MUTEX.set(MUTEX_REPORT);

         try {
            String msg;
            if (record != null) {
               SimpleFormatter f = new SimpleFormatter();
               msg = "Log record " + record.getSequenceNumber() + " was not published. " + this.head(f) + this.format(f, record) + this.tail(f, "");
            } else {
               msg = null;
            }

            Exception e = new IllegalStateException("Recursive publish detected by thread " + Thread.currentThread());
            this.reportError((String)msg, e, 1);
         } finally {
            MUTEX.set(MUTEX_PUBLISH);
         }
      }

   }

   private boolean tryMutex() {
      if (MUTEX.get() == null) {
         MUTEX.set(MUTEX_PUBLISH);
         return true;
      } else {
         return false;
      }
   }

   private void releaseMutex() {
      if (REMOVE != null) {
         try {
            REMOVE.invoke(MUTEX, (Object[])null);
         } catch (RuntimeException var2) {
            MUTEX.set((Object)null);
         } catch (Exception var3) {
            MUTEX.set((Object)null);
         }
      } else {
         MUTEX.set((Object)null);
      }

   }

   public void push() {
      this.push(true, 2);
   }

   public void flush() {
      this.push(false, 2);
   }

   public void close() {
      Object ccl = this.getAndSetContextClassLoader();

      try {
         MessageContext ctx = null;
         synchronized(this) {
            super.setLevel(Level.OFF);

            try {
               ctx = this.writeLogRecords(3);
            } finally {
               if (this.capacity > 0) {
                  this.capacity = -this.capacity;
               }

               if (this.size == 0 && this.data.length != 1) {
                  this.data = new LogRecord[1];
               }

            }
         }

         if (ctx != null) {
            this.send(ctx, false, 3);
         }
      } finally {
         this.setContextClassLoader(ccl);
      }

   }

   public synchronized void setLevel(Level newLevel) {
      if (this.capacity > 0) {
         super.setLevel(newLevel);
      } else {
         if (newLevel == null) {
            throw new NullPointerException();
         }

         this.checkAccess();
      }

   }

   public final synchronized Level getPushLevel() {
      return this.pushLevel;
   }

   public final synchronized void setPushLevel(Level level) {
      this.checkAccess();
      if (level == null) {
         throw new NullPointerException();
      } else if (this.isWriting) {
         throw new IllegalStateException();
      } else {
         this.pushLevel = level;
      }
   }

   public final synchronized Filter getPushFilter() {
      return this.pushFilter;
   }

   public final synchronized void setPushFilter(Filter filter) {
      this.checkAccess();
      if (this.isWriting) {
         throw new IllegalStateException();
      } else {
         this.pushFilter = filter;
      }
   }

   public final synchronized Comparator getComparator() {
      return this.comparator;
   }

   public final synchronized void setComparator(Comparator c) {
      this.checkAccess();
      if (this.isWriting) {
         throw new IllegalStateException();
      } else {
         this.comparator = c;
      }
   }

   public final synchronized int getCapacity() {
      if ($assertionsDisabled || this.capacity != Integer.MIN_VALUE && this.capacity != 0) {
         return Math.abs(this.capacity);
      } else {
         throw new AssertionError(this.capacity);
      }
   }

   public final synchronized Authenticator getAuthenticator() {
      this.checkAccess();
      return this.auth;
   }

   public final void setAuthenticator(Authenticator auth) {
      this.setAuthenticator0(auth);
   }

   public final void setAuthenticator(char[] password) {
      if (password == null) {
         this.setAuthenticator0((Authenticator)null);
      } else {
         this.setAuthenticator0(new DefaultAuthenticator(new String(password)));
      }

   }

   private void setAuthenticator0(Authenticator auth) {
      this.checkAccess();
      Session settings;
      synchronized(this) {
         if (this.isWriting) {
            throw new IllegalStateException();
         }

         this.auth = auth;
         settings = this.fixUpSession();
      }

      this.verifySettings(settings);
   }

   public final void setMailProperties(Properties props) {
      this.setMailProperties0(props);
   }

   private void setMailProperties0(Properties props) {
      this.checkAccess();
      props = (Properties)props.clone();
      Session settings;
      synchronized(this) {
         if (this.isWriting) {
            throw new IllegalStateException();
         }

         this.mailProps = props;
         settings = this.fixUpSession();
      }

      this.verifySettings(settings);
   }

   public final Properties getMailProperties() {
      this.checkAccess();
      Properties props;
      synchronized(this) {
         props = this.mailProps;
      }

      return (Properties)props.clone();
   }

   public final Filter[] getAttachmentFilters() {
      return (Filter[])((Filter[])this.readOnlyAttachmentFilters().clone());
   }

   public final void setAttachmentFilters(Filter[] filters) {
      this.checkAccess();
      filters = (Filter[])((Filter[])copyOf(filters, filters.length, Filter[].class));
      synchronized(this) {
         if (this.attachmentFormatters.length != filters.length) {
            throw attachmentMismatch(this.attachmentFormatters.length, filters.length);
         } else if (this.isWriting) {
            throw new IllegalStateException();
         } else {
            this.attachmentFilters = filters;
         }
      }
   }

   public final Formatter[] getAttachmentFormatters() {
      Formatter[] formatters;
      synchronized(this) {
         formatters = this.attachmentFormatters;
      }

      return (Formatter[])((Formatter[])formatters.clone());
   }

   public final void setAttachmentFormatters(Formatter[] formatters) {
      this.checkAccess();
      if (formatters.length == 0) {
         formatters = emptyFormatterArray();
      } else {
         formatters = (Formatter[])((Formatter[])copyOf(formatters, formatters.length, Formatter[].class));

         for(int i = 0; i < formatters.length; ++i) {
            if (formatters[i] == null) {
               throw new NullPointerException(atIndexMsg(i));
            }
         }
      }

      synchronized(this) {
         if (this.isWriting) {
            throw new IllegalStateException();
         } else {
            this.attachmentFormatters = formatters;
            this.fixUpAttachmentFilters();
            this.fixUpAttachmentNames();
         }
      }
   }

   public final Formatter[] getAttachmentNames() {
      Formatter[] formatters;
      synchronized(this) {
         formatters = this.attachmentNames;
      }

      return (Formatter[])((Formatter[])formatters.clone());
   }

   public final void setAttachmentNames(String[] names) {
      this.checkAccess();
      Formatter[] formatters;
      if (names.length == 0) {
         formatters = emptyFormatterArray();
      } else {
         formatters = new Formatter[names.length];
      }

      for(int i = 0; i < names.length; ++i) {
         String name = names[i];
         if (name == null) {
            throw new NullPointerException(atIndexMsg(i));
         }

         if (name.length() <= 0) {
            throw new IllegalArgumentException(atIndexMsg(i));
         }

         formatters[i] = new TailNameFormatter(name);
      }

      synchronized(this) {
         if (this.attachmentFormatters.length != names.length) {
            throw attachmentMismatch(this.attachmentFormatters.length, names.length);
         } else if (this.isWriting) {
            throw new IllegalStateException();
         } else {
            this.attachmentNames = formatters;
         }
      }
   }

   public final void setAttachmentNames(Formatter[] formatters) {
      this.checkAccess();
      formatters = (Formatter[])((Formatter[])copyOf(formatters, formatters.length, Formatter[].class));

      for(int i = 0; i < formatters.length; ++i) {
         if (formatters[i] == null) {
            throw new NullPointerException(atIndexMsg(i));
         }
      }

      synchronized(this) {
         if (this.attachmentFormatters.length != formatters.length) {
            throw attachmentMismatch(this.attachmentFormatters.length, formatters.length);
         } else if (this.isWriting) {
            throw new IllegalStateException();
         } else {
            this.attachmentNames = formatters;
         }
      }
   }

   public final synchronized Formatter getSubject() {
      return this.subjectFormatter;
   }

   public final void setSubject(String subject) {
      if (subject != null) {
         this.setSubject((Formatter)(new TailNameFormatter(subject)));
      } else {
         this.checkAccess();
         throw new NullPointerException();
      }
   }

   public final void setSubject(Formatter format) {
      this.checkAccess();
      if (format == null) {
         throw new NullPointerException();
      } else {
         synchronized(this) {
            if (this.isWriting) {
               throw new IllegalStateException();
            } else {
               this.subjectFormatter = format;
            }
         }
      }
   }

   protected void reportError(String msg, Exception ex, int code) {
      if (msg != null) {
         super.reportError(Level.SEVERE.getName() + ": " + msg, ex, code);
      } else {
         super.reportError((String)null, ex, code);
      }

   }

   final void checkAccess() {
      if (this.sealed) {
         LogManagerProperties.getLogManager().checkAccess();
      }

   }

   final String contentTypeOf(String head) {
      if (head != null && head.length() > 0) {
         int MAX_CHARS = true;
         if (head.length() > 25) {
            head = head.substring(0, 25);
         }

         try {
            String encoding = this.getEncodingName();
            ByteArrayInputStream in = new ByteArrayInputStream(head.getBytes(encoding));
            if (!$assertionsDisabled && !in.markSupported()) {
               throw new AssertionError(in.getClass().getName());
            }

            return URLConnection.guessContentTypeFromStream(in);
         } catch (IOException var5) {
            this.reportError((String)var5.getMessage(), var5, 5);
         }
      }

      return null;
   }

   final boolean isMissingContent(Message msg, Throwable t) {
      for(Throwable cause = t.getCause(); cause != null; cause = cause.getCause()) {
         t = cause;
      }

      try {
         msg.writeTo(new ByteArrayOutputStream(1024));
      } catch (RuntimeException var5) {
         throw var5;
      } catch (Exception var6) {
         String txt = var6.getMessage();
         if (!isEmpty(txt) && var6.getClass() == t.getClass()) {
            return txt.equals(t.getMessage());
         }
      }

      return false;
   }

   private void reportError(Message msg, Exception ex, int code) {
      try {
         super.reportError(this.toRawString(msg), ex, code);
      } catch (MessagingException var5) {
         this.reportError(this.toMsgString(var5), ex, code);
      } catch (IOException var6) {
         this.reportError(this.toMsgString(var6), ex, code);
      }

   }

   private String getContentType(String name) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String type = this.contentTypes.getContentType(name);
         return "application/octet-stream".equalsIgnoreCase(type) ? null : type;
      }
   }

   private String getEncodingName() {
      String encoding = this.getEncoding();
      if (encoding == null) {
         encoding = MimeUtility.getDefaultJavaCharset();
      }

      return encoding;
   }

   private void setContent(MimeBodyPart part, CharSequence buf, String type) throws MessagingException {
      String encoding = this.getEncodingName();
      if (type != null && !"text/plain".equalsIgnoreCase(type)) {
         type = this.contentWithEncoding(type, encoding);

         try {
            DataSource source = new ByteArrayDataSource(buf.toString(), type);
            part.setDataHandler(new DataHandler(source));
         } catch (IOException var6) {
            this.reportError((String)var6.getMessage(), var6, 5);
            part.setText(buf.toString(), encoding);
         }
      } else {
         part.setText(buf.toString(), MimeUtility.mimeCharset(encoding));
      }

   }

   private String contentWithEncoding(String type, String encoding) {
      if (!$assertionsDisabled && encoding == null) {
         throw new AssertionError();
      } else {
         try {
            ContentType ct = new ContentType(type);
            ct.setParameter("charset", MimeUtility.mimeCharset(encoding));
            encoding = ct.toString();
            if (!isEmpty(encoding)) {
               type = encoding;
            }
         } catch (MessagingException var4) {
            this.reportError((String)type, var4, 5);
         }

         return type;
      }
   }

   private synchronized void setCapacity0(int newCapacity) {
      if (newCapacity <= 0) {
         throw new IllegalArgumentException("Capacity must be greater than zero.");
      } else if (this.isWriting) {
         throw new IllegalStateException();
      } else {
         if (this.capacity < 0) {
            this.capacity = -newCapacity;
         } else {
            this.capacity = newCapacity;
         }

      }
   }

   private synchronized Filter[] readOnlyAttachmentFilters() {
      return this.attachmentFilters;
   }

   private static Formatter[] emptyFormatterArray() {
      return EMPTY_FORMATTERS;
   }

   private static Filter[] emptyFilterArray() {
      return EMPTY_FILTERS;
   }

   private boolean fixUpAttachmentNames() {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         boolean fixed = false;
         int expect = this.attachmentFormatters.length;
         int current = this.attachmentNames.length;
         if (current != expect) {
            this.attachmentNames = (Formatter[])((Formatter[])copyOf(this.attachmentNames, expect));
            fixed = current != 0;
         }

         if (expect == 0) {
            this.attachmentNames = emptyFormatterArray();
            if (!$assertionsDisabled && this.attachmentNames.length != 0) {
               throw new AssertionError();
            }
         } else {
            for(int i = 0; i < expect; ++i) {
               if (this.attachmentNames[i] == null) {
                  this.attachmentNames[i] = new TailNameFormatter(this.toString(this.attachmentFormatters[i]));
               }
            }
         }

         return fixed;
      }
   }

   private boolean fixUpAttachmentFilters() {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         boolean fixed = false;
         int expect = this.attachmentFormatters.length;
         int current = this.attachmentFilters.length;
         if (current != expect) {
            this.attachmentFilters = (Filter[])((Filter[])copyOf(this.attachmentFilters, expect));
            fixed = current != 0;
         }

         if (expect == 0) {
            this.attachmentFilters = emptyFilterArray();
            if (!$assertionsDisabled && this.attachmentFilters.length != 0) {
               throw new AssertionError();
            }
         }

         return fixed;
      }
   }

   private static Object[] copyOf(Object[] a, int size) {
      Object[] copy = (Object[])((Object[])Array.newInstance(a.getClass().getComponentType(), size));
      System.arraycopy(a, 0, copy, 0, Math.min(a.length, size));
      return copy;
   }

   private static Object[] copyOf(Object[] a, int len, Class type) {
      if (type == a.getClass()) {
         return (Object[])((Object[])a.clone());
      } else {
         Object[] copy = (Object[])((Object[])Array.newInstance(type.getComponentType(), len));
         System.arraycopy(a, 0, copy, 0, Math.min(len, a.length));
         return copy;
      }
   }

   private void reset() {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         if (this.size < this.data.length) {
            Arrays.fill(this.data, 0, this.size, (Object)null);
         } else {
            Arrays.fill(this.data, (Object)null);
         }

         this.size = 0;
      }
   }

   private void grow() {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         int len = this.data.length;
         int newCapacity = len + (len >> 1) + 1;
         if (newCapacity > this.capacity || newCapacity < len) {
            newCapacity = this.capacity;
         }

         if (!$assertionsDisabled && len == this.capacity) {
            throw new AssertionError(len);
         } else {
            this.data = (LogRecord[])((LogRecord[])copyOf(this.data, newCapacity));
         }
      }
   }

   private synchronized void init(boolean inherit) {
      LogManager manager = LogManagerProperties.getLogManager();
      String p = this.getClass().getName();
      this.mailProps = new Properties();
      this.contentTypes = FileTypeMap.getDefaultFileTypeMap();
      this.initErrorManager(manager, p);
      this.initLevel(manager, p);
      this.initFilter(manager, p);
      this.initCapacity(manager, p);
      this.initAuthenticator(manager, p);
      this.initEncoding(manager, p);
      this.initFormatter(manager, p);
      this.initComparator(manager, p);
      this.initPushLevel(manager, p);
      this.initPushFilter(manager, p);
      this.initSubject(manager, p);
      this.initAttachmentFormaters(manager, p);
      this.initAttachmentFilters(manager, p);
      this.initAttachmentNames(manager, p);
      if (inherit && manager.getProperty(p.concat(".verify")) != null) {
         this.verifySettings(this.initSession());
      }

   }

   private static boolean isEmpty(String s) {
      return s == null || s.length() == 0;
   }

   private static boolean hasValue(String name) {
      return !isEmpty(name) && !"null".equalsIgnoreCase(name);
   }

   private void initAttachmentFilters(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else if (!$assertionsDisabled && this.attachmentFormatters == null) {
         throw new AssertionError();
      } else {
         String list = manager.getProperty(p.concat(".attachment.filters"));
         if (list != null && list.length() > 0) {
            String[] names = list.split(",");
            Filter[] a = new Filter[names.length];

            for(int i = 0; i < a.length; ++i) {
               names[i] = names[i].trim();
               if (!"null".equalsIgnoreCase(names[i])) {
                  try {
                     a[i] = LogManagerProperties.newFilter(names[i]);
                  } catch (SecurityException var8) {
                     throw var8;
                  } catch (Exception var9) {
                     this.reportError((String)var9.getMessage(), var9, 4);
                  }
               }
            }

            this.attachmentFilters = a;
            if (this.fixUpAttachmentFilters()) {
               this.reportError((String)"Attachment filters.", attachmentMismatch("Length mismatch."), 4);
            }
         } else {
            this.attachmentFilters = emptyFilterArray();
            this.fixUpAttachmentFilters();
         }

      }
   }

   private void initAttachmentFormaters(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String list = manager.getProperty(p.concat(".attachment.formatters"));
         if (list != null && list.length() > 0) {
            String[] names = list.split(",");
            Formatter[] a;
            if (names.length == 0) {
               a = emptyFormatterArray();
            } else {
               a = new Formatter[names.length];
            }

            for(int i = 0; i < a.length; ++i) {
               names[i] = names[i].trim();
               if (!"null".equalsIgnoreCase(names[i])) {
                  try {
                     a[i] = LogManagerProperties.newFormatter(names[i]);
                     if (a[i] instanceof TailNameFormatter) {
                        a[i] = new SimpleFormatter();
                        Exception CNFE = new ClassNotFoundException(a[i].toString());
                        this.reportError((String)"Attachment formatter.", CNFE, 4);
                     }
                  } catch (SecurityException var8) {
                     throw var8;
                  } catch (Exception var9) {
                     a[i] = new SimpleFormatter();
                     this.reportError((String)var9.getMessage(), var9, 4);
                  }
               } else {
                  a[i] = new SimpleFormatter();
                  Exception NPE = new NullPointerException(atIndexMsg(i));
                  this.reportError((String)"Attachment formatter.", NPE, 4);
               }
            }

            this.attachmentFormatters = a;
         } else {
            this.attachmentFormatters = emptyFormatterArray();
         }

      }
   }

   private void initAttachmentNames(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else if (!$assertionsDisabled && this.attachmentFormatters == null) {
         throw new AssertionError();
      } else {
         String list = manager.getProperty(p.concat(".attachment.names"));
         if (list != null && list.length() > 0) {
            String[] names = list.split(",");
            Formatter[] a = new Formatter[names.length];

            for(int i = 0; i < a.length; ++i) {
               names[i] = names[i].trim();
               if (!"null".equalsIgnoreCase(names[i])) {
                  try {
                     try {
                        a[i] = LogManagerProperties.newFormatter(names[i]);
                     } catch (ClassNotFoundException var8) {
                        a[i] = new TailNameFormatter(names[i]);
                     } catch (ClassCastException var9) {
                        a[i] = new TailNameFormatter(names[i]);
                     }
                  } catch (SecurityException var10) {
                     throw var10;
                  } catch (Exception var11) {
                     this.reportError((String)var11.getMessage(), var11, 4);
                  }
               } else {
                  Exception NPE = new NullPointerException(atIndexMsg(i));
                  this.reportError((String)"Attachment names.", NPE, 4);
               }
            }

            this.attachmentNames = a;
            if (this.fixUpAttachmentNames()) {
               this.reportError((String)"Attachment names.", attachmentMismatch("Length mismatch."), 4);
            }
         } else {
            this.attachmentNames = emptyFormatterArray();
            this.fixUpAttachmentNames();
         }

      }
   }

   private void initAuthenticator(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String name = manager.getProperty(p.concat(".authenticator"));
         if (hasValue(name)) {
            try {
               this.auth = LogManagerProperties.newAuthenticator(name);
            } catch (SecurityException var5) {
               throw var5;
            } catch (ClassNotFoundException var6) {
               this.auth = new DefaultAuthenticator(name);
            } catch (ClassCastException var7) {
               this.auth = new DefaultAuthenticator(name);
            } catch (Exception var8) {
               this.reportError((String)var8.getMessage(), var8, 4);
            }
         }

      }
   }

   private void initLevel(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         try {
            String val = manager.getProperty(p.concat(".level"));
            if (val != null) {
               super.setLevel(Level.parse(val));
            } else {
               super.setLevel(Level.WARNING);
            }
         } catch (SecurityException var6) {
            throw var6;
         } catch (RuntimeException var7) {
            this.reportError((String)var7.getMessage(), var7, 4);

            try {
               super.setLevel(Level.WARNING);
            } catch (RuntimeException var5) {
               this.reportError((String)var5.getMessage(), var5, 4);
            }
         }

      }
   }

   private void initFilter(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         try {
            String name = manager.getProperty(p.concat(".filter"));
            if (hasValue(name)) {
               super.setFilter(LogManagerProperties.newFilter(name));
            }
         } catch (SecurityException var4) {
            throw var4;
         } catch (Exception var5) {
            this.reportError((String)var5.getMessage(), var5, 4);
         }

      }
   }

   private void initCapacity(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         int DEFAULT_CAPACITY = true;

         try {
            String value = manager.getProperty(p.concat(".capacity"));
            if (value != null) {
               this.setCapacity0(Integer.parseInt(value));
            } else {
               this.setCapacity0(1000);
            }
         } catch (RuntimeException var5) {
            this.reportError((String)var5.getMessage(), var5, 4);
         }

         if (this.capacity <= 0) {
            this.capacity = 1000;
         }

         this.data = new LogRecord[1];
      }
   }

   private void initEncoding(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         try {
            super.setEncoding(manager.getProperty(p.concat(".encoding")));
         } catch (SecurityException var4) {
            throw var4;
         } catch (UnsupportedEncodingException var5) {
            this.reportError((String)var5.getMessage(), var5, 4);
         } catch (RuntimeException var6) {
            this.reportError((String)var6.getMessage(), var6, 4);
         }

      }
   }

   private void initErrorManager(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String name = manager.getProperty(p.concat(".errorManager"));
         if (name != null) {
            try {
               ErrorManager em = LogManagerProperties.newErrorManager(name);
               super.setErrorManager(em);
            } catch (SecurityException var5) {
               throw var5;
            } catch (Exception var6) {
               this.reportError((String)var6.getMessage(), var6, 4);
            }
         }

      }
   }

   private void initFormatter(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String name = manager.getProperty(p.concat(".formatter"));
         if (hasValue(name)) {
            try {
               Formatter formatter = LogManagerProperties.newFormatter(name);
               if (!$assertionsDisabled && formatter == null) {
                  throw new AssertionError();
               }

               if (!(formatter instanceof TailNameFormatter)) {
                  super.setFormatter(formatter);
               } else {
                  super.setFormatter(new SimpleFormatter());
               }
            } catch (SecurityException var7) {
               throw var7;
            } catch (Exception var8) {
               this.reportError((String)var8.getMessage(), var8, 4);

               try {
                  super.setFormatter(new SimpleFormatter());
               } catch (RuntimeException var6) {
                  this.reportError((String)var6.getMessage(), var6, 4);
               }
            }
         } else {
            super.setFormatter(new SimpleFormatter());
         }

      }
   }

   private void initComparator(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String name = manager.getProperty(p.concat(".comparator"));
         if (hasValue(name)) {
            try {
               this.comparator = LogManagerProperties.newComparator(name);
            } catch (SecurityException var5) {
               throw var5;
            } catch (Exception var6) {
               this.reportError((String)var6.getMessage(), var6, 4);
            }
         }

      }
   }

   private void initPushLevel(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         try {
            String val = manager.getProperty(p.concat(".pushLevel"));
            if (val != null) {
               this.pushLevel = Level.parse(val);
            }
         } catch (RuntimeException var4) {
            this.reportError((String)var4.getMessage(), var4, 4);
         }

         if (this.pushLevel == null) {
            this.pushLevel = Level.OFF;
         }

      }
   }

   private void initPushFilter(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String name = manager.getProperty(p.concat(".pushFilter"));
         if (hasValue(name)) {
            try {
               this.pushFilter = LogManagerProperties.newFilter(name);
            } catch (SecurityException var5) {
               throw var5;
            } catch (Exception var6) {
               this.reportError((String)var6.getMessage(), var6, 4);
            }
         }

      }
   }

   private void initSubject(LogManager manager, String p) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String name = manager.getProperty(p.concat(".subject"));
         if (hasValue(name)) {
            try {
               this.subjectFormatter = LogManagerProperties.newFormatter(name);
            } catch (SecurityException var5) {
               throw var5;
            } catch (ClassNotFoundException var6) {
               this.subjectFormatter = new TailNameFormatter(name);
            } catch (ClassCastException var7) {
               this.subjectFormatter = new TailNameFormatter(name);
            } catch (Exception var8) {
               this.subjectFormatter = new TailNameFormatter(name);
               this.reportError((String)var8.getMessage(), var8, 4);
            }
         }

         if (this.subjectFormatter == null) {
            this.subjectFormatter = new TailNameFormatter("");
         }

      }
   }

   private boolean isAttachmentLoggable(LogRecord record) {
      Filter[] filters = this.readOnlyAttachmentFilters();

      for(int i = 0; i < filters.length; ++i) {
         Filter f = filters[i];
         if (f == null || f.isLoggable(record)) {
            return true;
         }
      }

      return false;
   }

   private boolean isPushable(LogRecord record) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         int value = this.getPushLevel().intValue();
         if (value != offValue && record.getLevel().intValue() >= value) {
            Filter filter = this.getPushFilter();
            return filter == null || filter.isLoggable(record);
         } else {
            return false;
         }
      }
   }

   private void push(boolean priority, int code) {
      if (this.tryMutex()) {
         try {
            MessageContext ctx = this.writeLogRecords(code);
            if (ctx != null) {
               this.send(ctx, priority, code);
            }
         } finally {
            this.releaseMutex();
         }
      } else {
         this.reportUnPublishedError((LogRecord)null);
      }

   }

   private void send(MessageContext ctx, boolean priority, int code) {
      Message msg = ctx.getMessage();

      try {
         this.envelopeFor(ctx, priority);
         Transport.send(msg);
      } catch (Exception var6) {
         this.reportError(msg, var6, code);
      }

   }

   private void sort() {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         if (this.comparator != null) {
            try {
               if (this.size != 1) {
                  Arrays.sort(this.data, 0, this.size, this.comparator);
               } else {
                  this.comparator.compare(this.data[0], this.data[0]);
               }
            } catch (RuntimeException var2) {
               this.reportError((String)var2.getMessage(), var2, 5);
            }
         }

      }
   }

   private synchronized MessageContext writeLogRecords(int code) {
      if (this.size != 0 && !this.isWriting) {
         this.isWriting = true;

         try {
            this.sort();
            if (this.session == null) {
               this.initSession();
            }

            MimeMessage msg = new MimeMessage(this.session);
            msg.setDescription(this.descriptionFrom(this.comparator, this.pushLevel, this.pushFilter));
            MimeBodyPart[] parts = new MimeBodyPart[this.attachmentFormatters.length];
            StringBuffer[] buffers = new StringBuffer[parts.length];
            String contentType = null;
            StringBuffer buf = null;
            this.appendSubject(msg, this.head(this.subjectFormatter));
            MimeBodyPart body = this.createBodyPart();
            Formatter bodyFormat = this.getFormatter();
            Filter bodyFilter = this.getFilter();
            Locale lastLocale = null;

            int i;
            for(i = 0; i < this.size; ++i) {
               boolean formatted = false;
               LogRecord r = this.data[i];
               this.data[i] = null;
               Locale locale = this.localeFor(r);
               this.appendSubject(msg, this.format(this.subjectFormatter, r));
               if (bodyFilter == null || bodyFilter.isLoggable(r)) {
                  if (buf == null) {
                     buf = new StringBuffer();
                     String head = this.head(bodyFormat);
                     buf.append(head);
                     contentType = this.contentTypeOf(head);
                  }

                  formatted = true;
                  buf.append(this.format(bodyFormat, r));
                  if (locale != null && !locale.equals(lastLocale)) {
                     this.appendContentLang(body, locale);
                  }
               }

               for(int i = 0; i < parts.length; ++i) {
                  Filter af = this.attachmentFilters[i];
                  if (af == null || af.isLoggable(r)) {
                     if (parts[i] == null) {
                        parts[i] = this.createBodyPart(i);
                        buffers[i] = new StringBuffer();
                        buffers[i].append(this.head(this.attachmentFormatters[i]));
                        this.appendFileName(parts[i], this.head(this.attachmentNames[i]));
                     }

                     formatted = true;
                     this.appendFileName(parts[i], this.format(this.attachmentNames[i], r));
                     buffers[i].append(this.format(this.attachmentFormatters[i], r));
                     if (locale != null && !locale.equals(lastLocale)) {
                        this.appendContentLang(parts[i], locale);
                     }
                  }
               }

               if (formatted) {
                  if (locale != null && !locale.equals(lastLocale)) {
                     this.appendContentLang(msg, locale);
                  }
               } else {
                  this.reportFilterError(r);
               }

               lastLocale = locale;
            }

            this.size = 0;

            String name;
            for(i = parts.length - 1; i >= 0; --i) {
               if (parts[i] != null) {
                  this.appendFileName(parts[i], this.tail(this.attachmentNames[i], "err"));
                  buffers[i].append(this.tail(this.attachmentFormatters[i], ""));
                  if (buffers[i].length() > 0) {
                     name = parts[i].getFileName();
                     if (isEmpty(name)) {
                        name = this.toString(this.attachmentFormatters[i]);
                        parts[i].setFileName(name);
                     }

                     this.setContent(parts[i], buffers[i], this.getContentType(name));
                  } else {
                     this.setIncompleteCopy(msg);
                     parts[i] = null;
                  }

                  buffers[i] = null;
               }
            }

            buffers = null;
            if (buf != null) {
               buf.append(this.tail(bodyFormat, ""));
            } else {
               buf = new StringBuffer(0);
            }

            this.appendSubject(msg, this.tail(this.subjectFormatter, ""));
            MimeMultipart multipart = new MimeMultipart();
            name = this.getContentType(bodyFormat.getClass().getName());
            this.setContent(body, buf, name == null ? contentType : name);
            buf = null;
            multipart.addBodyPart(body);

            for(int i = 0; i < parts.length; ++i) {
               if (parts[i] != null) {
                  multipart.addBodyPart(parts[i]);
               }
            }

            parts = null;
            msg.setContent(multipart);
            MessageContext var28 = new MessageContext(msg);
            return var28;
         } catch (RuntimeException var22) {
            this.reportError((String)var22.getMessage(), var22, code);
         } catch (Exception var23) {
            this.reportError(var23.getMessage(), var23, code);
         } finally {
            this.isWriting = false;
            if (this.size > 0) {
               this.reset();
            }

         }

         return null;
      } else {
         return null;
      }
   }

   private void verifySettings(Session session) {
      if (session != null) {
         Properties props = session.getProperties();
         Object check = props.put("verify", "");
         if (check instanceof String) {
            String value = (String)check;
            if (hasValue(value)) {
               this.verifySettings0(session, value);
            }
         } else if (check != null) {
            this.verifySettings0(session, check.getClass().toString());
         }
      }

   }

   private void verifySettings0(Session session, String verify) {
      if (!$assertionsDisabled && verify == null) {
         throw new AssertionError((String)null);
      } else if (!"local".equals(verify) && !"remote".equals(verify)) {
         this.reportError((String)"Verify must be 'local' or 'remote'.", new IllegalArgumentException(verify), 4);
      } else {
         String msg = "Local address is " + InternetAddress.getLocalAddress(session) + '.';

         try {
            Charset.forName(this.getEncodingName());
         } catch (RuntimeException var32) {
            IOException UEE = new UnsupportedEncodingException(var32.toString());
            UEE.initCause(var32);
            this.reportError((String)msg, UEE, 5);
         }

         MimeMessage abort = new MimeMessage(session);
         synchronized(this) {
            this.appendSubject(abort, this.head(this.subjectFormatter));
            this.appendSubject(abort, this.tail(this.subjectFormatter, ""));
         }

         this.setIncompleteCopy(abort);
         this.envelopeFor(new MessageContext(abort), true);

         try {
            abort.saveChanges();
         } catch (MessagingException var30) {
            this.reportError((String)msg, var30, 5);
         }

         try {
            Address[] all = abort.getAllRecipients();
            if (all == null) {
               all = new InternetAddress[0];
            }

            Transport t;
            MessagingException closed;
            try {
               Address[] any = ((Object[])all).length != 0 ? all : abort.getFrom();
               if (any == null || ((Object[])any).length == 0) {
                  closed = new MessagingException("No recipient or from address.");
                  this.reportError((String)msg, closed, 4);
                  throw closed;
               }

               t = session.getTransport((Address)((Object[])any)[0]);
               session.getProperty("mail.transport.protocol");
            } catch (MessagingException var36) {
               try {
                  t = session.getTransport();
               } catch (MessagingException var29) {
                  throw attach(var36, var29);
               }
            }

            String host = null;
            if ("remote".equals(verify)) {
               closed = null;
               t.connect();

               try {
                  try {
                     if (t instanceof SMTPTransport) {
                        host = ((SMTPTransport)t).getLocalHost();
                     }

                     t.sendMessage(abort, (Address[])all);
                  } finally {
                     try {
                        t.close();
                     } catch (MessagingException var28) {
                        closed = var28;
                     }

                  }

                  this.reportUnexpectedSend(abort, verify, (Exception)null);
               } catch (SendFailedException var34) {
                  Address[] recip = var34.getInvalidAddresses();
                  if (recip != null && recip.length != 0) {
                     this.fixUpContent(abort, verify, var34);
                     this.reportError((Message)abort, var34, 4);
                  }

                  recip = var34.getValidSentAddresses();
                  if (recip != null && recip.length != 0) {
                     this.reportUnexpectedSend(abort, verify, var34);
                  }
               } catch (MessagingException var35) {
                  if (!this.isMissingContent(abort, var35)) {
                     this.fixUpContent(abort, verify, var35);
                     this.reportError((Message)abort, var35, 4);
                  }
               }

               if (closed != null) {
                  this.fixUpContent(abort, verify, closed);
                  this.reportError((Message)abort, closed, 3);
               }
            } else {
               String protocol = t.getURLName().getProtocol();
               session.getProperty("mail.host");
               session.getProperty("mail.user");
               session.getProperty("mail." + protocol + ".host");
               session.getProperty("mail." + protocol + ".port");
               session.getProperty("mail." + protocol + ".user");
               if (t instanceof SMTPTransport) {
                  host = ((SMTPTransport)t).getLocalHost();
               } else {
                  host = session.getProperty("mail." + protocol + ".localhost");
                  if (isEmpty(host)) {
                     host = session.getProperty("mail." + protocol + ".localaddress");
                  }
               }
            }

            MessagingException ME;
            try {
               if (isEmpty(host)) {
                  if (InetAddress.getLocalHost().getCanonicalHostName().length() == 0) {
                     throw new UnknownHostException();
                  }
               } else if (InetAddress.getByName(host).getCanonicalHostName().length() == 0) {
                  throw new UnknownHostException(host);
               }
            } catch (IOException var27) {
               ME = new MessagingException(msg, var27);
               this.fixUpContent(abort, verify, ME);
               this.reportError((Message)abort, ME, 4);
            }

            try {
               MimeMultipart multipart = new MimeMultipart();
               MimeBodyPart body = new MimeBodyPart();
               body.setDisposition("inline");
               body.setDescription(verify);
               this.setAcceptLang(body);
               this.setContent(body, "", "text/plain");
               multipart.addBodyPart(body);
               abort.setContent(multipart);
               abort.saveChanges();
               abort.writeTo(new ByteArrayOutputStream(1024));
            } catch (IOException var26) {
               ME = new MessagingException(msg, var26);
               this.fixUpContent(abort, verify, ME);
               this.reportError((Message)abort, ME, 5);
            }

            if (((Object[])all).length == 0) {
               throw new MessagingException("No recipient addresses.");
            }

            verifyAddresses((Address[])all);
            Address[] from = abort.getFrom();
            Address sender = abort.getSender();
            if (sender instanceof InternetAddress) {
               ((InternetAddress)sender).validate();
            }

            if (abort.getHeader("From", ",") != null && from.length != 0) {
               verifyAddresses(from);

               for(int i = 0; i < from.length; ++i) {
                  if (from[i].equals(sender)) {
                     MessagingException ME = new MessagingException("Sender address '" + sender + "' equals from address.");
                     throw new MessagingException(msg, ME);
                  }
               }
            } else if (sender == null) {
               MessagingException ME = new MessagingException("No from or sender address.");
               throw new MessagingException(msg, ME);
            }

            verifyAddresses(abort.getReplyTo());
         } catch (MessagingException var37) {
            this.fixUpContent(abort, verify, var37);
            this.reportError((Message)abort, var37, 4);
         } catch (RuntimeException var38) {
            this.fixUpContent(abort, verify, var38);
            this.reportError((Message)abort, var38, 4);
         }

      }
   }

   private static void verifyAddresses(Address[] all) throws AddressException {
      if (all != null) {
         for(int i = 0; i < all.length; ++i) {
            Address a = all[i];
            if (a instanceof InternetAddress) {
               ((InternetAddress)a).validate();
            }
         }
      }

   }

   private void reportUnexpectedSend(MimeMessage msg, String verify, Exception cause) {
      MessagingException write = new MessagingException("An empty message was sent.", cause);
      this.fixUpContent(msg, verify, write);
      this.reportError((Message)msg, write, 4);
   }

   private void fixUpContent(MimeMessage msg, String verify, Throwable t) {
      try {
         MimeBodyPart body;
         String subjectType;
         String msgDesc;
         synchronized(this) {
            body = this.createBodyPart();
            msgDesc = this.descriptionFrom(this.comparator, this.pushLevel, this.pushFilter);
            subjectType = this.getClassId(this.subjectFormatter);
         }

         body.setDescription("Formatted using " + (t == null ? Throwable.class.getName() : t.getClass().getName()) + ", filtered with " + verify + ", and named by " + subjectType + '.');
         this.setContent(body, this.toMsgString(t), "text/plain");
         MimeMultipart multipart = new MimeMultipart();
         multipart.addBodyPart(body);
         msg.setContent(multipart);
         msg.setDescription(msgDesc);
         this.setAcceptLang(msg);
         msg.saveChanges();
      } catch (MessagingException var10) {
         this.reportError((String)"Unable to create body.", var10, 4);
      } catch (RuntimeException var11) {
         this.reportError((String)"Unable to create body.", var11, 4);
      }

   }

   private Session fixUpSession() {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         Session settings;
         if (this.mailProps.getProperty("verify") != null) {
            settings = this.initSession();
            if (!$assertionsDisabled && settings != this.session) {
               throw new AssertionError();
            }
         } else {
            this.session = null;
            settings = null;
         }

         return settings;
      }
   }

   private Session initSession() {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         String p = this.getClass().getName();
         LogManagerProperties proxy = new LogManagerProperties(this.mailProps, p);
         this.session = Session.getInstance(proxy, this.auth);
         return this.session;
      }
   }

   private void envelopeFor(MessageContext ctx, boolean priority) {
      Message msg = ctx.getMessage();
      Properties proxyProps = ctx.getSession().getProperties();
      this.setAcceptLang(msg);
      this.setFrom(msg, proxyProps);
      this.setRecipient(msg, proxyProps, "mail.to", Message.RecipientType.TO);
      this.setRecipient(msg, proxyProps, "mail.cc", Message.RecipientType.CC);
      this.setRecipient(msg, proxyProps, "mail.bcc", Message.RecipientType.BCC);
      this.setReplyTo(msg, proxyProps);
      this.setSender(msg, proxyProps);
      this.setMailer(msg);
      this.setAutoSubmitted(msg);
      if (priority) {
         this.setPriority(msg);
      }

      try {
         msg.setSentDate(new Date());
      } catch (MessagingException var6) {
         this.reportError((String)var6.getMessage(), var6, 5);
      }

   }

   private MimeBodyPart createBodyPart() throws MessagingException {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         MimeBodyPart part = new MimeBodyPart();
         part.setDisposition("inline");
         part.setDescription(this.descriptionFrom(this.getFormatter(), this.getFilter(), this.subjectFormatter));
         this.setAcceptLang(part);
         return part;
      }
   }

   private MimeBodyPart createBodyPart(int index) throws MessagingException {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         MimeBodyPart part = new MimeBodyPart();
         part.setDisposition("attachment");
         part.setDescription(this.descriptionFrom(this.attachmentFormatters[index], this.attachmentFilters[index], this.attachmentNames[index]));
         this.setAcceptLang(part);
         return part;
      }
   }

   private String descriptionFrom(Comparator c, Level l, Filter f) {
      return "Sorted using " + (c == null ? "no comparator" : c.getClass().getName()) + ", pushed when " + l.getName() + ", and " + (f == null ? "no push filter" : f.getClass().getName()) + '.';
   }

   private String descriptionFrom(Formatter f, Filter filter, Formatter name) {
      return "Formatted using " + this.getClassId(f) + ", filtered with " + (filter == null ? "no filter" : filter.getClass().getName()) + ", and named by " + this.getClassId(name) + '.';
   }

   private String getClassId(Formatter f) {
      return f instanceof TailNameFormatter ? String.class.getName() : f.getClass().getName();
   }

   private String toString(Formatter f) {
      String name = f.toString();
      return !isEmpty(name) ? name : this.getClassId(f);
   }

   private void appendFileName(Part part, String chunk) {
      if (chunk != null) {
         if (chunk.length() > 0) {
            this.appendFileName0(part, chunk);
         }
      } else {
         this.reportNullError(5);
      }

   }

   private void appendFileName0(Part part, String chunk) {
      try {
         String old = part.getFileName();
         part.setFileName(old != null ? old.concat(chunk) : chunk);
      } catch (MessagingException var4) {
         this.reportError((String)var4.getMessage(), var4, 5);
      }

   }

   private void appendSubject(Message msg, String chunk) {
      if (chunk != null) {
         if (chunk.length() > 0) {
            this.appendSubject0(msg, chunk);
         }
      } else {
         this.reportNullError(5);
      }

   }

   private void appendSubject0(Message msg, String chunk) {
      try {
         String encoding = this.getEncodingName();
         String old = msg.getSubject();
         if (!$assertionsDisabled && !(msg instanceof MimeMessage)) {
            throw new AssertionError();
         }

         ((MimeMessage)msg).setSubject(old != null ? old.concat(chunk) : chunk, MimeUtility.mimeCharset(encoding));
      } catch (MessagingException var5) {
         this.reportError((String)var5.getMessage(), var5, 5);
      }

   }

   private Locale localeFor(LogRecord r) {
      ResourceBundle rb = r.getResourceBundle();
      Locale l;
      if (rb != null) {
         l = rb.getLocale();
         if (l == null || isEmpty(l.getLanguage())) {
            l = Locale.getDefault();
         }
      } else {
         l = null;
      }

      return l;
   }

   private void appendContentLang(MimePart p, Locale l) {
      try {
         String lang = LogManagerProperties.toLanguageTag(l);
         if (lang.length() != 0) {
            String header = p.getHeader("Content-Language", (String)null);
            if (isEmpty(header)) {
               p.setHeader("Content-Language", lang);
            } else if (!header.equalsIgnoreCase(lang)) {
               lang = ",".concat(lang);
               int idx = 0;

               while((idx = header.indexOf(lang, idx)) > -1) {
                  idx += lang.length();
                  if (idx == header.length() || header.charAt(idx) == ',') {
                     break;
                  }
               }

               if (idx < 0) {
                  int len = header.lastIndexOf("\r\n\t");
                  if (len < 0) {
                     len = 20 + header.length();
                  } else {
                     len = header.length() - len + 8;
                  }

                  if (len + lang.length() > 76) {
                     header = header.concat("\r\n\t".concat(lang));
                  } else {
                     header = header.concat(lang);
                  }

                  p.setHeader("Content-Language", header);
               }
            }
         }
      } catch (MessagingException var7) {
         this.reportError((String)var7.getMessage(), var7, 5);
      }

   }

   private void setAcceptLang(Part p) {
      try {
         String lang = LogManagerProperties.toLanguageTag(Locale.getDefault());
         if (lang.length() != 0) {
            p.setHeader("Accept-Language", lang);
         }
      } catch (MessagingException var3) {
         this.reportError((String)var3.getMessage(), var3, 5);
      }

   }

   private void reportFilterError(LogRecord record) {
      if (!$assertionsDisabled && !Thread.holdsLock(this)) {
         throw new AssertionError();
      } else {
         SimpleFormatter f = new SimpleFormatter();
         String msg = "Log record " + record.getSequenceNumber() + " was filtered from all message parts.  " + this.head(f) + this.format(f, record) + this.tail(f, "");
         String txt = this.getFilter() + ", " + Arrays.asList(this.readOnlyAttachmentFilters());
         this.reportError((String)msg, new IllegalArgumentException(txt), 5);
      }
   }

   private void reportNullError(int code) {
      this.reportError((String)"null", new NullPointerException(), code);
   }

   private String head(Formatter f) {
      try {
         return f.getHead(this);
      } catch (RuntimeException var3) {
         this.reportError((String)var3.getMessage(), var3, 5);
         return "";
      }
   }

   private String format(Formatter f, LogRecord r) {
      try {
         return f.format(r);
      } catch (RuntimeException var4) {
         this.reportError((String)var4.getMessage(), var4, 5);
         return "";
      }
   }

   private String tail(Formatter f, String def) {
      try {
         return f.getTail(this);
      } catch (RuntimeException var4) {
         this.reportError((String)var4.getMessage(), var4, 5);
         return def;
      }
   }

   private void setMailer(Message msg) {
      try {
         Class mail = MailHandler.class;
         Class k = this.getClass();
         String value;
         if (k == mail) {
            value = mail.getName();
         } else {
            try {
               value = MimeUtility.encodeText(k.getName());
            } catch (UnsupportedEncodingException var6) {
               this.reportError((String)var6.getMessage(), var6, 5);
               value = k.getName().replaceAll("[^\\x00-\\x7F]", "\u001a");
            }

            value = MimeUtility.fold(10, mail.getName() + " using the " + value + " extension.");
         }

         msg.setHeader("X-Mailer", value);
      } catch (MessagingException var7) {
         this.reportError((String)var7.getMessage(), var7, 5);
      }

   }

   private void setPriority(Message msg) {
      try {
         msg.setHeader("Importance", "High");
         msg.setHeader("Priority", "urgent");
         msg.setHeader("X-Priority", "2");
      } catch (MessagingException var3) {
         this.reportError((String)var3.getMessage(), var3, 5);
      }

   }

   private void setIncompleteCopy(Message msg) {
      try {
         msg.setHeader("Incomplete-Copy", "");
      } catch (MessagingException var3) {
         this.reportError((String)var3.getMessage(), var3, 5);
      }

   }

   private void setAutoSubmitted(Message msg) {
      try {
         msg.setHeader("auto-submitted", "auto-generated");
      } catch (MessagingException var3) {
         this.reportError((String)var3.getMessage(), var3, 5);
      }

   }

   private void setFrom(Message msg, Properties props) {
      String from = props.getProperty("mail.from");
      if (from != null && from.length() > 0) {
         try {
            Address[] address = InternetAddress.parse(from, false);
            if (address != null && address.length != 0) {
               if (address.length == 1) {
                  msg.setFrom(address[0]);
               } else {
                  msg.addFrom(address);
               }
            } else {
               this.setDefaultFrom(msg);
            }
         } catch (MessagingException var5) {
            this.reportError((String)var5.getMessage(), var5, 5);
            this.setDefaultFrom(msg);
         }
      } else {
         this.setDefaultFrom(msg);
      }

   }

   private void setDefaultFrom(Message msg) {
      try {
         msg.setFrom();
      } catch (MessagingException var3) {
         this.reportError((String)var3.getMessage(), var3, 5);
      }

   }

   private void setReplyTo(Message msg, Properties props) {
      String reply = props.getProperty("mail.reply.to");
      if (reply != null && reply.length() > 0) {
         try {
            Address[] address = InternetAddress.parse(reply, false);
            if (address != null && address.length > 0) {
               msg.setReplyTo(address);
            }
         } catch (MessagingException var5) {
            this.reportError((String)var5.getMessage(), var5, 5);
         }
      }

   }

   private void setSender(Message msg, Properties props) {
      if (!$assertionsDisabled && !(msg instanceof MimeMessage)) {
         throw new AssertionError(msg);
      } else {
         String sender = props.getProperty("mail.sender");
         if (sender != null && sender.length() > 0) {
            try {
               InternetAddress[] address = InternetAddress.parse(sender, false);
               if (address != null && address.length > 0) {
                  ((MimeMessage)msg).setSender(address[0]);
                  if (address.length > 1) {
                     this.reportError((String)"Ignoring other senders.", tooManyAddresses(address, 1), 5);
                  }
               }
            } catch (MessagingException var5) {
               this.reportError((String)var5.getMessage(), var5, 5);
            }
         }

      }
   }

   private static AddressException tooManyAddresses(Address[] address, int offset) {
      String msg = Arrays.asList(address).subList(offset, address.length).toString();
      return new AddressException(msg);
   }

   private void setRecipient(Message msg, Properties props, String key, Message.RecipientType type) {
      String value = props.getProperty(key);
      if (value != null && value.length() > 0) {
         try {
            Address[] address = InternetAddress.parse(value, false);
            if (address != null && address.length > 0) {
               msg.setRecipients(type, address);
            }
         } catch (MessagingException var7) {
            this.reportError((String)var7.getMessage(), var7, 5);
         }
      }

   }

   private String toRawString(Message msg) throws MessagingException, IOException {
      if (msg != null) {
         int nbytes = Math.max(msg.getSize() + 1024, 1024);
         ByteArrayOutputStream out = new ByteArrayOutputStream(nbytes);
         msg.writeTo(out);
         return out.toString("US-ASCII");
      } else {
         return null;
      }
   }

   private String toMsgString(Throwable t) {
      if (t == null) {
         return "null";
      } else {
         String encoding = this.getEncodingName();

         try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, encoding));
            pw.println(t.getMessage());
            t.printStackTrace(pw);
            pw.flush();
            pw.close();
            return out.toString(encoding);
         } catch (IOException var5) {
            return t.toString() + ' ' + var5.toString();
         }
      }
   }

   private Object getAndSetContextClassLoader() {
      try {
         return AccessController.doPrivileged(GET_AND_SET_CCL);
      } catch (SecurityException var2) {
         return GET_AND_SET_CCL;
      }
   }

   private void setContextClassLoader(Object ccl) {
      if (ccl == null || ccl instanceof ClassLoader) {
         AccessController.doPrivileged(new GetAndSetContext(ccl));
      }

   }

   private static RuntimeException attachmentMismatch(String msg) {
      return new IndexOutOfBoundsException(msg);
   }

   private static RuntimeException attachmentMismatch(int expected, int found) {
      return attachmentMismatch("Attachments mismatched, expected " + expected + " but given " + found + '.');
   }

   private static MessagingException attach(MessagingException required, Exception optional) {
      if (optional != null && !required.setNextException(optional) && optional instanceof MessagingException) {
         MessagingException head = (MessagingException)optional;
         if (head.setNextException(required)) {
            return head;
         }
      }

      return required;
   }

   private static String atIndexMsg(int i) {
      return "At index: " + i + '.';
   }

   static {
      $assertionsDisabled = !MailHandler.class.desiredAssertionStatus();
      EMPTY_FILTERS = new Filter[0];
      EMPTY_FORMATTERS = new Formatter[0];
      offValue = Level.OFF.intValue();
      GET_AND_SET_CCL = new GetAndSetContext(MailHandler.class);
      MUTEX = new ThreadLocal();
      MUTEX_PUBLISH = Level.ALL;
      MUTEX_REPORT = Level.OFF;

      Method m;
      try {
         m = ThreadLocal.class.getMethod("remove", (Class[])null);
      } catch (RuntimeException var2) {
         m = null;
      } catch (Exception var3) {
         m = null;
      }

      REMOVE = m;
   }

   private static final class TailNameFormatter extends Formatter {
      private final String name;

      TailNameFormatter(String name) {
         assert name != null;

         this.name = name;
      }

      public final String format(LogRecord record) {
         return "";
      }

      public final String getTail(Handler h) {
         return this.name;
      }

      public final boolean equals(Object o) {
         return o instanceof TailNameFormatter ? this.name.equals(((TailNameFormatter)o).name) : false;
      }

      public final int hashCode() {
         return this.getClass().hashCode() + this.name.hashCode();
      }

      public final String toString() {
         return this.name;
      }
   }

   private static final class GetAndSetContext implements PrivilegedAction {
      private final Object source;

      GetAndSetContext(Object source) {
         this.source = source;
      }

      public final Object run() {
         Thread current = Thread.currentThread();
         ClassLoader ccl = current.getContextClassLoader();
         ClassLoader loader;
         if (this.source == null) {
            loader = null;
         } else if (this.source instanceof ClassLoader) {
            loader = (ClassLoader)this.source;
         } else if (this.source instanceof Class) {
            loader = ((Class)this.source).getClassLoader();
         } else {
            loader = this.source.getClass().getClassLoader();
         }

         if (ccl != loader) {
            current.setContextClassLoader(loader);
            return ccl;
         } else {
            return this;
         }
      }
   }

   private static final class DefaultAuthenticator extends Authenticator {
      private final String pass;

      DefaultAuthenticator(String pass) {
         assert pass != null;

         this.pass = pass;
      }

      protected final PasswordAuthentication getPasswordAuthentication() {
         return new PasswordAuthentication(this.getDefaultUserName(), this.pass);
      }
   }
}
