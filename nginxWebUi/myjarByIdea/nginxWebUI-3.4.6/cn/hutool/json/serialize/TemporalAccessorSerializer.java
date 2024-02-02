package cn.hutool.json.serialize;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;

public class TemporalAccessorSerializer implements JSONObjectSerializer<TemporalAccessor>, JSONDeserializer<TemporalAccessor> {
   private static final String YEAR_KEY = "year";
   private static final String MONTH_KEY = "month";
   private static final String DAY_KEY = "day";
   private static final String HOUR_KEY = "hour";
   private static final String MINUTE_KEY = "minute";
   private static final String SECOND_KEY = "second";
   private static final String NANO_KEY = "nano";
   private final Class<? extends TemporalAccessor> temporalAccessorClass;

   public TemporalAccessorSerializer(Class<? extends TemporalAccessor> temporalAccessorClass) {
      this.temporalAccessorClass = temporalAccessorClass;
   }

   public void serialize(JSONObject json, TemporalAccessor bean) {
      if (bean instanceof LocalDate) {
         LocalDate localDate = (LocalDate)bean;
         json.set("year", localDate.getYear());
         json.set("month", localDate.getMonthValue());
         json.set("day", localDate.getDayOfMonth());
      } else if (bean instanceof LocalDateTime) {
         LocalDateTime localDateTime = (LocalDateTime)bean;
         json.set("year", localDateTime.getYear());
         json.set("month", localDateTime.getMonthValue());
         json.set("day", localDateTime.getDayOfMonth());
         json.set("hour", localDateTime.getHour());
         json.set("minute", localDateTime.getMinute());
         json.set("second", localDateTime.getSecond());
         json.set("nano", localDateTime.getNano());
      } else {
         if (!(bean instanceof LocalTime)) {
            throw new JSONException("Unsupported type to JSON: {}", new Object[]{bean.getClass().getName()});
         }

         LocalTime localTime = (LocalTime)bean;
         json.set("hour", localTime.getHour());
         json.set("minute", localTime.getMinute());
         json.set("second", localTime.getSecond());
         json.set("nano", localTime.getNano());
      }

   }

   public TemporalAccessor deserialize(JSON json) {
      JSONObject jsonObject = (JSONObject)json;
      if (LocalDate.class.equals(this.temporalAccessorClass)) {
         return LocalDate.of(jsonObject.getInt("year"), jsonObject.getInt("month"), jsonObject.getInt("day"));
      } else if (LocalDateTime.class.equals(this.temporalAccessorClass)) {
         return LocalDateTime.of(jsonObject.getInt("year"), jsonObject.getInt("month"), jsonObject.getInt("day"), jsonObject.getInt("hour"), jsonObject.getInt("minute"), jsonObject.getInt("second"), jsonObject.getInt("nano"));
      } else if (LocalTime.class.equals(this.temporalAccessorClass)) {
         return LocalTime.of(jsonObject.getInt("hour"), jsonObject.getInt("minute"), jsonObject.getInt("second"), jsonObject.getInt("nano"));
      } else {
         throw new JSONException("Unsupported type from JSON: {}", new Object[]{this.temporalAccessorClass});
      }
   }
}
