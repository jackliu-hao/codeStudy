package org.noear.solon.socketd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.util.HeaderUtil;

public class SocketContext extends ContextEmpty {
   private InetSocketAddress _inetSocketAddress;
   private Session _session;
   private Message _message;
   private boolean _messageIsString;
   private MethodType _method;
   private URI _uri;
   ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();

   public SocketContext(Session session, Message message) {
      this._session = session;
      this._message = message;
      this._messageIsString = message.isString();
      this._method = session.method();
      this._inetSocketAddress = session.getRemoteAddress();
      if (session.headerMap().size() > 0) {
         this.headerMap().putAll(session.headerMap());
      }

      if (Utils.isNotEmpty(message.header())) {
         Map<String, String> headerMap = HeaderUtil.decodeHeaderMap(message.header());
         this.headerMap().putAll(headerMap);
      }

      if (session.paramMap().size() > 0) {
         this.paramMap().putAll(session.paramMap());
      }

      this.sessionState = new SocketSessionState(this._session);
   }

   public Object request() {
      return this._session;
   }

   public String ip() {
      return this._inetSocketAddress == null ? null : this._inetSocketAddress.getAddress().toString();
   }

   public boolean isMultipart() {
      return false;
   }

   public String method() {
      return this._method.name;
   }

   public String protocol() {
      return this._method == MethodType.WEBSOCKET ? "WS" : "SOCKET";
   }

   public URI uri() {
      if (this._uri == null) {
         this._uri = URI.create(this.url());
      }

      return this._uri;
   }

   public String url() {
      return this._message.resourceDescriptor();
   }

   public long contentLength() {
      return this._message.body() == null ? 0L : (long)this._message.body().length;
   }

   public String contentType() {
      return (String)this.headerMap().get("Content-Type");
   }

   public String queryString() {
      return this.uri().getQuery();
   }

   public InputStream bodyAsStream() throws IOException {
      return new ByteArrayInputStream(this._message.body());
   }

   public Object response() {
      return this._session;
   }

   public void contentType(String contentType) {
      this.headerSet("Content-Type", contentType);
   }

   public OutputStream outputStream() {
      return this._outputStream;
   }

   public void output(byte[] bytes) {
      try {
         this._outputStream.write(bytes);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public void output(InputStream stream) {
      try {
         byte[] buff = new byte[100];
         int rc = false;

         int rc;
         while((rc = stream.read(buff, 0, 100)) > 0) {
            this._outputStream.write(buff, 0, rc);
         }

      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   protected void commit() throws IOException {
      if (this._session.isValid()) {
         if (this._messageIsString) {
            this._session.send(this._outputStream.toString());
         } else {
            Message msg = Message.wrapResponse(this._message, this._outputStream.toByteArray());
            this._session.send(msg);
         }
      }

   }

   public void close() throws IOException {
      this._session.close();
   }
}
