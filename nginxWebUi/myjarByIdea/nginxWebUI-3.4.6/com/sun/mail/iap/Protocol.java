package com.sun.mail.iap;

import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import javax.net.ssl.SSLSocket;

public class Protocol {
   protected String host;
   private Socket socket;
   protected boolean quote;
   protected MailLogger logger;
   protected MailLogger traceLogger;
   protected Properties props;
   protected String prefix;
   private boolean connected = false;
   private TraceInputStream traceInput;
   private volatile ResponseInputStream input;
   private TraceOutputStream traceOutput;
   private volatile DataOutputStream output;
   private int tagCounter = 0;
   private String localHostName;
   private final Vector handlers = new Vector();
   private volatile long timestamp;
   private static final byte[] CRLF = new byte[]{13, 10};

   public Protocol(String host, int port, Properties props, String prefix, boolean isSSL, MailLogger logger) throws IOException, ProtocolException {
      try {
         this.host = host;
         this.props = props;
         this.prefix = prefix;
         this.logger = logger;
         this.traceLogger = logger.getSubLogger("protocol", (String)null);
         this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
         this.quote = PropUtil.getBooleanProperty(props, "mail.debug.quote", false);
         this.initStreams();
         this.processGreeting(this.readResponse());
         this.timestamp = System.currentTimeMillis();
         this.connected = true;
      } finally {
         if (!this.connected) {
            this.disconnect();
         }

      }

   }

   private void initStreams() throws IOException {
      this.traceInput = new TraceInputStream(this.socket.getInputStream(), this.traceLogger);
      this.traceInput.setQuote(this.quote);
      this.input = new ResponseInputStream(this.traceInput);
      this.traceOutput = new TraceOutputStream(this.socket.getOutputStream(), this.traceLogger);
      this.traceOutput.setQuote(this.quote);
      this.output = new DataOutputStream(new BufferedOutputStream(this.traceOutput));
   }

   public Protocol(InputStream in, PrintStream out, boolean debug) throws IOException {
      this.host = "localhost";
      this.quote = false;
      this.logger = new MailLogger(this.getClass(), "DEBUG", debug, out);
      this.traceLogger = this.logger.getSubLogger("protocol", (String)null);
      this.traceInput = new TraceInputStream(in, this.traceLogger);
      this.traceInput.setQuote(this.quote);
      this.input = new ResponseInputStream(this.traceInput);
      this.traceOutput = new TraceOutputStream(out, this.traceLogger);
      this.traceOutput.setQuote(this.quote);
      this.output = new DataOutputStream(new BufferedOutputStream(this.traceOutput));
      this.timestamp = System.currentTimeMillis();
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public void addResponseHandler(ResponseHandler h) {
      this.handlers.addElement(h);
   }

   public void removeResponseHandler(ResponseHandler h) {
      this.handlers.removeElement(h);
   }

   public void notifyResponseHandlers(Response[] responses) {
      if (this.handlers.size() != 0) {
         for(int i = 0; i < responses.length; ++i) {
            Response r = responses[i];
            if (r != null) {
               Object[] h = this.handlers.toArray();

               for(int j = 0; j < h.length; ++j) {
                  if (h[j] != null) {
                     ((ResponseHandler)h[j]).handleResponse(r);
                  }
               }
            }
         }

      }
   }

   protected void processGreeting(Response r) throws ProtocolException {
      if (r.isBYE()) {
         throw new ConnectionException(this, r);
      }
   }

   protected ResponseInputStream getInputStream() {
      return this.input;
   }

   protected OutputStream getOutputStream() {
      return this.output;
   }

   protected synchronized boolean supportsNonSyncLiterals() {
      return false;
   }

   public Response readResponse() throws IOException, ProtocolException {
      return new Response(this);
   }

   protected ByteArray getResponseBuffer() {
      return null;
   }

   public String writeCommand(String command, Argument args) throws IOException, ProtocolException {
      String tag = "A" + Integer.toString(this.tagCounter++, 10);
      this.output.writeBytes(tag + " " + command);
      if (args != null) {
         this.output.write(32);
         args.write(this);
      }

      this.output.write(CRLF);
      this.output.flush();
      return tag;
   }

   public synchronized Response[] command(String command, Argument args) {
      this.commandStart(command);
      Vector v = new Vector();
      boolean done = false;
      String tag = null;
      Response r = null;

      try {
         tag = this.writeCommand(command, args);
      } catch (LiteralException var9) {
         v.addElement(var9.getResponse());
         done = true;
      } catch (Exception var10) {
         v.addElement(Response.byeResponse(var10));
         done = true;
      }

      Response byeResp = null;

      while(!done) {
         try {
            r = this.readResponse();
         } catch (IOException var11) {
            if (byeResp != null) {
               break;
            }

            r = Response.byeResponse(var11);
         } catch (ProtocolException var12) {
            continue;
         }

         if (r.isBYE()) {
            byeResp = r;
         } else {
            v.addElement(r);
            if (r.isTagged() && r.getTag().equals(tag)) {
               done = true;
            }
         }
      }

      if (byeResp != null) {
         v.addElement(byeResp);
      }

      Response[] responses = new Response[v.size()];
      v.copyInto(responses);
      this.timestamp = System.currentTimeMillis();
      this.commandEnd();
      return responses;
   }

   public void handleResult(Response response) throws ProtocolException {
      if (!response.isOK()) {
         if (response.isNO()) {
            throw new CommandFailedException(response);
         } else if (response.isBAD()) {
            throw new BadCommandException(response);
         } else if (response.isBYE()) {
            this.disconnect();
            throw new ConnectionException(this, response);
         }
      }
   }

   public void simpleCommand(String cmd, Argument args) throws ProtocolException {
      Response[] r = this.command(cmd, args);
      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
   }

   public synchronized void startTLS(String cmd) throws IOException, ProtocolException {
      if (!(this.socket instanceof SSLSocket)) {
         this.simpleCommand(cmd, (Argument)null);
         this.socket = SocketFetcher.startTLS(this.socket, this.host, this.props, this.prefix);
         this.initStreams();
      }
   }

   public boolean isSSL() {
      return this.socket instanceof SSLSocket;
   }

   protected synchronized void disconnect() {
      if (this.socket != null) {
         try {
            this.socket.close();
         } catch (IOException var2) {
         }

         this.socket = null;
      }

   }

   protected synchronized String getLocalHost() {
      if (this.localHostName == null || this.localHostName.length() <= 0) {
         this.localHostName = this.props.getProperty(this.prefix + ".localhost");
      }

      if (this.localHostName == null || this.localHostName.length() <= 0) {
         this.localHostName = this.props.getProperty(this.prefix + ".localaddress");
      }

      InetAddress localHost;
      try {
         if (this.localHostName == null || this.localHostName.length() <= 0) {
            localHost = InetAddress.getLocalHost();
            this.localHostName = localHost.getCanonicalHostName();
            if (this.localHostName == null) {
               this.localHostName = "[" + localHost.getHostAddress() + "]";
            }
         }
      } catch (UnknownHostException var2) {
      }

      if ((this.localHostName == null || this.localHostName.length() <= 0) && this.socket != null && this.socket.isBound()) {
         localHost = this.socket.getLocalAddress();
         this.localHostName = localHost.getCanonicalHostName();
         if (this.localHostName == null) {
            this.localHostName = "[" + localHost.getHostAddress() + "]";
         }
      }

      return this.localHostName;
   }

   protected boolean isTracing() {
      return this.traceLogger.isLoggable(Level.FINEST);
   }

   protected void suspendTracing() {
      if (this.traceLogger.isLoggable(Level.FINEST)) {
         this.traceInput.setTrace(false);
         this.traceOutput.setTrace(false);
      }

   }

   protected void resumeTracing() {
      if (this.traceLogger.isLoggable(Level.FINEST)) {
         this.traceInput.setTrace(true);
         this.traceOutput.setTrace(true);
      }

   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.disconnect();
   }

   private void commandStart(String command) {
   }

   private void commandEnd() {
   }
}
