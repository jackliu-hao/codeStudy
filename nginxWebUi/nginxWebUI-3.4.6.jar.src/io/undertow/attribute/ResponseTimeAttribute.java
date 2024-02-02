/*     */ package io.undertow.attribute;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class ResponseTimeAttribute
/*     */   implements ExchangeAttribute
/*     */ {
/*  33 */   private static final AttachmentKey<Long> FIRST_RESPONSE_TIME_NANOS = AttachmentKey.create(Long.class);
/*     */   
/*     */   public static final String RESPONSE_TIME_MILLIS_SHORT = "%D";
/*     */   
/*     */   public static final String RESPONSE_TIME_SECONDS_SHORT = "%T";
/*     */   public static final String RESPONSE_TIME_MILLIS = "%{RESPONSE_TIME}";
/*     */   public static final String RESPONSE_TIME_MICROS = "%{RESPONSE_TIME_MICROS}";
/*     */   public static final String RESPONSE_TIME_NANOS = "%{RESPONSE_TIME_NANOS}";
/*     */   private final TimeUnit timeUnit;
/*     */   
/*     */   public ResponseTimeAttribute(TimeUnit timeUnit) {
/*  44 */     this.timeUnit = timeUnit;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readAttribute(HttpServerExchange exchange) {
/*  49 */     long nanos, requestStartTime = exchange.getRequestStartTime();
/*  50 */     if (requestStartTime == -1L) {
/*  51 */       return null;
/*     */     }
/*     */     
/*  54 */     Long first = (Long)exchange.getAttachment(FIRST_RESPONSE_TIME_NANOS);
/*  55 */     if (first != null) {
/*  56 */       nanos = first.longValue();
/*     */     } else {
/*  58 */       nanos = System.nanoTime() - requestStartTime;
/*  59 */       if (exchange.isResponseComplete())
/*     */       {
/*  61 */         exchange.putAttachment(FIRST_RESPONSE_TIME_NANOS, Long.valueOf(nanos));
/*     */       }
/*     */     } 
/*  64 */     if (this.timeUnit == TimeUnit.SECONDS) {
/*  65 */       StringBuilder buf = new StringBuilder();
/*  66 */       long milis = TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS);
/*  67 */       buf.append(Long.toString(milis / 1000L));
/*  68 */       buf.append('.');
/*  69 */       int remains = (int)(milis % 1000L);
/*  70 */       buf.append(Long.toString((remains / 100)));
/*  71 */       remains %= 100;
/*  72 */       buf.append(Long.toString((remains / 10)));
/*  73 */       buf.append(Long.toString((remains % 10)));
/*  74 */       return buf.toString();
/*     */     } 
/*  76 */     return String.valueOf(this.timeUnit.convert(nanos, TimeUnit.NANOSECONDS));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/*  82 */     throw new ReadOnlyAttributeException("Response Time", newValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  87 */     if (this.timeUnit.equals(TimeUnit.MILLISECONDS)) {
/*  88 */       return "%{RESPONSE_TIME}";
/*     */     }
/*  90 */     if (this.timeUnit.equals(TimeUnit.SECONDS)) {
/*  91 */       return "%T";
/*     */     }
/*  93 */     if (this.timeUnit.equals(TimeUnit.MICROSECONDS)) {
/*  94 */       return "%{RESPONSE_TIME_MICROS}";
/*     */     }
/*  96 */     if (this.timeUnit.equals(TimeUnit.NANOSECONDS)) {
/*  97 */       return "%{RESPONSE_TIME_NANOS}";
/*     */     }
/*  99 */     return "ResponseTimeAttribute";
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */     implements ExchangeAttributeBuilder
/*     */   {
/*     */     public String name() {
/* 106 */       return "Response Time";
/*     */     }
/*     */ 
/*     */     
/*     */     public ExchangeAttribute build(String token) {
/* 111 */       if (token.equals("%{RESPONSE_TIME}") || token.equals("%D")) {
/* 112 */         return new ResponseTimeAttribute(TimeUnit.MILLISECONDS);
/*     */       }
/* 114 */       if (token.equals("%T")) {
/* 115 */         return new ResponseTimeAttribute(TimeUnit.SECONDS);
/*     */       }
/* 117 */       if (token.equals("%{RESPONSE_TIME_MICROS}")) {
/* 118 */         return new ResponseTimeAttribute(TimeUnit.MICROSECONDS);
/*     */       }
/* 120 */       if (token.equals("%{RESPONSE_TIME_NANOS}")) {
/* 121 */         return new ResponseTimeAttribute(TimeUnit.NANOSECONDS);
/*     */       }
/* 123 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int priority() {
/* 128 */       return 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ResponseTimeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */