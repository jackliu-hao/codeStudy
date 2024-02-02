package javax.servlet.http;

import java.util.Enumeration;

/** @deprecated */
@Deprecated
public interface HttpSessionContext {
   /** @deprecated */
   @Deprecated
   HttpSession getSession(String var1);

   /** @deprecated */
   @Deprecated
   Enumeration<String> getIds();
}
