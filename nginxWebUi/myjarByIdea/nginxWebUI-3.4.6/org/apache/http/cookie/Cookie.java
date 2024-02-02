package org.apache.http.cookie;

import java.util.Date;
import org.apache.http.annotation.Obsolete;

public interface Cookie {
   String getName();

   String getValue();

   @Obsolete
   String getComment();

   @Obsolete
   String getCommentURL();

   Date getExpiryDate();

   boolean isPersistent();

   String getDomain();

   String getPath();

   @Obsolete
   int[] getPorts();

   boolean isSecure();

   @Obsolete
   int getVersion();

   boolean isExpired(Date var1);
}
