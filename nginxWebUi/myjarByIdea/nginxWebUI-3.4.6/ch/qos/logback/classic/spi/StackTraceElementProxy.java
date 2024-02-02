package ch.qos.logback.classic.spi;

import java.io.Serializable;

public class StackTraceElementProxy implements Serializable {
   private static final long serialVersionUID = -2374374378980555982L;
   final StackTraceElement ste;
   private transient String steAsString;
   private ClassPackagingData cpd;

   public StackTraceElementProxy(StackTraceElement ste) {
      if (ste == null) {
         throw new IllegalArgumentException("ste cannot be null");
      } else {
         this.ste = ste;
      }
   }

   public String getSTEAsString() {
      if (this.steAsString == null) {
         this.steAsString = "at " + this.ste.toString();
      }

      return this.steAsString;
   }

   public StackTraceElement getStackTraceElement() {
      return this.ste;
   }

   public void setClassPackagingData(ClassPackagingData cpd) {
      if (this.cpd != null) {
         throw new IllegalStateException("Packaging data has been already set");
      } else {
         this.cpd = cpd;
      }
   }

   public ClassPackagingData getClassPackagingData() {
      return this.cpd;
   }

   public int hashCode() {
      return this.ste.hashCode();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         StackTraceElementProxy other = (StackTraceElementProxy)obj;
         if (!this.ste.equals(other.ste)) {
            return false;
         } else {
            if (this.cpd == null) {
               if (other.cpd != null) {
                  return false;
               }
            } else if (!this.cpd.equals(other.cpd)) {
               return false;
            }

            return true;
         }
      }
   }

   public String toString() {
      return this.getSTEAsString();
   }
}
