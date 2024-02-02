package cn.hutool.json.serialize;

import cn.hutool.json.JSON;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalSerializeMapping {
   private static Map<Type, JSONSerializer<? extends JSON, ?>> serializerMap = new ConcurrentHashMap();
   private static Map<Type, JSONDeserializer<?>> deserializerMap = new ConcurrentHashMap();

   public static void put(Type type, JSONArraySerializer<?> serializer) {
      putInternal(type, serializer);
   }

   public static void put(Type type, JSONObjectSerializer<?> serializer) {
      putInternal(type, serializer);
   }

   private static synchronized void putInternal(Type type, JSONSerializer<? extends JSON, ?> serializer) {
      if (null == serializerMap) {
         serializerMap = new ConcurrentHashMap();
      }

      serializerMap.put(type, serializer);
   }

   public static synchronized void put(Type type, JSONDeserializer<?> deserializer) {
      if (null == deserializerMap) {
         deserializerMap = new ConcurrentHashMap();
      }

      deserializerMap.put(type, deserializer);
   }

   public static JSONSerializer<? extends JSON, ?> getSerializer(Type type) {
      return null == serializerMap ? null : (JSONSerializer)serializerMap.get(type);
   }

   public static JSONDeserializer<?> getDeserializer(Type type) {
      return null == deserializerMap ? null : (JSONDeserializer)deserializerMap.get(type);
   }

   static {
      TemporalAccessorSerializer localDateSerializer = new TemporalAccessorSerializer(LocalDate.class);
      serializerMap.put(LocalDate.class, localDateSerializer);
      deserializerMap.put(LocalDate.class, localDateSerializer);
      TemporalAccessorSerializer localDateTimeSerializer = new TemporalAccessorSerializer(LocalDateTime.class);
      serializerMap.put(LocalDateTime.class, localDateTimeSerializer);
      deserializerMap.put(LocalDateTime.class, localDateTimeSerializer);
      TemporalAccessorSerializer localTimeSerializer = new TemporalAccessorSerializer(LocalTime.class);
      serializerMap.put(LocalTime.class, localTimeSerializer);
      deserializerMap.put(LocalTime.class, localTimeSerializer);
   }
}
