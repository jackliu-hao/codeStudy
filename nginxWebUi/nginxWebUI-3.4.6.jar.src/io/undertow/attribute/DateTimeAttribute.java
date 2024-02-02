/*     */ package io.undertow.attribute;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.DateUtils;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
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
/*     */ 
/*     */ public class DateTimeAttribute
/*     */   implements ExchangeAttribute
/*     */ {
/*     */   public static final String DATE_TIME_SHORT = "%t";
/*     */   public static final String DATE_TIME = "%{DATE_TIME}";
/*     */   public static final String CUSTOM_TIME = "%{time,";
/*  39 */   public static final ExchangeAttribute INSTANCE = new DateTimeAttribute();
/*     */   
/*     */   private final String dateFormat;
/*     */   private final ThreadLocal<SimpleDateFormat> cachedFormat;
/*     */   
/*     */   private DateTimeAttribute() {
/*  45 */     this.dateFormat = null;
/*  46 */     this.cachedFormat = null;
/*     */   }
/*     */   
/*     */   public DateTimeAttribute(String dateFormat) {
/*  50 */     this(dateFormat, null);
/*     */   }
/*     */   
/*     */   public DateTimeAttribute(final String dateFormat, final String timezone) {
/*  54 */     this.dateFormat = dateFormat;
/*  55 */     this.cachedFormat = new ThreadLocal<SimpleDateFormat>()
/*     */       {
/*     */         protected SimpleDateFormat initialValue() {
/*  58 */           SimpleDateFormat format = new SimpleDateFormat(dateFormat);
/*  59 */           if (timezone != null) {
/*  60 */             format.setTimeZone(TimeZone.getTimeZone(timezone));
/*     */           }
/*  62 */           return format;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public String readAttribute(HttpServerExchange exchange) {
/*  68 */     if (this.dateFormat == null) {
/*  69 */       return DateUtils.toCommonLogFormat(new Date());
/*     */     }
/*  71 */     SimpleDateFormat dateFormat = this.cachedFormat.get();
/*  72 */     return dateFormat.format(new Date());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/*  78 */     throw new ReadOnlyAttributeException("Date time", newValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  83 */     if (this.dateFormat == null)
/*  84 */       return "%{DATE_TIME}"; 
/*  85 */     return "%{time," + this.dateFormat + "}";
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */     implements ExchangeAttributeBuilder
/*     */   {
/*     */     public String name() {
/*  92 */       return "Date Time";
/*     */     }
/*     */ 
/*     */     
/*     */     public ExchangeAttribute build(String token) {
/*  97 */       if (token.equals("%{DATE_TIME}") || token.equals("%t")) {
/*  98 */         return DateTimeAttribute.INSTANCE;
/*     */       }
/* 100 */       if (token.startsWith("%{time,") && token.endsWith("}")) {
/* 101 */         return new DateTimeAttribute(token.substring("%{time,".length(), token.length() - 1));
/*     */       }
/* 103 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int priority() {
/* 108 */       return 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\DateTimeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */