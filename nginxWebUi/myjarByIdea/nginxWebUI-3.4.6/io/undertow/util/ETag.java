package io.undertow.util;

public class ETag {
   private final boolean weak;
   private final String tag;

   public ETag(boolean weak, String tag) {
      this.weak = weak;
      this.tag = tag;
   }

   public boolean isWeak() {
      return this.weak;
   }

   public String getTag() {
      return this.tag;
   }

   public String toString() {
      return this.weak ? "W/\"" + this.tag + "\"" : "\"" + this.tag + "\"";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ETag eTag = (ETag)o;
         if (this.weak != eTag.weak) {
            return false;
         } else {
            if (this.tag != null) {
               if (!this.tag.equals(eTag.tag)) {
                  return false;
               }
            } else if (eTag.tag != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.weak ? 1 : 0;
      result = 31 * result + (this.tag != null ? this.tag.hashCode() : 0);
      return result;
   }
}
