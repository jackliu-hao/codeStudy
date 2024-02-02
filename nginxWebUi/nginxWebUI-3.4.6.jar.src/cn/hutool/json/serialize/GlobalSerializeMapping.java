/*     */ package cn.hutool.json.serialize;
/*     */ 
/*     */ import cn.hutool.json.JSON;
/*     */ import java.lang.reflect.Type;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class GlobalSerializeMapping
/*     */ {
/*  25 */   private static Map<Type, JSONSerializer<? extends JSON, ?>> serializerMap = new ConcurrentHashMap<>();
/*  26 */   private static Map<Type, JSONDeserializer<?>> deserializerMap = new ConcurrentHashMap<>();
/*     */   static {
/*  28 */     TemporalAccessorSerializer localDateSerializer = new TemporalAccessorSerializer((Class)LocalDate.class);
/*  29 */     serializerMap.put(LocalDate.class, localDateSerializer);
/*  30 */     deserializerMap.put(LocalDate.class, localDateSerializer);
/*     */     
/*  32 */     TemporalAccessorSerializer localDateTimeSerializer = new TemporalAccessorSerializer((Class)LocalDateTime.class);
/*  33 */     serializerMap.put(LocalDateTime.class, localDateTimeSerializer);
/*  34 */     deserializerMap.put(LocalDateTime.class, localDateTimeSerializer);
/*     */     
/*  36 */     TemporalAccessorSerializer localTimeSerializer = new TemporalAccessorSerializer((Class)LocalTime.class);
/*  37 */     serializerMap.put(LocalTime.class, localTimeSerializer);
/*  38 */     deserializerMap.put(LocalTime.class, localTimeSerializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void put(Type type, JSONArraySerializer<?> serializer) {
/*  48 */     putInternal(type, (JSONSerializer)serializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void put(Type type, JSONObjectSerializer<?> serializer) {
/*  58 */     putInternal(type, (JSONSerializer)serializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static synchronized void putInternal(Type type, JSONSerializer<? extends JSON, ?> serializer) {
/*  68 */     if (null == serializerMap) {
/*  69 */       serializerMap = new ConcurrentHashMap<>();
/*     */     }
/*  71 */     serializerMap.put(type, serializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void put(Type type, JSONDeserializer<?> deserializer) {
/*  81 */     if (null == deserializerMap) {
/*  82 */       deserializerMap = new ConcurrentHashMap<>();
/*     */     }
/*  84 */     deserializerMap.put(type, deserializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JSONSerializer<? extends JSON, ?> getSerializer(Type type) {
/*  93 */     if (null == serializerMap) {
/*  94 */       return null;
/*     */     }
/*  96 */     return serializerMap.get(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JSONDeserializer<?> getDeserializer(Type type) {
/* 105 */     if (null == deserializerMap) {
/* 106 */       return null;
/*     */     }
/* 108 */     return deserializerMap.get(type);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\serialize\GlobalSerializeMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */