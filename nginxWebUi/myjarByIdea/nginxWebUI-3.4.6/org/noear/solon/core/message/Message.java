package org.noear.solon.core.message;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;
import org.noear.solon.annotation.Note;

public class Message {
   private final int flag;
   private final String key;
   private final String resourceDescriptor;
   private final String header;
   private final byte[] body;
   private static final byte[] EMPTY_B = new byte[0];
   private static final String EMPTY_S = "";
   private Charset charset;
   private boolean isString;
   private boolean _handled;

   @Note("1.消息标志")
   public int flag() {
      return this.flag;
   }

   @Note("2.消息key")
   public String key() {
      return this.key;
   }

   @Note("3.资源描述")
   public String resourceDescriptor() {
      return this.resourceDescriptor;
   }

   @Note("4.消息头")
   public String header() {
      return this.header;
   }

   @Note("5.消息主体")
   public byte[] body() {
      return this.body;
   }

   public String bodyAsString() {
      return this.body == null ? null : new String(this.body, this.charset);
   }

   public <T> T map(Function<Message, T> mapper) {
      return mapper.apply(this);
   }

   public Message(int flag, String key, String resourceDescriptor, String header, byte[] body) {
      this.charset = StandardCharsets.UTF_8;
      this.flag = flag;
      this.key = key == null ? "" : key;
      this.resourceDescriptor = resourceDescriptor == null ? "" : resourceDescriptor;
      this.header = header == null ? "" : header;
      this.body = body == null ? EMPTY_B : body;
   }

   public Message(int flag, String key, String resourceDescriptor, byte[] body) {
      this(flag, key, resourceDescriptor, (String)null, body);
   }

   public Message(int flag, String key, byte[] body) {
      this(flag, key, (String)null, (String)null, body);
   }

   public Message(int flag, byte[] body) {
      this(flag, (String)null, (String)null, (String)null, body);
   }

   public String toString() {
      return "Message{flag=" + this.flag() + ", key='" + this.key() + '\'' + ", resourceDescriptor='" + this.resourceDescriptor() + '\'' + ", header='" + this.header() + '\'' + ", body='" + this.bodyAsString() + '\'' + '}';
   }

   public Charset getCharset() {
      return this.charset;
   }

   public void setCharset(Charset charset) {
      if (charset != null) {
         this.charset = charset;
      }

   }

   public boolean isString() {
      return this.isString;
   }

   public Message isString(boolean isString) {
      this.isString = isString;
      return this;
   }

   public void setHandled(boolean handled) {
      this._handled = handled;
   }

   public boolean getHandled() {
      return this._handled;
   }

   public static String guid() {
      return UUID.randomUUID().toString();
   }

   public static Message wrap(byte[] body) {
      return new Message(10, guid(), body);
   }

   public static Message wrap(String body) {
      return wrap(body.getBytes(StandardCharsets.UTF_8));
   }

   public static Message wrap(String resourceDescriptor, String header, byte[] body) {
      return new Message(10, guid(), resourceDescriptor, header, body);
   }

   public static Message wrap(String resourceDescriptor, String header, String body) {
      return wrap(resourceDescriptor, header, body.getBytes(StandardCharsets.UTF_8));
   }

   public static Message wrapContainer(byte[] body) {
      return new Message(1, body);
   }

   public static Message wrapHeartbeat() {
      return new Message(11, guid(), (byte[])null);
   }

   public static Message wrapHandshake(String header, byte[] body) {
      return new Message(12, guid(), "", header, body);
   }

   public static Message wrapHandshake(String header) {
      return wrapHandshake(header, (byte[])null);
   }

   public static Message wrapResponse(Message request, byte[] body) {
      return new Message(13, request.key(), body);
   }

   public static Message wrapResponse(Message request, String body) {
      return new Message(13, request.key(), body.getBytes(StandardCharsets.UTF_8));
   }
}
