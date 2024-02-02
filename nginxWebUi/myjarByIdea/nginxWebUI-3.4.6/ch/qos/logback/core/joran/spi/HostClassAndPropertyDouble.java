package ch.qos.logback.core.joran.spi;

public class HostClassAndPropertyDouble {
   final Class<?> hostClass;
   final String propertyName;

   public HostClassAndPropertyDouble(Class<?> hostClass, String propertyName) {
      this.hostClass = hostClass;
      this.propertyName = propertyName;
   }

   public Class<?> getHostClass() {
      return this.hostClass;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public int hashCode() {
      int prime = true;
      int result = 1;
      result = 31 * result + (this.hostClass == null ? 0 : this.hostClass.hashCode());
      result = 31 * result + (this.propertyName == null ? 0 : this.propertyName.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         HostClassAndPropertyDouble other = (HostClassAndPropertyDouble)obj;
         if (this.hostClass == null) {
            if (other.hostClass != null) {
               return false;
            }
         } else if (!this.hostClass.equals(other.hostClass)) {
            return false;
         }

         if (this.propertyName == null) {
            if (other.propertyName != null) {
               return false;
            }
         } else if (!this.propertyName.equals(other.propertyName)) {
            return false;
         }

         return true;
      }
   }
}
