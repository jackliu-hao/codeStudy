/*    */ package cn.hutool.json.serialize;
/*    */ 
/*    */ import cn.hutool.json.JSON;
/*    */ import cn.hutool.json.JSONException;
/*    */ import cn.hutool.json.JSONObject;
/*    */ import java.time.LocalDate;
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.LocalTime;
/*    */ import java.time.temporal.TemporalAccessor;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TemporalAccessorSerializer
/*    */   implements JSONObjectSerializer<TemporalAccessor>, JSONDeserializer<TemporalAccessor>
/*    */ {
/*    */   private static final String YEAR_KEY = "year";
/*    */   private static final String MONTH_KEY = "month";
/*    */   private static final String DAY_KEY = "day";
/*    */   private static final String HOUR_KEY = "hour";
/*    */   private static final String MINUTE_KEY = "minute";
/*    */   private static final String SECOND_KEY = "second";
/*    */   private static final String NANO_KEY = "nano";
/*    */   private final Class<? extends TemporalAccessor> temporalAccessorClass;
/*    */   
/*    */   public TemporalAccessorSerializer(Class<? extends TemporalAccessor> temporalAccessorClass) {
/* 31 */     this.temporalAccessorClass = temporalAccessorClass;
/*    */   }
/*    */ 
/*    */   
/*    */   public void serialize(JSONObject json, TemporalAccessor bean) {
/* 36 */     if (bean instanceof LocalDate) {
/* 37 */       LocalDate localDate = (LocalDate)bean;
/* 38 */       json.set("year", Integer.valueOf(localDate.getYear()));
/* 39 */       json.set("month", Integer.valueOf(localDate.getMonthValue()));
/* 40 */       json.set("day", Integer.valueOf(localDate.getDayOfMonth()));
/* 41 */     } else if (bean instanceof LocalDateTime) {
/* 42 */       LocalDateTime localDateTime = (LocalDateTime)bean;
/* 43 */       json.set("year", Integer.valueOf(localDateTime.getYear()));
/* 44 */       json.set("month", Integer.valueOf(localDateTime.getMonthValue()));
/* 45 */       json.set("day", Integer.valueOf(localDateTime.getDayOfMonth()));
/* 46 */       json.set("hour", Integer.valueOf(localDateTime.getHour()));
/* 47 */       json.set("minute", Integer.valueOf(localDateTime.getMinute()));
/* 48 */       json.set("second", Integer.valueOf(localDateTime.getSecond()));
/* 49 */       json.set("nano", Integer.valueOf(localDateTime.getNano()));
/* 50 */     } else if (bean instanceof LocalTime) {
/* 51 */       LocalTime localTime = (LocalTime)bean;
/* 52 */       json.set("hour", Integer.valueOf(localTime.getHour()));
/* 53 */       json.set("minute", Integer.valueOf(localTime.getMinute()));
/* 54 */       json.set("second", Integer.valueOf(localTime.getSecond()));
/* 55 */       json.set("nano", Integer.valueOf(localTime.getNano()));
/*    */     } else {
/* 57 */       throw new JSONException("Unsupported type to JSON: {}", new Object[] { bean.getClass().getName() });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public TemporalAccessor deserialize(JSON json) {
/* 63 */     JSONObject jsonObject = (JSONObject)json;
/* 64 */     if (LocalDate.class.equals(this.temporalAccessorClass))
/* 65 */       return LocalDate.of(jsonObject.getInt("year").intValue(), jsonObject.getInt("month").intValue(), jsonObject.getInt("day").intValue()); 
/* 66 */     if (LocalDateTime.class.equals(this.temporalAccessorClass))
/* 67 */       return LocalDateTime.of(jsonObject.getInt("year").intValue(), jsonObject.getInt("month").intValue(), jsonObject.getInt("day").intValue(), jsonObject
/* 68 */           .getInt("hour").intValue(), jsonObject.getInt("minute").intValue(), jsonObject.getInt("second").intValue(), jsonObject.getInt("nano").intValue()); 
/* 69 */     if (LocalTime.class.equals(this.temporalAccessorClass)) {
/* 70 */       return LocalTime.of(jsonObject.getInt("hour").intValue(), jsonObject.getInt("minute").intValue(), jsonObject.getInt("second").intValue(), jsonObject.getInt("nano").intValue());
/*    */     }
/*    */     
/* 73 */     throw new JSONException("Unsupported type from JSON: {}", new Object[] { this.temporalAccessorClass });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\serialize\TemporalAccessorSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */