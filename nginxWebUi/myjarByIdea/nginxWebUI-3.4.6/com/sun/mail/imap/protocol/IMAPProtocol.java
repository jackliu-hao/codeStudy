package com.sun.mail.imap.protocol;

import com.sun.mail.auth.Ntlm;
import com.sun.mail.iap.Argument;
import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.Literal;
import com.sun.mail.iap.LiteralException;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.ACL;
import com.sun.mail.imap.AppendUID;
import com.sun.mail.imap.Rights;
import com.sun.mail.imap.SortTerm;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import javax.mail.Flags;
import javax.mail.Quota;
import javax.mail.internet.MimeUtility;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;

public class IMAPProtocol extends Protocol {
   private boolean connected = false;
   private boolean rev1 = false;
   private boolean noauthdebug = true;
   private boolean authenticated;
   private Map capabilities;
   private List authmechs;
   protected SearchSequence searchSequence;
   protected String[] searchCharsets;
   private String name;
   private SaslAuthenticator saslAuthenticator;
   private ByteArray ba;
   private static final byte[] CRLF = new byte[]{13, 10};
   private static final FetchItem[] fetchItems = new FetchItem[0];
   private volatile String idleTag;
   private static final byte[] DONE = new byte[]{68, 79, 78, 69, 13, 10};

   public IMAPProtocol(String name, String host, int port, Properties props, boolean isSSL, MailLogger logger) throws IOException, ProtocolException {
      super(host, port, props, "mail." + name, isSSL, logger);

      try {
         this.name = name;
         this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
         if (this.capabilities == null) {
            this.capability();
         }

         if (this.hasCapability("IMAP4rev1")) {
            this.rev1 = true;
         }

         this.searchCharsets = new String[2];
         this.searchCharsets[0] = "UTF-8";
         this.searchCharsets[1] = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
         this.connected = true;
      } finally {
         if (!this.connected) {
            this.disconnect();
         }

      }

   }

   public FetchItem[] getFetchItems() {
      return fetchItems;
   }

   public void capability() throws ProtocolException {
      Response[] r = this.command("CAPABILITY", (Argument)null);
      if (!r[r.length - 1].isOK()) {
         throw new ProtocolException(r[r.length - 1].toString());
      } else {
         this.capabilities = new HashMap(10);
         this.authmechs = new ArrayList(5);
         int i = 0;

         for(int len = r.length; i < len; ++i) {
            if (r[i] instanceof IMAPResponse) {
               IMAPResponse ir = (IMAPResponse)r[i];
               if (ir.keyEquals("CAPABILITY")) {
                  this.parseCapabilities(ir);
               }
            }
         }

      }
   }

   protected void setCapabilities(Response r) {
      byte b;
      while((b = r.readByte()) > 0 && b != 91) {
      }

      if (b != 0) {
         String s = r.readAtom();
         if (s.equalsIgnoreCase("CAPABILITY")) {
            this.capabilities = new HashMap(10);
            this.authmechs = new ArrayList(5);
            this.parseCapabilities(r);
         }
      }
   }

   protected void parseCapabilities(Response r) {
      while(true) {
         String s;
         if ((s = r.readAtom(']')) != null) {
            if (s.length() != 0) {
               this.capabilities.put(s.toUpperCase(Locale.ENGLISH), s);
               if (!s.regionMatches(true, 0, "AUTH=", 0, 5)) {
                  continue;
               }

               this.authmechs.add(s.substring(5));
               if (this.logger.isLoggable(Level.FINE)) {
                  this.logger.fine("AUTH: " + s.substring(5));
               }
               continue;
            }

            if (r.peekByte() != 93) {
               r.skipToken();
               continue;
            }
         }

         return;
      }
   }

   protected void processGreeting(Response r) throws ProtocolException {
      super.processGreeting(r);
      if (r.isOK()) {
         this.setCapabilities(r);
      } else {
         IMAPResponse ir = (IMAPResponse)r;
         if (ir.keyEquals("PREAUTH")) {
            this.authenticated = true;
            this.setCapabilities(r);
         } else {
            throw new ConnectionException(this, r);
         }
      }
   }

   public boolean isAuthenticated() {
      return this.authenticated;
   }

   public boolean isREV1() {
      return this.rev1;
   }

   protected boolean supportsNonSyncLiterals() {
      return this.hasCapability("LITERAL+");
   }

   public Response readResponse() throws IOException, ProtocolException {
      IMAPResponse r = new IMAPResponse(this);
      if (((IMAPResponse)r).keyEquals("FETCH")) {
         r = new FetchResponse((IMAPResponse)r, this.getFetchItems());
      }

      return (Response)r;
   }

   public boolean hasCapability(String c) {
      if (c.endsWith("*")) {
         c = c.substring(0, c.length() - 1).toUpperCase(Locale.ENGLISH);
         Iterator it = this.capabilities.keySet().iterator();

         do {
            if (!it.hasNext()) {
               return false;
            }
         } while(!((String)it.next()).startsWith(c));

         return true;
      } else {
         return this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH));
      }
   }

   public Map getCapabilities() {
      return this.capabilities;
   }

   public void disconnect() {
      super.disconnect();
      this.authenticated = false;
   }

   public void noop() throws ProtocolException {
      this.logger.fine("IMAPProtocol noop");
      this.simpleCommand("NOOP", (Argument)null);
   }

   public void logout() throws ProtocolException {
      try {
         Response[] r = this.command("LOGOUT", (Argument)null);
         this.authenticated = false;
         this.notifyResponseHandlers(r);
      } finally {
         this.disconnect();
      }

   }

   public void login(String u, String p) throws ProtocolException {
      Argument args = new Argument();
      args.writeString(u);
      args.writeString(p);
      Response[] r = null;

      try {
         if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("LOGIN command trace suppressed");
            this.suspendTracing();
         }

         r = this.command("LOGIN", args);
      } finally {
         this.resumeTracing();
      }

      this.notifyResponseHandlers(r);
      if (this.noauthdebug && this.isTracing()) {
         this.logger.fine("LOGIN command result: " + r[r.length - 1]);
      }

      this.handleResult(r[r.length - 1]);
      this.setCapabilities(r[r.length - 1]);
      this.authenticated = true;
   }

   public synchronized void authlogin(String u, String p) throws ProtocolException {
      Vector v = new Vector();
      String tag = null;
      Response r = null;
      boolean done = false;

      try {
         if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE LOGIN command trace suppressed");
            this.suspendTracing();
         }

         try {
            tag = this.writeCommand("AUTHENTICATE LOGIN", (Argument)null);
         } catch (Exception var16) {
            r = Response.byeResponse(var16);
            done = true;
         }

         OutputStream os = this.getOutputStream();
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
         boolean first = true;

         while(!done) {
            try {
               r = this.readResponse();
               if (r.isContinuation()) {
                  String s;
                  if (first) {
                     s = u;
                     first = false;
                  } else {
                     s = p;
                  }

                  b64os.write(ASCIIUtility.getBytes(s));
                  b64os.flush();
                  bos.write(CRLF);
                  os.write(bos.toByteArray());
                  os.flush();
                  bos.reset();
               } else if (r.isTagged() && r.getTag().equals(tag)) {
                  done = true;
               } else if (r.isBYE()) {
                  done = true;
               } else {
                  v.addElement(r);
               }
            } catch (Exception var17) {
               r = Response.byeResponse(var17);
               done = true;
            }
         }
      } finally {
         this.resumeTracing();
      }

      Response[] responses = new Response[v.size()];
      v.copyInto(responses);
      this.notifyResponseHandlers(responses);
      if (this.noauthdebug && this.isTracing()) {
         this.logger.fine("AUTHENTICATE LOGIN command result: " + r);
      }

      this.handleResult(r);
      this.setCapabilities(r);
      this.authenticated = true;
   }

   public synchronized void authplain(String authzid, String u, String p) throws ProtocolException {
      Vector v = new Vector();
      String tag = null;
      Response r = null;
      boolean done = false;

      try {
         if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE PLAIN command trace suppressed");
            this.suspendTracing();
         }

         try {
            tag = this.writeCommand("AUTHENTICATE PLAIN", (Argument)null);
         } catch (Exception var17) {
            r = Response.byeResponse(var17);
            done = true;
         }

         OutputStream os = this.getOutputStream();
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);

         while(!done) {
            try {
               r = this.readResponse();
               if (r.isContinuation()) {
                  String nullByte = "\u0000";
                  String s = (authzid == null ? "" : authzid) + "\u0000" + u + "\u0000" + p;
                  b64os.write(ASCIIUtility.getBytes(s));
                  b64os.flush();
                  bos.write(CRLF);
                  os.write(bos.toByteArray());
                  os.flush();
                  bos.reset();
               } else if (r.isTagged() && r.getTag().equals(tag)) {
                  done = true;
               } else if (r.isBYE()) {
                  done = true;
               } else {
                  v.addElement(r);
               }
            } catch (Exception var18) {
               r = Response.byeResponse(var18);
               done = true;
            }
         }
      } finally {
         this.resumeTracing();
      }

      Response[] responses = new Response[v.size()];
      v.copyInto(responses);
      this.notifyResponseHandlers(responses);
      if (this.noauthdebug && this.isTracing()) {
         this.logger.fine("AUTHENTICATE PLAIN command result: " + r);
      }

      this.handleResult(r);
      this.setCapabilities(r);
      this.authenticated = true;
   }

   public synchronized void authntlm(String authzid, String u, String p) throws ProtocolException {
      Vector v = new Vector();
      String tag = null;
      Response r = null;
      boolean done = false;
      String type1Msg = null;
      int flags = PropUtil.getIntProperty(this.props, "mail." + this.name + ".auth.ntlm.flags", 0);
      String domain = this.props.getProperty("mail." + this.name + ".auth.ntlm.domain", "");
      Ntlm ntlm = new Ntlm(domain, this.getLocalHost(), u, p, this.logger);

      try {
         if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE NTLM command trace suppressed");
            this.suspendTracing();
         }

         try {
            tag = this.writeCommand("AUTHENTICATE NTLM", (Argument)null);
         } catch (Exception var19) {
            r = Response.byeResponse(var19);
            done = true;
         }

         OutputStream os = this.getOutputStream();
         boolean first = true;

         while(!done) {
            try {
               r = this.readResponse();
               if (r.isContinuation()) {
                  String s;
                  if (first) {
                     s = ntlm.generateType1Msg(flags);
                     first = false;
                  } else {
                     s = ntlm.generateType3Msg(r.getRest());
                  }

                  os.write(ASCIIUtility.getBytes(s));
                  os.write(CRLF);
                  os.flush();
               } else if (r.isTagged() && r.getTag().equals(tag)) {
                  done = true;
               } else if (r.isBYE()) {
                  done = true;
               } else {
                  v.addElement(r);
               }
            } catch (Exception var20) {
               r = Response.byeResponse(var20);
               done = true;
            }
         }
      } finally {
         this.resumeTracing();
      }

      Response[] responses = new Response[v.size()];
      v.copyInto(responses);
      this.notifyResponseHandlers(responses);
      if (this.noauthdebug && this.isTracing()) {
         this.logger.fine("AUTHENTICATE NTLM command result: " + r);
      }

      this.handleResult(r);
      this.setCapabilities(r);
      this.authenticated = true;
   }

   public void sasllogin(String[] allowed, String realm, String authzid, String u, String p) throws ProtocolException {
      if (this.saslAuthenticator == null) {
         try {
            Class sac = Class.forName("com.sun.mail.imap.protocol.IMAPSaslAuthenticator");
            Constructor c = sac.getConstructor(IMAPProtocol.class, String.class, Properties.class, MailLogger.class, String.class);
            this.saslAuthenticator = (SaslAuthenticator)c.newInstance(this, this.name, this.props, this.logger, this.host);
         } catch (Exception var11) {
            this.logger.log(Level.FINE, "Can't load SASL authenticator", (Throwable)var11);
            return;
         }
      }

      Object v;
      if (allowed != null && allowed.length > 0) {
         v = new ArrayList(allowed.length);

         for(int i = 0; i < allowed.length; ++i) {
            if (this.authmechs.contains(allowed[i])) {
               ((List)v).add(allowed[i]);
            }
         }
      } else {
         v = this.authmechs;
      }

      String[] mechs = (String[])((String[])((List)v).toArray(new String[((List)v).size()]));

      try {
         if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("SASL authentication command trace suppressed");
            this.suspendTracing();
         }

         if (this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p)) {
            if (this.noauthdebug && this.isTracing()) {
               this.logger.fine("SASL authentication succeeded");
            }

            this.authenticated = true;
         } else if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("SASL authentication failed");
         }
      } finally {
         this.resumeTracing();
      }

   }

   OutputStream getIMAPOutputStream() {
      return this.getOutputStream();
   }

   public void proxyauth(String u) throws ProtocolException {
      Argument args = new Argument();
      args.writeString(u);
      this.simpleCommand("PROXYAUTH", args);
   }

   public void id(String guid) throws ProtocolException {
      this.simpleCommand("ID (\"GUID\" \"" + guid + "\")", (Argument)null);
   }

   public void startTLS() throws ProtocolException {
      try {
         super.startTLS("STARTTLS");
      } catch (ProtocolException var3) {
         this.logger.log(Level.FINE, "STARTTLS ProtocolException", (Throwable)var3);
         throw var3;
      } catch (Exception var4) {
         this.logger.log(Level.FINE, "STARTTLS Exception", (Throwable)var4);
         Response[] r = new Response[]{Response.byeResponse(var4)};
         this.notifyResponseHandlers(r);
         this.disconnect();
         throw new ProtocolException("STARTTLS failure", var4);
      }
   }

   public MailboxInfo select(String mbox) throws ProtocolException {
      mbox = BASE64MailboxEncoder.encode(mbox);
      Argument args = new Argument();
      args.writeString(mbox);
      Response[] r = this.command("SELECT", args);
      MailboxInfo minfo = new MailboxInfo(r);
      this.notifyResponseHandlers(r);
      Response response = r[r.length - 1];
      if (response.isOK()) {
         if (response.toString().indexOf("READ-ONLY") != -1) {
            minfo.mode = 1;
         } else {
            minfo.mode = 2;
         }
      }

      this.handleResult(response);
      return minfo;
   }

   public MailboxInfo examine(String mbox) throws ProtocolException {
      mbox = BASE64MailboxEncoder.encode(mbox);
      Argument args = new Argument();
      args.writeString(mbox);
      Response[] r = this.command("EXAMINE", args);
      MailboxInfo minfo = new MailboxInfo(r);
      minfo.mode = 1;
      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
      return minfo;
   }

   public void unselect() throws ProtocolException {
      if (!this.hasCapability("UNSELECT")) {
         throw new BadCommandException("UNSELECT not supported");
      } else {
         this.simpleCommand("UNSELECT", (Argument)null);
      }
   }

   public Status status(String mbox, String[] items) throws ProtocolException {
      if (!this.isREV1() && !this.hasCapability("IMAP4SUNVERSION")) {
         throw new BadCommandException("STATUS not supported");
      } else {
         mbox = BASE64MailboxEncoder.encode(mbox);
         Argument args = new Argument();
         args.writeString(mbox);
         Argument itemArgs = new Argument();
         if (items == null) {
            items = Status.standardItems;
         }

         int i = 0;

         for(int len = items.length; i < len; ++i) {
            itemArgs.writeAtom(items[i]);
         }

         args.writeArgument(itemArgs);
         Response[] r = this.command("STATUS", args);
         Status status = null;
         Response response = r[r.length - 1];
         if (response.isOK()) {
            int i = 0;

            for(int len = r.length; i < len; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (ir.keyEquals("STATUS")) {
                     if (status == null) {
                        status = new Status(ir);
                     } else {
                        Status.add(status, new Status(ir));
                     }

                     r[i] = null;
                  }
               }
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         return status;
      }
   }

   public void create(String mbox) throws ProtocolException {
      mbox = BASE64MailboxEncoder.encode(mbox);
      Argument args = new Argument();
      args.writeString(mbox);
      this.simpleCommand("CREATE", args);
   }

   public void delete(String mbox) throws ProtocolException {
      mbox = BASE64MailboxEncoder.encode(mbox);
      Argument args = new Argument();
      args.writeString(mbox);
      this.simpleCommand("DELETE", args);
   }

   public void rename(String o, String n) throws ProtocolException {
      o = BASE64MailboxEncoder.encode(o);
      n = BASE64MailboxEncoder.encode(n);
      Argument args = new Argument();
      args.writeString(o);
      args.writeString(n);
      this.simpleCommand("RENAME", args);
   }

   public void subscribe(String mbox) throws ProtocolException {
      Argument args = new Argument();
      mbox = BASE64MailboxEncoder.encode(mbox);
      args.writeString(mbox);
      this.simpleCommand("SUBSCRIBE", args);
   }

   public void unsubscribe(String mbox) throws ProtocolException {
      Argument args = new Argument();
      mbox = BASE64MailboxEncoder.encode(mbox);
      args.writeString(mbox);
      this.simpleCommand("UNSUBSCRIBE", args);
   }

   public ListInfo[] list(String ref, String pattern) throws ProtocolException {
      return this.doList("LIST", ref, pattern);
   }

   public ListInfo[] lsub(String ref, String pattern) throws ProtocolException {
      return this.doList("LSUB", ref, pattern);
   }

   protected ListInfo[] doList(String cmd, String ref, String pat) throws ProtocolException {
      ref = BASE64MailboxEncoder.encode(ref);
      pat = BASE64MailboxEncoder.encode(pat);
      Argument args = new Argument();
      args.writeString(ref);
      args.writeString(pat);
      Response[] r = this.command(cmd, args);
      ListInfo[] linfo = null;
      Response response = r[r.length - 1];
      if (response.isOK()) {
         Vector v = new Vector(1);
         int i = 0;

         for(int len = r.length; i < len; ++i) {
            if (r[i] instanceof IMAPResponse) {
               IMAPResponse ir = (IMAPResponse)r[i];
               if (ir.keyEquals(cmd)) {
                  v.addElement(new ListInfo(ir));
                  r[i] = null;
               }
            }
         }

         if (v.size() > 0) {
            linfo = new ListInfo[v.size()];
            v.copyInto(linfo);
         }
      }

      this.notifyResponseHandlers(r);
      this.handleResult(response);
      return linfo;
   }

   public void append(String mbox, Flags f, Date d, Literal data) throws ProtocolException {
      this.appenduid(mbox, f, d, data, false);
   }

   public AppendUID appenduid(String mbox, Flags f, Date d, Literal data) throws ProtocolException {
      return this.appenduid(mbox, f, d, data, true);
   }

   public AppendUID appenduid(String mbox, Flags f, Date d, Literal data, boolean uid) throws ProtocolException {
      mbox = BASE64MailboxEncoder.encode(mbox);
      Argument args = new Argument();
      args.writeString(mbox);
      if (f != null) {
         if (f.contains(Flags.Flag.RECENT)) {
            f = new Flags(f);
            f.remove(Flags.Flag.RECENT);
         }

         args.writeAtom(this.createFlagList(f));
      }

      if (d != null) {
         args.writeString(INTERNALDATE.format(d));
      }

      args.writeBytes(data);
      Response[] r = this.command("APPEND", args);
      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
      return uid ? this.getAppendUID(r[r.length - 1]) : null;
   }

   private AppendUID getAppendUID(Response r) {
      if (!r.isOK()) {
         return null;
      } else {
         byte b;
         while((b = r.readByte()) > 0 && b != 91) {
         }

         if (b == 0) {
            return null;
         } else {
            String s = r.readAtom();
            if (!s.equalsIgnoreCase("APPENDUID")) {
               return null;
            } else {
               long uidvalidity = r.readLong();
               long uid = r.readLong();
               return new AppendUID(uidvalidity, uid);
            }
         }
      }
   }

   public void check() throws ProtocolException {
      this.simpleCommand("CHECK", (Argument)null);
   }

   public void close() throws ProtocolException {
      this.simpleCommand("CLOSE", (Argument)null);
   }

   public void expunge() throws ProtocolException {
      this.simpleCommand("EXPUNGE", (Argument)null);
   }

   public void uidexpunge(UIDSet[] set) throws ProtocolException {
      if (!this.hasCapability("UIDPLUS")) {
         throw new BadCommandException("UID EXPUNGE not supported");
      } else {
         this.simpleCommand("UID EXPUNGE " + UIDSet.toString(set), (Argument)null);
      }
   }

   public BODYSTRUCTURE fetchBodyStructure(int msgno) throws ProtocolException {
      Response[] r = this.fetch(msgno, "BODYSTRUCTURE");
      this.notifyResponseHandlers(r);
      Response response = r[r.length - 1];
      if (response.isOK()) {
         return (BODYSTRUCTURE)FetchResponse.getItem(r, msgno, BODYSTRUCTURE.class);
      } else if (response.isNO()) {
         return null;
      } else {
         this.handleResult(response);
         return null;
      }
   }

   public BODY peekBody(int msgno, String section) throws ProtocolException {
      return this.fetchBody(msgno, section, true);
   }

   public BODY fetchBody(int msgno, String section) throws ProtocolException {
      return this.fetchBody(msgno, section, false);
   }

   protected BODY fetchBody(int msgno, String section, boolean peek) throws ProtocolException {
      Response[] r;
      if (peek) {
         r = this.fetch(msgno, "BODY.PEEK[" + (section == null ? "]" : section + "]"));
      } else {
         r = this.fetch(msgno, "BODY[" + (section == null ? "]" : section + "]"));
      }

      this.notifyResponseHandlers(r);
      Response response = r[r.length - 1];
      if (response.isOK()) {
         return (BODY)FetchResponse.getItem(r, msgno, BODY.class);
      } else if (response.isNO()) {
         return null;
      } else {
         this.handleResult(response);
         return null;
      }
   }

   public BODY peekBody(int msgno, String section, int start, int size) throws ProtocolException {
      return this.fetchBody(msgno, section, start, size, true, (ByteArray)null);
   }

   public BODY fetchBody(int msgno, String section, int start, int size) throws ProtocolException {
      return this.fetchBody(msgno, section, start, size, false, (ByteArray)null);
   }

   public BODY peekBody(int msgno, String section, int start, int size, ByteArray ba) throws ProtocolException {
      return this.fetchBody(msgno, section, start, size, true, ba);
   }

   public BODY fetchBody(int msgno, String section, int start, int size, ByteArray ba) throws ProtocolException {
      return this.fetchBody(msgno, section, start, size, false, ba);
   }

   protected BODY fetchBody(int msgno, String section, int start, int size, boolean peek, ByteArray ba) throws ProtocolException {
      this.ba = ba;
      Response[] r = this.fetch(msgno, (peek ? "BODY.PEEK[" : "BODY[") + (section == null ? "]<" : section + "]<") + start + "." + size + ">");
      this.notifyResponseHandlers(r);
      Response response = r[r.length - 1];
      if (response.isOK()) {
         return (BODY)FetchResponse.getItem(r, msgno, BODY.class);
      } else if (response.isNO()) {
         return null;
      } else {
         this.handleResult(response);
         return null;
      }
   }

   protected ByteArray getResponseBuffer() {
      ByteArray ret = this.ba;
      this.ba = null;
      return ret;
   }

   public RFC822DATA fetchRFC822(int msgno, String what) throws ProtocolException {
      Response[] r = this.fetch(msgno, what == null ? "RFC822" : "RFC822." + what);
      this.notifyResponseHandlers(r);
      Response response = r[r.length - 1];
      if (response.isOK()) {
         return (RFC822DATA)FetchResponse.getItem(r, msgno, RFC822DATA.class);
      } else if (response.isNO()) {
         return null;
      } else {
         this.handleResult(response);
         return null;
      }
   }

   public Flags fetchFlags(int msgno) throws ProtocolException {
      Flags flags = null;
      Response[] r = this.fetch(msgno, "FLAGS");
      int i = 0;

      for(int len = r.length; i < len; ++i) {
         if (r[i] != null && r[i] instanceof FetchResponse && ((FetchResponse)r[i]).getNumber() == msgno) {
            FetchResponse fr = (FetchResponse)r[i];
            if ((flags = (Flags)fr.getItem(Flags.class)) != null) {
               r[i] = null;
               break;
            }
         }
      }

      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
      return flags;
   }

   public UID fetchUID(int msgno) throws ProtocolException {
      Response[] r = this.fetch(msgno, "UID");
      this.notifyResponseHandlers(r);
      Response response = r[r.length - 1];
      if (response.isOK()) {
         return (UID)FetchResponse.getItem(r, msgno, UID.class);
      } else if (response.isNO()) {
         return null;
      } else {
         this.handleResult(response);
         return null;
      }
   }

   public UID fetchSequenceNumber(long uid) throws ProtocolException {
      UID u = null;
      Response[] r = this.fetch(String.valueOf(uid), "UID", true);
      int i = 0;

      for(int len = r.length; i < len; ++i) {
         if (r[i] != null && r[i] instanceof FetchResponse) {
            FetchResponse fr = (FetchResponse)r[i];
            if ((u = (UID)fr.getItem(UID.class)) != null) {
               if (u.uid == uid) {
                  break;
               }

               u = null;
            }
         }
      }

      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
      return u;
   }

   public UID[] fetchSequenceNumbers(long start, long end) throws ProtocolException {
      Response[] r = this.fetch(start + ":" + (end == -1L ? "*" : String.valueOf(end)), "UID", true);
      Vector v = new Vector();
      int i = 0;

      for(int len = r.length; i < len; ++i) {
         if (r[i] != null && r[i] instanceof FetchResponse) {
            FetchResponse fr = (FetchResponse)r[i];
            UID u;
            if ((u = (UID)fr.getItem(UID.class)) != null) {
               v.addElement(u);
            }
         }
      }

      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
      UID[] ua = new UID[v.size()];
      v.copyInto(ua);
      return ua;
   }

   public UID[] fetchSequenceNumbers(long[] uids) throws ProtocolException {
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < uids.length; ++i) {
         if (i > 0) {
            sb.append(",");
         }

         sb.append(String.valueOf(uids[i]));
      }

      Response[] r = this.fetch(sb.toString(), "UID", true);
      Vector v = new Vector();
      int i = 0;

      for(int len = r.length; i < len; ++i) {
         if (r[i] != null && r[i] instanceof FetchResponse) {
            FetchResponse fr = (FetchResponse)r[i];
            UID u;
            if ((u = (UID)fr.getItem(UID.class)) != null) {
               v.addElement(u);
            }
         }
      }

      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
      UID[] ua = new UID[v.size()];
      v.copyInto(ua);
      return ua;
   }

   public Response[] fetch(MessageSet[] msgsets, String what) throws ProtocolException {
      return this.fetch(MessageSet.toString(msgsets), what, false);
   }

   public Response[] fetch(int start, int end, String what) throws ProtocolException {
      return this.fetch(start + ":" + end, what, false);
   }

   public Response[] fetch(int msg, String what) throws ProtocolException {
      return this.fetch(String.valueOf(msg), what, false);
   }

   private Response[] fetch(String msgSequence, String what, boolean uid) throws ProtocolException {
      return uid ? this.command("UID FETCH " + msgSequence + " (" + what + ")", (Argument)null) : this.command("FETCH " + msgSequence + " (" + what + ")", (Argument)null);
   }

   public void copy(MessageSet[] msgsets, String mbox) throws ProtocolException {
      this.copy(MessageSet.toString(msgsets), mbox);
   }

   public void copy(int start, int end, String mbox) throws ProtocolException {
      this.copy(start + ":" + end, mbox);
   }

   private void copy(String msgSequence, String mbox) throws ProtocolException {
      mbox = BASE64MailboxEncoder.encode(mbox);
      Argument args = new Argument();
      args.writeAtom(msgSequence);
      args.writeString(mbox);
      this.simpleCommand("COPY", args);
   }

   public void storeFlags(MessageSet[] msgsets, Flags flags, boolean set) throws ProtocolException {
      this.storeFlags(MessageSet.toString(msgsets), flags, set);
   }

   public void storeFlags(int start, int end, Flags flags, boolean set) throws ProtocolException {
      this.storeFlags(start + ":" + end, flags, set);
   }

   public void storeFlags(int msg, Flags flags, boolean set) throws ProtocolException {
      this.storeFlags(String.valueOf(msg), flags, set);
   }

   private void storeFlags(String msgset, Flags flags, boolean set) throws ProtocolException {
      Response[] r;
      if (set) {
         r = this.command("STORE " + msgset + " +FLAGS " + this.createFlagList(flags), (Argument)null);
      } else {
         r = this.command("STORE " + msgset + " -FLAGS " + this.createFlagList(flags), (Argument)null);
      }

      this.notifyResponseHandlers(r);
      this.handleResult(r[r.length - 1]);
   }

   private String createFlagList(Flags flags) {
      StringBuffer sb = new StringBuffer();
      sb.append("(");
      Flags.Flag[] sf = flags.getSystemFlags();
      boolean first = true;

      for(int i = 0; i < sf.length; ++i) {
         Flags.Flag f = sf[i];
         String s;
         if (f == Flags.Flag.ANSWERED) {
            s = "\\Answered";
         } else if (f == Flags.Flag.DELETED) {
            s = "\\Deleted";
         } else if (f == Flags.Flag.DRAFT) {
            s = "\\Draft";
         } else if (f == Flags.Flag.FLAGGED) {
            s = "\\Flagged";
         } else if (f == Flags.Flag.RECENT) {
            s = "\\Recent";
         } else {
            if (f != Flags.Flag.SEEN) {
               continue;
            }

            s = "\\Seen";
         }

         if (first) {
            first = false;
         } else {
            sb.append(' ');
         }

         sb.append(s);
      }

      String[] uf = flags.getUserFlags();

      for(int i = 0; i < uf.length; ++i) {
         if (first) {
            first = false;
         } else {
            sb.append(' ');
         }

         sb.append(uf[i]);
      }

      sb.append(")");
      return sb.toString();
   }

   public int[] search(MessageSet[] msgsets, SearchTerm term) throws ProtocolException, SearchException {
      return this.search(MessageSet.toString(msgsets), term);
   }

   public int[] search(SearchTerm term) throws ProtocolException, SearchException {
      return this.search("ALL", term);
   }

   private int[] search(String msgSequence, SearchTerm term) throws ProtocolException, SearchException {
      this.getSearchSequence();
      if (SearchSequence.isAscii(term)) {
         try {
            return this.issueSearch(msgSequence, term, (String)null);
         } catch (IOException var9) {
         }
      }

      for(int i = 0; i < this.searchCharsets.length; ++i) {
         if (this.searchCharsets[i] != null) {
            try {
               return this.issueSearch(msgSequence, term, this.searchCharsets[i]);
            } catch (CommandFailedException var5) {
               this.searchCharsets[i] = null;
            } catch (IOException var6) {
            } catch (ProtocolException var7) {
               throw var7;
            } catch (SearchException var8) {
               throw var8;
            }
         }
      }

      throw new SearchException("Search failed");
   }

   private int[] issueSearch(String msgSequence, SearchTerm term, String charset) throws ProtocolException, SearchException, IOException {
      Argument args = this.getSearchSequence().generateSequence(term, charset == null ? null : MimeUtility.javaCharset(charset));
      args.writeAtom(msgSequence);
      Response[] r;
      if (charset == null) {
         r = this.command("SEARCH", args);
      } else {
         r = this.command("SEARCH CHARSET " + charset, args);
      }

      Response response = r[r.length - 1];
      int[] matches = null;
      if (response.isOK()) {
         Vector v = new Vector();
         int i = 0;

         int i;
         for(i = r.length; i < i; ++i) {
            if (r[i] instanceof IMAPResponse) {
               IMAPResponse ir = (IMAPResponse)r[i];
               if (ir.keyEquals("SEARCH")) {
                  int num;
                  while((num = ir.readNumber()) != -1) {
                     v.addElement(new Integer(num));
                  }

                  r[i] = null;
               }
            }
         }

         i = v.size();
         matches = new int[i];

         for(i = 0; i < i; ++i) {
            matches[i] = (Integer)v.elementAt(i);
         }
      }

      this.notifyResponseHandlers(r);
      this.handleResult(response);
      return matches;
   }

   protected SearchSequence getSearchSequence() {
      if (this.searchSequence == null) {
         this.searchSequence = new SearchSequence();
      }

      return this.searchSequence;
   }

   public int[] sort(SortTerm[] term, SearchTerm sterm) throws ProtocolException, SearchException {
      if (!this.hasCapability("SORT*")) {
         throw new BadCommandException("SORT not supported");
      } else if (term != null && term.length != 0) {
         Argument args = new Argument();
         Argument sargs = new Argument();

         for(int i = 0; i < term.length; ++i) {
            sargs.writeAtom(term[i].toString());
         }

         args.writeArgument(sargs);
         args.writeAtom("UTF-8");
         if (sterm != null) {
            try {
               args.append(this.getSearchSequence().generateSequence(sterm, "UTF-8"));
            } catch (IOException var13) {
               throw new SearchException(var13.toString());
            }
         } else {
            args.writeAtom("ALL");
         }

         Response[] r = this.command("SORT", args);
         Response response = r[r.length - 1];
         int[] matches = null;
         if (response.isOK()) {
            Vector v = new Vector();
            int i = 0;

            int i;
            for(i = r.length; i < i; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (ir.keyEquals("SORT")) {
                     int num;
                     while((num = ir.readNumber()) != -1) {
                        v.addElement(new Integer(num));
                     }

                     r[i] = null;
                  }
               }
            }

            i = v.size();
            matches = new int[i];

            for(i = 0; i < i; ++i) {
               matches[i] = (Integer)v.elementAt(i);
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         return matches;
      } else {
         throw new BadCommandException("Must have at least one sort term");
      }
   }

   public Namespaces namespace() throws ProtocolException {
      if (!this.hasCapability("NAMESPACE")) {
         throw new BadCommandException("NAMESPACE not supported");
      } else {
         Response[] r = this.command("NAMESPACE", (Argument)null);
         Namespaces namespace = null;
         Response response = r[r.length - 1];
         if (response.isOK()) {
            int i = 0;

            for(int len = r.length; i < len; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (ir.keyEquals("NAMESPACE")) {
                     if (namespace == null) {
                        namespace = new Namespaces(ir);
                     }

                     r[i] = null;
                  }
               }
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         return namespace;
      }
   }

   public Quota[] getQuotaRoot(String mbox) throws ProtocolException {
      if (!this.hasCapability("QUOTA")) {
         throw new BadCommandException("GETQUOTAROOT not supported");
      } else {
         mbox = BASE64MailboxEncoder.encode(mbox);
         Argument args = new Argument();
         args.writeString(mbox);
         Response[] r = this.command("GETQUOTAROOT", args);
         Response response = r[r.length - 1];
         Hashtable tab = new Hashtable();
         if (response.isOK()) {
            int i = 0;

            for(int len = r.length; i < len; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (!ir.keyEquals("QUOTAROOT")) {
                     if (ir.keyEquals("QUOTA")) {
                        Quota quota = this.parseQuota(ir);
                        Quota q = (Quota)tab.get(quota.quotaRoot);
                        if (q != null && q.resources != null) {
                        }

                        tab.put(quota.quotaRoot, quota);
                        r[i] = null;
                     }
                  } else {
                     ir.readAtomString();
                     String root = null;

                     while((root = ir.readAtomString()) != null && root.length() > 0) {
                        tab.put(root, new Quota(root));
                     }

                     r[i] = null;
                  }
               }
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         Quota[] qa = new Quota[tab.size()];
         Enumeration e = tab.elements();

         for(int i = 0; e.hasMoreElements(); ++i) {
            qa[i] = (Quota)e.nextElement();
         }

         return qa;
      }
   }

   public Quota[] getQuota(String root) throws ProtocolException {
      if (!this.hasCapability("QUOTA")) {
         throw new BadCommandException("QUOTA not supported");
      } else {
         Argument args = new Argument();
         args.writeString(root);
         Response[] r = this.command("GETQUOTA", args);
         Quota quota = null;
         Vector v = new Vector();
         Response response = r[r.length - 1];
         if (response.isOK()) {
            int i = 0;

            for(int len = r.length; i < len; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (ir.keyEquals("QUOTA")) {
                     quota = this.parseQuota(ir);
                     v.addElement(quota);
                     r[i] = null;
                  }
               }
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         Quota[] qa = new Quota[v.size()];
         v.copyInto(qa);
         return qa;
      }
   }

   public void setQuota(Quota quota) throws ProtocolException {
      if (!this.hasCapability("QUOTA")) {
         throw new BadCommandException("QUOTA not supported");
      } else {
         Argument args = new Argument();
         args.writeString(quota.quotaRoot);
         Argument qargs = new Argument();
         if (quota.resources != null) {
            for(int i = 0; i < quota.resources.length; ++i) {
               qargs.writeAtom(quota.resources[i].name);
               qargs.writeNumber(quota.resources[i].limit);
            }
         }

         args.writeArgument(qargs);
         Response[] r = this.command("SETQUOTA", args);
         Response response = r[r.length - 1];
         this.notifyResponseHandlers(r);
         this.handleResult(response);
      }
   }

   private Quota parseQuota(Response r) throws ParsingException {
      String quotaRoot = r.readAtomString();
      Quota q = new Quota(quotaRoot);
      r.skipSpaces();
      if (r.readByte() != 40) {
         throw new ParsingException("parse error in QUOTA");
      } else {
         Vector v = new Vector();

         while(r.peekByte() != 41) {
            String name = r.readAtom();
            if (name != null) {
               long usage = r.readLong();
               long limit = r.readLong();
               Quota.Resource res = new Quota.Resource(name, usage, limit);
               v.addElement(res);
            }
         }

         r.readByte();
         q.resources = new Quota.Resource[v.size()];
         v.copyInto(q.resources);
         return q;
      }
   }

   public void setACL(String mbox, char modifier, ACL acl) throws ProtocolException {
      if (!this.hasCapability("ACL")) {
         throw new BadCommandException("ACL not supported");
      } else {
         mbox = BASE64MailboxEncoder.encode(mbox);
         Argument args = new Argument();
         args.writeString(mbox);
         args.writeString(acl.getName());
         String rights = acl.getRights().toString();
         if (modifier == '+' || modifier == '-') {
            rights = modifier + rights;
         }

         args.writeString(rights);
         Response[] r = this.command("SETACL", args);
         Response response = r[r.length - 1];
         this.notifyResponseHandlers(r);
         this.handleResult(response);
      }
   }

   public void deleteACL(String mbox, String user) throws ProtocolException {
      if (!this.hasCapability("ACL")) {
         throw new BadCommandException("ACL not supported");
      } else {
         mbox = BASE64MailboxEncoder.encode(mbox);
         Argument args = new Argument();
         args.writeString(mbox);
         args.writeString(user);
         Response[] r = this.command("DELETEACL", args);
         Response response = r[r.length - 1];
         this.notifyResponseHandlers(r);
         this.handleResult(response);
      }
   }

   public ACL[] getACL(String mbox) throws ProtocolException {
      if (!this.hasCapability("ACL")) {
         throw new BadCommandException("ACL not supported");
      } else {
         mbox = BASE64MailboxEncoder.encode(mbox);
         Argument args = new Argument();
         args.writeString(mbox);
         Response[] r = this.command("GETACL", args);
         Response response = r[r.length - 1];
         Vector v = new Vector();
         if (response.isOK()) {
            int i = 0;

            for(int len = r.length; i < len; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (ir.keyEquals("ACL")) {
                     ir.readAtomString();
                     String name = null;

                     while((name = ir.readAtomString()) != null) {
                        String rights = ir.readAtomString();
                        if (rights == null) {
                           break;
                        }

                        ACL acl = new ACL(name, new Rights(rights));
                        v.addElement(acl);
                     }

                     r[i] = null;
                  }
               }
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         ACL[] aa = new ACL[v.size()];
         v.copyInto(aa);
         return aa;
      }
   }

   public Rights[] listRights(String mbox, String user) throws ProtocolException {
      if (!this.hasCapability("ACL")) {
         throw new BadCommandException("ACL not supported");
      } else {
         mbox = BASE64MailboxEncoder.encode(mbox);
         Argument args = new Argument();
         args.writeString(mbox);
         args.writeString(user);
         Response[] r = this.command("LISTRIGHTS", args);
         Response response = r[r.length - 1];
         Vector v = new Vector();
         if (response.isOK()) {
            int i = 0;

            for(int len = r.length; i < len; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (ir.keyEquals("LISTRIGHTS")) {
                     ir.readAtomString();
                     ir.readAtomString();

                     String rights;
                     while((rights = ir.readAtomString()) != null) {
                        v.addElement(new Rights(rights));
                     }

                     r[i] = null;
                  }
               }
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         Rights[] ra = new Rights[v.size()];
         v.copyInto(ra);
         return ra;
      }
   }

   public Rights myRights(String mbox) throws ProtocolException {
      if (!this.hasCapability("ACL")) {
         throw new BadCommandException("ACL not supported");
      } else {
         mbox = BASE64MailboxEncoder.encode(mbox);
         Argument args = new Argument();
         args.writeString(mbox);
         Response[] r = this.command("MYRIGHTS", args);
         Response response = r[r.length - 1];
         Rights rights = null;
         if (response.isOK()) {
            int i = 0;

            for(int len = r.length; i < len; ++i) {
               if (r[i] instanceof IMAPResponse) {
                  IMAPResponse ir = (IMAPResponse)r[i];
                  if (ir.keyEquals("MYRIGHTS")) {
                     ir.readAtomString();
                     String rs = ir.readAtomString();
                     if (rights == null) {
                        rights = new Rights(rs);
                     }

                     r[i] = null;
                  }
               }
            }
         }

         this.notifyResponseHandlers(r);
         this.handleResult(response);
         return rights;
      }
   }

   public synchronized void idleStart() throws ProtocolException {
      if (!this.hasCapability("IDLE")) {
         throw new BadCommandException("IDLE not supported");
      } else {
         Vector v = new Vector();
         boolean done = false;
         Response r = null;

         try {
            this.idleTag = this.writeCommand("IDLE", (Argument)null);
         } catch (LiteralException var5) {
            v.addElement(var5.getResponse());
            done = true;
         } catch (Exception var6) {
            v.addElement(Response.byeResponse(var6));
            done = true;
         }

         while(!done) {
            try {
               r = this.readResponse();
            } catch (IOException var7) {
               r = Response.byeResponse(var7);
            } catch (ProtocolException var8) {
               continue;
            }

            v.addElement(r);
            if (r.isContinuation() || r.isBYE()) {
               done = true;
            }
         }

         Response[] responses = new Response[v.size()];
         v.copyInto(responses);
         r = responses[responses.length - 1];
         this.notifyResponseHandlers(responses);
         if (!r.isContinuation()) {
            this.handleResult(r);
         }

      }
   }

   public synchronized Response readIdleResponse() {
      if (this.idleTag == null) {
         return null;
      } else {
         Response r = null;

         while(r == null) {
            try {
               r = this.readResponse();
            } catch (InterruptedIOException var3) {
               if (var3.bytesTransferred == 0) {
                  r = null;
               } else {
                  r = Response.byeResponse(var3);
               }
            } catch (IOException var4) {
               r = Response.byeResponse(var4);
            } catch (ProtocolException var5) {
               r = Response.byeResponse(var5);
            }
         }

         return r;
      }
   }

   public boolean processIdleResponse(Response r) throws ProtocolException {
      Response[] responses = new Response[]{r};
      boolean done = false;
      this.notifyResponseHandlers(responses);
      if (r.isBYE()) {
         done = true;
      }

      if (r.isTagged() && r.getTag().equals(this.idleTag)) {
         done = true;
      }

      if (done) {
         this.idleTag = null;
      }

      this.handleResult(r);
      return !done;
   }

   public void idleAbort() throws ProtocolException {
      OutputStream os = this.getOutputStream();

      try {
         os.write(DONE);
         os.flush();
      } catch (IOException var3) {
      }

   }
}
