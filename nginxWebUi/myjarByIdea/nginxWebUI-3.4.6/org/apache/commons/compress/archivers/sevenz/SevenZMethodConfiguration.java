package org.apache.commons.compress.archivers.sevenz;

import java.util.Objects;

public class SevenZMethodConfiguration {
   private final SevenZMethod method;
   private final Object options;

   public SevenZMethodConfiguration(SevenZMethod method) {
      this(method, (Object)null);
   }

   public SevenZMethodConfiguration(SevenZMethod method, Object options) {
      this.method = method;
      this.options = options;
      if (options != null && !Coders.findByMethod(method).canAcceptOptions(options)) {
         throw new IllegalArgumentException("The " + method + " method doesn't support options of type " + options.getClass());
      }
   }

   public SevenZMethod getMethod() {
      return this.method;
   }

   public Object getOptions() {
      return this.options;
   }

   public int hashCode() {
      return this.method == null ? 0 : this.method.hashCode();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         SevenZMethodConfiguration other = (SevenZMethodConfiguration)obj;
         return Objects.equals(this.method, other.method) && Objects.equals(this.options, other.options);
      } else {
         return false;
      }
   }
}
