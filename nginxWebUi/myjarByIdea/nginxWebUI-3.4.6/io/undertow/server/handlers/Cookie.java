package io.undertow.server.handlers;

import java.util.Date;

public interface Cookie extends Comparable {
   String getName();

   String getValue();

   Cookie setValue(String var1);

   String getPath();

   Cookie setPath(String var1);

   String getDomain();

   Cookie setDomain(String var1);

   Integer getMaxAge();

   Cookie setMaxAge(Integer var1);

   boolean isDiscard();

   Cookie setDiscard(boolean var1);

   boolean isSecure();

   Cookie setSecure(boolean var1);

   int getVersion();

   Cookie setVersion(int var1);

   boolean isHttpOnly();

   Cookie setHttpOnly(boolean var1);

   Date getExpires();

   Cookie setExpires(Date var1);

   String getComment();

   Cookie setComment(String var1);

   default boolean isSameSite() {
      return false;
   }

   default Cookie setSameSite(boolean sameSite) {
      throw new UnsupportedOperationException("Not implemented");
   }

   default String getSameSiteMode() {
      return null;
   }

   default Cookie setSameSiteMode(String mode) {
      throw new UnsupportedOperationException("Not implemented");
   }

   default int compareTo(Object other) {
      Cookie o = (Cookie)other;
      int retVal = false;
      if (this.getName() == null && o.getName() != null) {
         return -1;
      } else if (this.getName() != null && o.getName() == null) {
         return 1;
      } else {
         int retVal = this.getName() == null && o.getName() == null ? 0 : this.getName().compareTo(o.getName());
         if (retVal != 0) {
            return retVal;
         } else if (this.getPath() == null && o.getPath() != null) {
            return -1;
         } else if (this.getPath() != null && o.getPath() == null) {
            return 1;
         } else {
            retVal = this.getPath() == null && o.getPath() == null ? 0 : this.getPath().compareTo(o.getPath());
            if (retVal != 0) {
               return retVal;
            } else if (this.getDomain() == null && o.getDomain() != null) {
               return -1;
            } else if (this.getDomain() != null && o.getDomain() == null) {
               return 1;
            } else {
               retVal = this.getDomain() == null && o.getDomain() == null ? 0 : this.getDomain().compareTo(o.getDomain());
               return retVal != 0 ? retVal : 0;
            }
         }
      }
   }
}
