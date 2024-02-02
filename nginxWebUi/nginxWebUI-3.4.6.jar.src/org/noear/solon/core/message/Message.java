/*     */ package org.noear.solon.core.message;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.UUID;
/*     */ import java.util.function.Function;
/*     */ import org.noear.solon.annotation.Note;
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
/*     */ public class Message
/*     */ {
/*     */   private final int flag;
/*     */   private final String key;
/*     */   private final String resourceDescriptor;
/*     */   private final String header;
/*     */   private final byte[] body;
/*     */   
/*     */   @Note("1.消息标志")
/*     */   public int flag() {
/*  28 */     return this.flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("2.消息key")
/*     */   public String key() {
/*  38 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("3.资源描述")
/*     */   public String resourceDescriptor() {
/*  48 */     return this.resourceDescriptor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("4.消息头")
/*     */   public String header() {
/*  56 */     return this.header;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("5.消息主体")
/*     */   public byte[] body() {
/*  67 */     return this.body;
/*     */   }
/*     */   
/*     */   public String bodyAsString() {
/*  71 */     if (this.body == null) {
/*  72 */       return null;
/*     */     }
/*  74 */     return new String(this.body, this.charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T map(Function<Message, T> mapper) {
/*  84 */     return mapper.apply(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  89 */   private static final byte[] EMPTY_B = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String EMPTY_S = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Charset charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean _handled;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message(int flag, String key, String resourceDescriptor, String header, byte[] body) {
/* 126 */     this.charset = StandardCharsets.UTF_8; this.flag = flag; this.key = (key == null) ? "" : key; this.resourceDescriptor = (resourceDescriptor == null) ? "" : resourceDescriptor; this.header = (header == null) ? "" : header;
/*     */     this.body = (body == null) ? EMPTY_B : body;
/*     */   }
/* 129 */   public Message(int flag, String key, String resourceDescriptor, byte[] body) { this(flag, key, resourceDescriptor, null, body); } public Message(int flag, String key, byte[] body) { this(flag, key, null, null, body); } public Charset getCharset() { return this.charset; }
/*     */   public Message(int flag, byte[] body) { this(flag, null, null, null, body); } public String toString() {
/*     */     return "Message{flag=" + flag() + ", key='" + key() + '\'' + ", resourceDescriptor='" + resourceDescriptor() + '\'' + ", header='" + header() + '\'' + ", body='" + bodyAsString() + '\'' + '}';
/*     */   } public void setCharset(Charset charset) {
/* 133 */     if (charset != null) {
/* 134 */       this.charset = charset;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isString() {
/* 144 */     return this.isString;
/*     */   }
/*     */   public Message isString(boolean isString) {
/* 147 */     this.isString = isString;
/* 148 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHandled(boolean handled) {
/* 156 */     this._handled = handled;
/*     */   }
/*     */   
/*     */   public boolean getHandled() {
/* 160 */     return this._handled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String guid() {
/* 170 */     return UUID.randomUUID().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Message wrap(byte[] body) {
/* 177 */     return new Message(10, guid(), body);
/*     */   }
/*     */   
/*     */   public static Message wrap(String body) {
/* 181 */     return wrap(body.getBytes(StandardCharsets.UTF_8));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Message wrap(String resourceDescriptor, String header, byte[] body) {
/* 189 */     return new Message(10, guid(), resourceDescriptor, header, body);
/*     */   }
/*     */   
/*     */   public static Message wrap(String resourceDescriptor, String header, String body) {
/* 193 */     return wrap(resourceDescriptor, header, body.getBytes(StandardCharsets.UTF_8));
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
/*     */   public static Message wrapContainer(byte[] body) {
/* 205 */     return new Message(1, body);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Message wrapHeartbeat() {
/* 212 */     return new Message(11, guid(), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Message wrapHandshake(String header, byte[] body) {
/* 220 */     return new Message(12, guid(), "", header, body);
/*     */   }
/*     */   
/*     */   public static Message wrapHandshake(String header) {
/* 224 */     return wrapHandshake(header, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Message wrapResponse(Message request, byte[] body) {
/* 231 */     return new Message(13, request.key(), body);
/*     */   }
/*     */   
/*     */   public static Message wrapResponse(Message request, String body) {
/* 235 */     return new Message(13, request.key(), body.getBytes(StandardCharsets.UTF_8));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\message\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */