package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicCommentHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler {
   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      cookie.setComment(value);
   }

   public String getAttributeName() {
      return "comment";
   }
}
