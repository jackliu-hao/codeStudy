package cn.hutool.core.date.format;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

public abstract class AbstractDateBasic implements DateBasic, Serializable {
   private static final long serialVersionUID = 6333136319870641818L;
   protected final String pattern;
   protected final TimeZone timeZone;
   protected final Locale locale;

   protected AbstractDateBasic(String pattern, TimeZone timeZone, Locale locale) {
      this.pattern = pattern;
      this.timeZone = timeZone;
      this.locale = locale;
   }

   public String getPattern() {
      return this.pattern;
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof FastDatePrinter)) {
         return false;
      } else {
         AbstractDateBasic other = (AbstractDateBasic)obj;
         return this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale);
      }
   }

   public int hashCode() {
      return this.pattern.hashCode() + 13 * (this.timeZone.hashCode() + 13 * this.locale.hashCode());
   }

   public String toString() {
      return "FastDatePrinter[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
   }
}
