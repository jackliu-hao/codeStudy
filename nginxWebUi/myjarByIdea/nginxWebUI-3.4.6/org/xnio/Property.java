package org.xnio;

import java.io.Serializable;
import org.xnio._private.Messages;

public final class Property implements Serializable {
   private static final long serialVersionUID = -4958518978461712277L;
   private final String key;
   private final Object value;

   private Property(String key, Object value) {
      if (key == null) {
         throw Messages.msg.nullParameter("key");
      } else if (value == null) {
         throw Messages.msg.nullParameter("value");
      } else {
         this.key = key;
         this.value = value;
      }
   }

   private Property(String key, String value) {
      this(key, (Object)value);
   }

   public String getKey() {
      return this.key;
   }

   public Object getValue() {
      return this.value;
   }

   public String toString() {
      return "(" + this.key + "=>" + this.value.toString() + ")";
   }

   public int hashCode() {
      return this.key.hashCode() * 7 + this.value.hashCode();
   }

   public boolean equals(Object obj) {
      return obj instanceof Property && this.equals((Property)obj);
   }

   public boolean equals(Property other) {
      return this.key.equals(other.key) && this.value.equals(other.value);
   }

   public static Property of(String key, Object value) {
      return new Property(key, value);
   }

   public static Property of(String key, String value) {
      return new Property(key, value);
   }
}
