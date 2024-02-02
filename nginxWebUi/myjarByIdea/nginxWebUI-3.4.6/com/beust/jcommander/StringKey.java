package com.beust.jcommander;

public class StringKey implements FuzzyMap.IKey {
   private String m_name;

   public StringKey(String name) {
      this.m_name = name;
   }

   public String getName() {
      return this.m_name;
   }

   public String toString() {
      return this.m_name;
   }

   public int hashCode() {
      int prime = true;
      int result = 1;
      result = 31 * result + (this.m_name == null ? 0 : this.m_name.hashCode());
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
         StringKey other = (StringKey)obj;
         if (this.m_name == null) {
            if (other.m_name != null) {
               return false;
            }
         } else if (!this.m_name.equals(other.m_name)) {
            return false;
         }

         return true;
      }
   }
}
