package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import java.io.Serializable;
import java.util.Map;

public class LoggerContextVO implements Serializable {
   private static final long serialVersionUID = 5488023392483144387L;
   final String name;
   final Map<String, String> propertyMap;
   final long birthTime;

   public LoggerContextVO(LoggerContext lc) {
      this.name = lc.getName();
      this.propertyMap = lc.getCopyOfPropertyMap();
      this.birthTime = lc.getBirthTime();
   }

   public LoggerContextVO(String name, Map<String, String> propertyMap, long birthTime) {
      this.name = name;
      this.propertyMap = propertyMap;
      this.birthTime = birthTime;
   }

   public String getName() {
      return this.name;
   }

   public Map<String, String> getPropertyMap() {
      return this.propertyMap;
   }

   public long getBirthTime() {
      return this.birthTime;
   }

   public String toString() {
      return "LoggerContextVO{name='" + this.name + '\'' + ", propertyMap=" + this.propertyMap + ", birthTime=" + this.birthTime + '}';
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof LoggerContextVO)) {
         return false;
      } else {
         LoggerContextVO that = (LoggerContextVO)o;
         if (this.birthTime != that.birthTime) {
            return false;
         } else {
            if (this.name != null) {
               if (!this.name.equals(that.name)) {
                  return false;
               }
            } else if (that.name != null) {
               return false;
            }

            if (this.propertyMap != null) {
               if (!this.propertyMap.equals(that.propertyMap)) {
                  return false;
               }
            } else if (that.propertyMap != null) {
               return false;
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int result = this.name != null ? this.name.hashCode() : 0;
      result = 31 * result + (this.propertyMap != null ? this.propertyMap.hashCode() : 0);
      result = 31 * result + (int)(this.birthTime ^ this.birthTime >>> 32);
      return result;
   }
}
