package com.sun.mail.pop3;

import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.SharedByteArrayOutputStream;
import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.net.ssl.SSLSocket;

class Protocol {
   private Socket socket;
   private String host;
   private Properties props;
   private String prefix;
   private DataInputStream input;
   private PrintWriter output;
   private TraceInputStream traceInput;
   private TraceOutputStream traceOutput;
   private MailLogger logger;
   private MailLogger traceLogger;
   private String apopChallenge = null;
   private Map capabilities = null;
   private boolean pipelining;
   private boolean noauthdebug = true;
   private boolean traceSuspended;
   private static final int POP3_PORT = 110;
   private static final String CRLF = "\r\n";
   private static final int SLOP = 128;
   private static char[] digits;
   // $FF: synthetic field
   static final boolean $assertionsDisabled;

   Protocol(String host, int port, MailLogger logger, Properties props, String prefix, boolean isSSL) throws IOException {
      this.host = host;
      this.props = props;
      this.prefix = prefix;
      this.logger = logger;
      this.traceLogger = logger.getSubLogger("protocol", (String)null);
      this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
      boolean enableAPOP = this.getBoolProp(props, prefix + ".apop.enable");
      boolean disableCapa = this.getBoolProp(props, prefix + ".disablecapa");

      Response r;
      try {
         if (port == -1) {
            port = 110;
         }

         if (logger.isLoggable(Level.FINE)) {
            logger.fine("connecting to host \"" + host + "\", port " + port + ", isSSL " + isSSL);
         }

         this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
         this.initStreams();
         r = this.simpleCommand((String)null);
      } catch (IOException var23) {
         try {
            this.socket.close();
         } finally {
            throw var23;
         }

         throw var23;
      }

      if (!r.ok) {
         try {
            this.socket.close();
         } finally {
            throw new IOException("Connect failed");
         }

         throw new IOException("Connect failed");
      } else {
         if (enableAPOP) {
            int challStart = r.data.indexOf(60);
            int challEnd = r.data.indexOf(62, challStart);
            if (challStart != -1 && challEnd != -1) {
               this.apopChallenge = r.data.substring(challStart, challEnd + 1);
            }

            logger.log(Level.FINE, "APOP challenge: {0}", (Object)this.apopChallenge);
         }

         if (!disableCapa) {
            this.setCapabilities(this.capa());
         }

         this.pipelining = this.hasCapability("PIPELINING") || PropUtil.getBooleanProperty(props, prefix + ".pipelining", false);
         if (this.pipelining) {
            logger.config("PIPELINING enabled");
         }

      }
   }

   private final synchronized boolean getBoolProp(Properties props, String prop) {
      boolean val = PropUtil.getBooleanProperty(props, prop, false);
      if (this.logger.isLoggable(Level.CONFIG)) {
         this.logger.config(prop + ": " + val);
      }

      return val;
   }

   private void initStreams() throws IOException {
      boolean quote = PropUtil.getBooleanProperty(this.props, "mail.debug.quote", false);
      this.traceInput = new TraceInputStream(this.socket.getInputStream(), this.traceLogger);
      this.traceInput.setQuote(quote);
      this.traceOutput = new TraceOutputStream(this.socket.getOutputStream(), this.traceLogger);
      this.traceOutput.setQuote(quote);
      this.input = new DataInputStream(new BufferedInputStream(this.traceInput));
      this.output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.traceOutput, "iso-8859-1")));
   }

   protected void finalize() throws Throwable {
      super.finalize();
      if (this.socket != null) {
         this.quit();
      }

   }

   synchronized void setCapabilities(InputStream in) {
      if (in == null) {
         this.capabilities = null;
      } else {
         this.capabilities = new HashMap(10);
         BufferedReader r = null;

         try {
            r = new BufferedReader(new InputStreamReader(in, "us-ascii"));
         } catch (UnsupportedEncodingException var18) {
            if (!$assertionsDisabled) {
               throw new AssertionError();
            }
         }

         try {
            String s;
            String cap;
            try {
               for(; (s = r.readLine()) != null; this.capabilities.put(cap.toUpperCase(Locale.ENGLISH), s)) {
                  cap = s;
                  int i = s.indexOf(32);
                  if (i > 0) {
                     cap = s.substring(0, i);
                  }
               }
            } catch (IOException var16) {
            }
         } finally {
            try {
               in.close();
            } catch (IOException var15) {
            }

         }

      }
   }

   synchronized boolean hasCapability(String c) {
      return this.capabilities != null && this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH));
   }

   synchronized Map getCapabilities() {
      return this.capabilities;
   }

   synchronized String login(String user, String password) throws IOException {
      boolean batch = this.pipelining && this.socket instanceof SSLSocket;

      String cmd;
      try {
         if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("authentication command trace suppressed");
            this.suspendTracing();
         }

         String dpw = null;
         if (this.apopChallenge != null) {
            dpw = this.getDigest(password);
         }

         Response r;
         if (this.apopChallenge != null && dpw != null) {
            r = this.simpleCommand("APOP " + user + " " + dpw);
         } else if (batch) {
            cmd = "USER " + user;
            this.batchCommandStart(cmd);
            this.issueCommand(cmd);
            cmd = "PASS " + password;
            this.batchCommandContinue(cmd);
            this.issueCommand(cmd);
            r = this.readResponse();
            if (!r.ok) {
               String err = r.data != null ? r.data : "USER command failed";
               r = this.readResponse();
               this.batchCommandEnd();
               String var8 = err;
               return var8;
            }

            r = this.readResponse();
            this.batchCommandEnd();
         } else {
            r = this.simpleCommand("USER " + user);
            if (!r.ok) {
               cmd = r.data != null ? r.data : "USER command failed";
               return cmd;
            }

            r = this.simpleCommand("PASS " + password);
         }

         if (this.noauthdebug && this.isTracing()) {
            this.logger.log(Level.FINE, "authentication command {0}", (Object)(r.ok ? "succeeded" : "failed"));
         }

         if (r.ok) {
            cmd = null;
            return cmd;
         }

         cmd = r.data != null ? r.data : "login failed";
      } finally {
         this.resumeTracing();
      }

      return cmd;
   }

   private String getDigest(String password) {
      String key = this.apopChallenge + password;

      byte[] digest;
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         digest = md.digest(key.getBytes("iso-8859-1"));
      } catch (NoSuchAlgorithmException var5) {
         return null;
      } catch (UnsupportedEncodingException var6) {
         return null;
      }

      return toHex(digest);
   }

   private static String toHex(byte[] bytes) {
      char[] result = new char[bytes.length * 2];
      int index = 0;

      for(int i = 0; index < bytes.length; ++index) {
         int temp = bytes[index] & 255;
         result[i++] = digits[temp >> 4];
         result[i++] = digits[temp & 15];
      }

      return new String(result);
   }

   synchronized boolean quit() throws IOException {
      boolean ok = false;

      try {
         Response r = this.simpleCommand("QUIT");
         ok = r.ok;
      } finally {
         try {
            this.socket.close();
         } finally {
            this.socket = null;
            this.input = null;
            this.output = null;
         }
      }

      return ok;
   }

   synchronized Status stat() throws IOException {
      Response r = this.simpleCommand("STAT");
      Status s = new Status();
      if (!r.ok) {
         throw new IOException("STAT command failed: " + r.data);
      } else {
         if (r.data != null) {
            try {
               StringTokenizer st = new StringTokenizer(r.data);
               s.total = Integer.parseInt(st.nextToken());
               s.size = Integer.parseInt(st.nextToken());
            } catch (Exception var4) {
            }
         }

         return s;
      }
   }

   synchronized int list(int msg) throws IOException {
      Response r = this.simpleCommand("LIST " + msg);
      int size = -1;
      if (r.ok && r.data != null) {
         try {
            StringTokenizer st = new StringTokenizer(r.data);
            st.nextToken();
            size = Integer.parseInt(st.nextToken());
         } catch (Exception var5) {
         }
      }

      return size;
   }

   synchronized InputStream list() throws IOException {
      Response r = this.multilineCommand("LIST", 128);
      return r.bytes;
   }

   synchronized InputStream retr(int msg, int size) throws IOException {
      boolean batch = size == 0 && this.pipelining;
      Response r;
      String cmd;
      StringTokenizer st;
      if (batch) {
         cmd = "LIST " + msg;
         this.batchCommandStart(cmd);
         this.issueCommand(cmd);
         cmd = "RETR " + msg;
         this.batchCommandContinue(cmd);
         this.issueCommand(cmd);
         r = this.readResponse();
         if (r.ok && r.data != null) {
            try {
               st = new StringTokenizer(r.data);
               st.nextToken();
               size = Integer.parseInt(st.nextToken());
               if (size <= 1073741824 && size >= 0) {
                  if (this.logger.isLoggable(Level.FINE)) {
                     this.logger.fine("pipeline message size " + size);
                  }

                  size += 128;
               } else {
                  size = 0;
               }
            } catch (Exception var9) {
            }
         }

         r = this.readResponse();
         if (r.ok) {
            r.bytes = this.readMultilineResponse(size + 128);
         }

         this.batchCommandEnd();
      } else {
         cmd = "RETR " + msg;
         this.multilineCommandStart(cmd);
         this.issueCommand(cmd);
         r = this.readResponse();
         if (!r.ok) {
            this.multilineCommandEnd();
            return null;
         }

         if (size <= 0 && r.data != null) {
            try {
               st = new StringTokenizer(r.data);
               String s = st.nextToken();
               String octets = st.nextToken();
               if (octets.equals("octets")) {
                  size = Integer.parseInt(s);
                  if (size <= 1073741824 && size >= 0) {
                     if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("guessing message size: " + size);
                     }

                     size += 128;
                  } else {
                     size = 0;
                  }
               }
            } catch (Exception var10) {
            }
         }

         r.bytes = this.readMultilineResponse(size);
         this.multilineCommandEnd();
      }

      if (r.ok && size > 0 && this.logger.isLoggable(Level.FINE)) {
         this.logger.fine("got message size " + r.bytes.available());
      }

      return r.bytes;
   }

   synchronized boolean retr(int msg, OutputStream os) throws IOException {
      String cmd = "RETR " + msg;
      this.multilineCommandStart(cmd);
      this.issueCommand(cmd);
      Response r = this.readResponse();
      if (!r.ok) {
         this.multilineCommandEnd();
         return false;
      } else {
         Throwable terr = null;
         int lastb = 10;

         int b;
         try {
            for(; (b = this.input.read()) >= 0; lastb = b) {
               if (lastb == 10 && b == 46) {
                  b = this.input.read();
                  if (b == 13) {
                     b = this.input.read();
                     break;
                  }
               }

               if (terr == null) {
                  try {
                     os.write(b);
                  } catch (IOException var11) {
                     this.logger.log(Level.FINE, "exception while streaming", (Throwable)var11);
                     terr = var11;
                  } catch (RuntimeException var12) {
                     this.logger.log(Level.FINE, "exception while streaming", (Throwable)var12);
                     terr = var12;
                  }
               }
            }
         } catch (InterruptedIOException var13) {
            try {
               this.socket.close();
            } catch (IOException var10) {
            }

            throw var13;
         }

         if (b < 0) {
            throw new EOFException("EOF on socket");
         } else {
            if (terr != null) {
               if (terr instanceof IOException) {
                  throw (IOException)terr;
               }

               if (terr instanceof RuntimeException) {
                  throw (RuntimeException)terr;
               }

               if (!$assertionsDisabled) {
                  throw new AssertionError();
               }
            }

            this.multilineCommandEnd();
            return true;
         }
      }
   }

   synchronized InputStream top(int msg, int n) throws IOException {
      Response r = this.multilineCommand("TOP " + msg + " " + n, 0);
      return r.bytes;
   }

   synchronized boolean dele(int msg) throws IOException {
      Response r = this.simpleCommand("DELE " + msg);
      return r.ok;
   }

   synchronized String uidl(int msg) throws IOException {
      Response r = this.simpleCommand("UIDL " + msg);
      if (!r.ok) {
         return null;
      } else {
         int i = r.data.indexOf(32);
         return i > 0 ? r.data.substring(i + 1) : null;
      }
   }

   synchronized boolean uidl(String[] uids) throws IOException {
      Response r = this.multilineCommand("UIDL", 15 * uids.length);
      if (!r.ok) {
         return false;
      } else {
         LineInputStream lis = new LineInputStream(r.bytes);
         String line = null;

         while((line = lis.readLine()) != null) {
            int i = line.indexOf(32);
            if (i >= 1 && i < line.length()) {
               int n = Integer.parseInt(line.substring(0, i));
               if (n > 0 && n <= uids.length) {
                  uids[n - 1] = line.substring(i + 1);
               }
            }
         }

         try {
            r.bytes.close();
         } catch (IOException var7) {
         }

         return true;
      }
   }

   synchronized boolean noop() throws IOException {
      Response r = this.simpleCommand("NOOP");
      return r.ok;
   }

   synchronized boolean rset() throws IOException {
      Response r = this.simpleCommand("RSET");
      return r.ok;
   }

   synchronized boolean stls() throws IOException {
      if (this.socket instanceof SSLSocket) {
         return true;
      } else {
         Response r = this.simpleCommand("STLS");
         if (r.ok) {
            try {
               this.socket = SocketFetcher.startTLS(this.socket, this.host, this.props, this.prefix);
               this.initStreams();
            } catch (IOException var8) {
               try {
                  this.socket.close();
               } finally {
                  this.socket = null;
                  this.input = null;
                  this.output = null;
               }

               IOException sioex = new IOException("Could not convert socket to TLS");
               sioex.initCause(var8);
               throw sioex;
            }
         }

         return r.ok;
      }
   }

   synchronized boolean isSSL() {
      return this.socket instanceof SSLSocket;
   }

   synchronized InputStream capa() throws IOException {
      Response r = this.multilineCommand("CAPA", 128);
      return !r.ok ? null : r.bytes;
   }

   private Response simpleCommand(String cmd) throws IOException {
      this.simpleCommandStart(cmd);
      this.issueCommand(cmd);
      Response r = this.readResponse();
      this.simpleCommandEnd();
      return r;
   }

   private void issueCommand(String cmd) throws IOException {
      if (this.socket == null) {
         throw new IOException("Folder is closed");
      } else {
         if (cmd != null) {
            cmd = cmd + "\r\n";
            this.output.print(cmd);
            this.output.flush();
         }

      }
   }

   private Response readResponse() throws IOException {
      String line = null;

      try {
         line = this.input.readLine();
      } catch (InterruptedIOException var6) {
         try {
            this.socket.close();
         } catch (IOException var5) {
         }

         throw new EOFException(var6.getMessage());
      } catch (SocketException var7) {
         try {
            this.socket.close();
         } catch (IOException var4) {
         }

         throw new EOFException(var7.getMessage());
      }

      if (line == null) {
         this.traceLogger.finest("<EOF>");
         throw new EOFException("EOF on socket");
      } else {
         Response r = new Response();
         if (line.startsWith("+OK")) {
            r.ok = true;
         } else {
            if (!line.startsWith("-ERR")) {
               throw new IOException("Unexpected response: " + line);
            }

            r.ok = false;
         }

         int i;
         if ((i = line.indexOf(32)) >= 0) {
            r.data = line.substring(i + 1);
         }

         return r;
      }
   }

   private Response multilineCommand(String cmd, int size) throws IOException {
      this.multilineCommandStart(cmd);
      this.issueCommand(cmd);
      Response r = this.readResponse();
      if (!r.ok) {
         this.multilineCommandEnd();
         return r;
      } else {
         r.bytes = this.readMultilineResponse(size);
         this.multilineCommandEnd();
         return r;
      }
   }

   private InputStream readMultilineResponse(int size) throws IOException {
      SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size);
      int lastb = 10;

      int b;
      try {
         while((b = this.input.read()) >= 0) {
            if (lastb == 10 && b == 46) {
               b = this.input.read();
               if (b == 13) {
                  b = this.input.read();
                  break;
               }
            }

            buf.write(b);
            lastb = b;
         }
      } catch (InterruptedIOException var8) {
         try {
            this.socket.close();
         } catch (IOException var7) {
         }

         throw var8;
      }

      if (b < 0) {
         throw new EOFException("EOF on socket");
      } else {
         return buf.toStream();
      }
   }

   protected boolean isTracing() {
      return this.traceLogger.isLoggable(Level.FINEST);
   }

   private void suspendTracing() {
      if (this.traceLogger.isLoggable(Level.FINEST)) {
         this.traceInput.setTrace(false);
         this.traceOutput.setTrace(false);
      }

   }

   private void resumeTracing() {
      if (this.traceLogger.isLoggable(Level.FINEST)) {
         this.traceInput.setTrace(true);
         this.traceOutput.setTrace(true);
      }

   }

   private void simpleCommandStart(String command) {
   }

   private void simpleCommandEnd() {
   }

   private void multilineCommandStart(String command) {
   }

   private void multilineCommandEnd() {
   }

   private void batchCommandStart(String command) {
   }

   private void batchCommandContinue(String command) {
   }

   private void batchCommandEnd() {
   }

   static {
      $assertionsDisabled = !Protocol.class.desiredAssertionStatus();
      digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   }
}
