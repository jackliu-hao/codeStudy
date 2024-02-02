/*     */ package io.undertow.server.handlers.form;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.SameThreadExecutor;
/*     */ import io.undertow.util.URLUtils;
/*     */ import io.undertow.util.UrlDecodeException;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public class FormEncodedDataDefinition
/*     */   implements FormParserFactory.ParserDefinition<FormEncodedDataDefinition>
/*     */ {
/*     */   public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
/*     */   private static boolean parseExceptionLogAsDebug = false;
/*  49 */   private String defaultEncoding = "ISO-8859-1";
/*     */ 
/*     */   
/*     */   private boolean forceCreation = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public FormDataParser create(HttpServerExchange exchange) {
/*  57 */     String mimeType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
/*  58 */     if (this.forceCreation || (mimeType != null && mimeType.startsWith("application/x-www-form-urlencoded"))) {
/*     */       
/*  60 */       String charset = this.defaultEncoding;
/*  61 */       String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
/*  62 */       if (contentType != null) {
/*  63 */         String cs = Headers.extractQuotedValueFromHeader(contentType, "charset");
/*  64 */         if (cs != null) {
/*  65 */           charset = cs;
/*     */         }
/*     */       } 
/*  68 */       UndertowLogger.REQUEST_LOGGER.tracef("Created form encoded parser for %s", exchange);
/*  69 */       return new FormEncodedDataParser(charset, exchange);
/*     */     } 
/*  71 */     return null;
/*     */   }
/*     */   
/*     */   public String getDefaultEncoding() {
/*  75 */     return this.defaultEncoding;
/*     */   }
/*     */   
/*     */   public boolean isForceCreation() {
/*  79 */     return this.forceCreation;
/*     */   }
/*     */   
/*     */   public FormEncodedDataDefinition setForceCreation(boolean forceCreation) {
/*  83 */     this.forceCreation = forceCreation;
/*  84 */     return this;
/*     */   }
/*     */   
/*     */   public FormEncodedDataDefinition setDefaultEncoding(String defaultEncoding) {
/*  88 */     this.defaultEncoding = defaultEncoding;
/*  89 */     return this;
/*     */   }
/*     */   
/*     */   private static final class FormEncodedDataParser
/*     */     implements ChannelListener<StreamSourceChannel>, FormDataParser {
/*     */     private final HttpServerExchange exchange;
/*     */     private final FormData data;
/*  96 */     private final StringBuilder builder = new StringBuilder();
/*  97 */     private String name = null;
/*     */ 
/*     */     
/*     */     private String charset;
/*     */ 
/*     */     
/*     */     private HttpHandler handler;
/*     */ 
/*     */     
/* 106 */     private int state = 0;
/*     */     
/*     */     private FormEncodedDataParser(String charset, HttpServerExchange exchange) {
/* 109 */       this.exchange = exchange;
/* 110 */       this.charset = charset;
/* 111 */       this.data = new FormData(exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 1000));
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleEvent(StreamSourceChannel channel) {
/*     */       try {
/* 117 */         doParse(channel);
/* 118 */         if (this.state == 4) {
/* 119 */           this.exchange.dispatch(SameThreadExecutor.INSTANCE, this.handler);
/*     */         }
/* 121 */       } catch (IOException e) {
/* 122 */         IoUtils.safeClose((Closeable)channel);
/* 123 */         UndertowLogger.REQUEST_IO_LOGGER.ioExceptionReadingFromChannel(e);
/* 124 */         this.exchange.endExchange();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void doParse(StreamSourceChannel channel) throws IOException {
/* 130 */       int c = 0;
/* 131 */       PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/*     */       
/* 133 */       try { ByteBuffer buffer = pooled.getBuffer();
/*     */         while (true) {
/* 135 */           buffer.clear();
/* 136 */           c = channel.read(buffer);
/* 137 */           if (c > 0) {
/* 138 */             buffer.flip();
/* 139 */             while (buffer.hasRemaining()) {
/* 140 */               byte n = buffer.get();
/* 141 */               switch (this.state) {
/*     */                 case 0:
/* 143 */                   if (n == 61) {
/* 144 */                     this.name = this.builder.toString();
/* 145 */                     this.builder.setLength(0);
/* 146 */                     this.state = 2; continue;
/* 147 */                   }  if (n == 38) {
/* 148 */                     addPair(this.builder.toString(), "");
/* 149 */                     this.builder.setLength(0);
/* 150 */                     this.state = 0; continue;
/* 151 */                   }  if (n == 37 || n == 43 || n < 0) {
/* 152 */                     this.state = 1;
/* 153 */                     this.builder.append((char)(n & 0xFF)); continue;
/*     */                   } 
/* 155 */                   this.builder.append((char)n);
/*     */ 
/*     */ 
/*     */                 
/*     */                 case 1:
/* 160 */                   if (n == 61) {
/* 161 */                     this.name = decodeParameterName(this.builder.toString(), this.charset, true, new StringBuilder());
/* 162 */                     this.builder.setLength(0);
/* 163 */                     this.state = 2; continue;
/* 164 */                   }  if (n == 38) {
/* 165 */                     addPair(decodeParameterName(this.builder.toString(), this.charset, true, new StringBuilder()), "");
/* 166 */                     this.builder.setLength(0);
/* 167 */                     this.state = 0; continue;
/*     */                   } 
/* 169 */                   this.builder.append((char)(n & 0xFF));
/*     */ 
/*     */ 
/*     */                 
/*     */                 case 2:
/* 174 */                   if (n == 38) {
/* 175 */                     addPair(this.name, this.builder.toString());
/* 176 */                     this.builder.setLength(0);
/* 177 */                     this.state = 0; continue;
/* 178 */                   }  if (n == 37 || n == 43 || n < 0) {
/* 179 */                     this.state = 3;
/* 180 */                     this.builder.append((char)(n & 0xFF)); continue;
/*     */                   } 
/* 182 */                   this.builder.append((char)n);
/*     */ 
/*     */ 
/*     */                 
/*     */                 case 3:
/* 187 */                   if (n == 38) {
/* 188 */                     addPair(this.name, decodeParameterValue(this.name, this.builder.toString(), this.charset, true, new StringBuilder()));
/* 189 */                     this.builder.setLength(0);
/* 190 */                     this.state = 0; continue;
/*     */                   } 
/* 192 */                   this.builder.append((char)(n & 0xFF));
/*     */               } 
/*     */ 
/*     */ 
/*     */             
/*     */             } 
/*     */           } 
/* 199 */           if (c <= 0) {
/* 200 */             if (c == -1)
/* 201 */             { if (this.state == 2) {
/* 202 */                 addPair(this.name, this.builder.toString());
/* 203 */               } else if (this.state == 3) {
/* 204 */                 addPair(this.name, decodeParameterValue(this.name, this.builder.toString(), this.charset, true, new StringBuilder()));
/* 205 */               } else if (this.builder.length() > 0) {
/* 206 */                 if (this.state == 1) {
/* 207 */                   addPair(decodeParameterName(this.builder.toString(), this.charset, true, new StringBuilder()), "");
/*     */                 } else {
/* 209 */                   addPair(this.builder.toString(), "");
/*     */                 } 
/*     */               } 
/* 212 */               this.state = 4;
/* 213 */               this.exchange.putAttachment(FORM_DATA, this.data); }  return;
/*     */           } 
/*     */         }  }
/* 216 */       finally { pooled.close(); }
/*     */     
/*     */     }
/*     */ 
/*     */     
/*     */     private void addPair(String name, String value) {
/* 222 */       if (name != null && value != null) {
/* 223 */         this.data.add(name, value);
/*     */       }
/*     */     }
/*     */     
/*     */     private String decodeParameterValue(String name, String value, String charset, boolean decodeSlash, StringBuilder stringBuilder) {
/* 228 */       String decodedValue = null;
/*     */       
/*     */       try {
/* 231 */         decodedValue = URLUtils.decode(value, charset, decodeSlash, stringBuilder);
/* 232 */       } catch (UrlDecodeException e) {
/* 233 */         if (!FormEncodedDataDefinition.parseExceptionLogAsDebug) {
/* 234 */           UndertowLogger.REQUEST_LOGGER.errorf(UndertowMessages.MESSAGES.failedToDecodeParameterValue(name, value, (Exception)e), new Object[0]);
/* 235 */           FormEncodedDataDefinition.parseExceptionLogAsDebug = true;
/*     */         } else {
/* 237 */           UndertowLogger.REQUEST_LOGGER.debugf(UndertowMessages.MESSAGES.failedToDecodeParameterValue(name, value, (Exception)e), new Object[0]);
/*     */         } 
/*     */       } 
/*     */       
/* 241 */       return decodedValue;
/*     */     }
/*     */     
/*     */     private String decodeParameterName(String name, String charset, boolean decodeSlash, StringBuilder stringBuilder) {
/* 245 */       String decodedName = null;
/*     */       
/*     */       try {
/* 248 */         decodedName = URLUtils.decode(name, charset, decodeSlash, stringBuilder);
/* 249 */       } catch (UrlDecodeException e) {
/* 250 */         if (!FormEncodedDataDefinition.parseExceptionLogAsDebug) {
/* 251 */           UndertowLogger.REQUEST_LOGGER.errorf(UndertowMessages.MESSAGES.failedToDecodeParameterName(name, (Exception)e), new Object[0]);
/* 252 */           FormEncodedDataDefinition.parseExceptionLogAsDebug = true;
/*     */         } else {
/* 254 */           UndertowLogger.REQUEST_LOGGER.debugf(UndertowMessages.MESSAGES.failedToDecodeParameterName(name, (Exception)e), new Object[0]);
/*     */         } 
/*     */       } 
/*     */       
/* 258 */       return decodedName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void parse(HttpHandler handler) throws Exception {
/* 263 */       if (this.exchange.getAttachment(FORM_DATA) != null) {
/* 264 */         handler.handleRequest(this.exchange);
/*     */         return;
/*     */       } 
/* 267 */       this.handler = handler;
/* 268 */       StreamSourceChannel channel = this.exchange.getRequestChannel();
/* 269 */       if (channel == null) {
/* 270 */         throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
/*     */       }
/* 272 */       doParse(channel);
/* 273 */       if (this.state != 4) {
/* 274 */         channel.getReadSetter().set(this);
/* 275 */         channel.resumeReads();
/*     */       } else {
/* 277 */         this.exchange.dispatch(SameThreadExecutor.INSTANCE, handler);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public FormData parseBlocking() throws IOException {
/* 284 */       FormData existing = (FormData)this.exchange.getAttachment(FORM_DATA);
/* 285 */       if (existing != null) {
/* 286 */         return existing;
/*     */       }
/*     */       
/* 289 */       StreamSourceChannel channel = this.exchange.getRequestChannel();
/* 290 */       if (channel == null) {
/* 291 */         throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
/*     */       }
/* 293 */       while (this.state != 4) {
/* 294 */         doParse(channel);
/* 295 */         if (this.state != 4) {
/* 296 */           channel.awaitReadable();
/*     */         }
/*     */       } 
/*     */       
/* 300 */       return this.data;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void setCharacterEncoding(String encoding) {
/* 310 */       this.charset = encoding;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\form\FormEncodedDataDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */