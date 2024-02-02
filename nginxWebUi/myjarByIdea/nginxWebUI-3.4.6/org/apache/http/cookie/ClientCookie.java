package org.apache.http.cookie;

import org.apache.http.annotation.Obsolete;

public interface ClientCookie extends Cookie {
   @Obsolete
   String VERSION_ATTR = "version";
   String PATH_ATTR = "path";
   String DOMAIN_ATTR = "domain";
   String MAX_AGE_ATTR = "max-age";
   String SECURE_ATTR = "secure";
   @Obsolete
   String COMMENT_ATTR = "comment";
   String EXPIRES_ATTR = "expires";
   @Obsolete
   String PORT_ATTR = "port";
   @Obsolete
   String COMMENTURL_ATTR = "commenturl";
   @Obsolete
   String DISCARD_ATTR = "discard";

   String getAttribute(String var1);

   boolean containsAttribute(String var1);
}
